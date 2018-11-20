/*
 * Copyright (c) 2018, Ed Letner.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zipsted.justtimer.data.source

import com.zipsted.justtimer.any
import com.zipsted.justtimer.capture
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.source.event.EventsDataSource
import com.zipsted.justtimer.data.source.event.EventsRepository
import com.zipsted.justtimer.eq
import org.hamcrest.CoreMatchers.`is`
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.*
import org.mockito.Mockito.verify

class EventsRepositoryTest {

    private val EVENT_NAME_1 = "Event 1"
    private val EVENT_NAME_2 = "Event 2"
    private val EVENT_NAME_3 = "Event 3"

    private val EVENTS = arrayListOf(Event(EVENT_NAME_1), Event(EVENT_NAME_2), Event(EVENT_NAME_3))

    private lateinit var eventsRepository: EventsRepository

    @Mock private lateinit var localDataSource: EventsDataSource
    @Mock private lateinit var getEventCallback: GetItemCallback<Event>
    @Mock private lateinit var loadEventsCallback: LoadCallback<Event>

    @Captor private lateinit var eventsCallbackCaptor: ArgumentCaptor<LoadCallback<Event>>
    @Captor private lateinit var eventCallbackCaptor: ArgumentCaptor<GetItemCallback<Event>>

    @Before fun setupEventsRepository() {
        MockitoAnnotations.initMocks(this)

        eventsRepository = EventsRepository.getInstance(localDataSource)
    }

    @After fun destroyRepositoryInstance() {
        EventsRepository.destroyInstance()
    }

    @Test fun `get all events`() {
        eventsRepository.getAll(loadEventsCallback)
        verify(localDataSource).getAll(any())
    }

    @Test fun `get one event`() {
        eventsRepository.get(EVENT_NAME_1, getEventCallback)
        verify(localDataSource).get(eq(EVENT_NAME_1), any())
    }

    @Test fun `save and delete events`() {
        with(eventsRepository) {
            // saving part
            val newEvent1 = Event(EVENT_NAME_1)
            saveEvent(newEvent1)
            verify(localDataSource).saveEvent(newEvent1)
            val newEvent2 = Event(EVENT_NAME_2)
            saveEvent(newEvent2)
            verify(localDataSource).saveEvent(newEvent2)
            val newEvent3 = Event(EVENT_NAME_3)
            saveEvent(newEvent3)
            verify(localDataSource).saveEvent(newEvent3)

            assertThat(cachedEvents.size, `is`(3))
            val event = cachedEvents[newEvent1.id]
            assertNotNull(event as Event)
            assertThat(event.name, `is`(EVENT_NAME_1))

            // delete part
            delete(newEvent1)
            verify(localDataSource).delete(newEvent1)
            delete(newEvent3)
            verify(localDataSource).delete(newEvent3)

            assertThat(cachedEvents.size, `is`(1))
            val cachedEvent = cachedEvents[newEvent2.id]
            assertNotNull(cachedEvent as Event)
            assertThat(cachedEvent.name, `is`(EVENT_NAME_2))
        }
    }

    @Test fun `event is not available`() {
        val id = "6882"
        eventsRepository.get(id, getEventCallback)
        verify(localDataSource).get(eq(id), capture(eventCallbackCaptor))
        eventCallbackCaptor.value.onDataNotAvailable()
    }

    @Test fun `events are available`() {
        eventsRepository.getAll(loadEventsCallback)
        verify(localDataSource).getAll(capture(eventsCallbackCaptor))
        eventsCallbackCaptor.value.onLoaded(EVENTS)
    }
}