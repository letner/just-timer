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

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) class EventsInTimelineDaoTest {

    private lateinit var database: TimerDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                TimerDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertEventInTimelineAndGetByOrder() {
        database.eventInTimelineDao().insert(MOCK_1_EVENT_IN_TIMELINE)
        val loadedEvent = database.eventInTimelineDao().getItemByOrder(MOCK_1_ORDER)
        assertEvent(loadedEvent, MOCK_1_ID, MOCK_1_EVENT_ID, MOCK_1_TIMELINE_ID)
    }

    @Test
    fun insertForTwoTimelinesAndDeleteByEventId() {
        insertThreeEventsInTimeline()

        val eventsInParticularTimeline = database.eventInTimelineDao().getEventsForTimeline(MOCK_1_TIMELINE_ID)
        assertThat(eventsInParticularTimeline.size, `is`(2))

        database.eventInTimelineDao().deleteByEventId(MOCK_1_EVENT_ID)
        val events = database.eventInTimelineDao().getEventsForTimeline(MOCK_1_TIMELINE_ID)
        assertThat(events.size, `is`(1))
        assertThat(events[0].id, `is`(MOCK_3_ID))
    }

    @Test
    fun updateOrder() {
        insertThreeEventsInTimeline()

        database.eventInTimelineDao().updateOrder(MOCK_1_ORDER, THIRD_VARIABLE_ORDER)
        database.eventInTimelineDao().updateOrder(MOCK_3_ORDER, MOCK_1_ORDER)
        database.eventInTimelineDao().updateOrder(THIRD_VARIABLE_ORDER, MOCK_3_ORDER)
        val event = database.eventInTimelineDao().getItemByOrder(MOCK_1_ORDER)

        assertEvent(event, MOCK_3_ID, MOCK_2_EVENT_ID, MOCK_1_TIMELINE_ID)
    }

    private fun assertEvent(
            event: EventInTimeline?,
            id: String,
            eventId: String,
            timelineId: String
    ) {
        assertThat<EventInTimeline>(event as EventInTimeline, notNullValue())
        assertThat(event.id, `is`(id))
        assertThat(event.eventId, `is`(eventId))
        assertThat(event.timelineId, `is`(timelineId))
    }

    private fun insertThreeEventsInTimeline() {
        val event1 = Event(MOCK_1_NAME, 1, 0, MOCK_1_EVENT_ID)
        val event2 = Event(MOCK_2_NAME, 0, 45, MOCK_2_EVENT_ID)
        database.eventDao().insertEvent(event1)
        database.eventDao().insertEvent(event2)
        val eventInTimeline1 = EventInTimeline(MOCK_1_ORDER, MOCK_1_EVENT_ID, MOCK_1_TIMELINE_ID, MOCK_1_ID)
        val eventInTimeline2 = EventInTimeline(MOCK_2_ORDER, MOCK_1_EVENT_ID, MOCK_2_TIMELINE_ID, MOCK_2_ID)
        val eventInTimeline3 = EventInTimeline(MOCK_3_ORDER, MOCK_2_EVENT_ID, MOCK_1_TIMELINE_ID, MOCK_3_ID)
        database.eventInTimelineDao().insert(eventInTimeline1)
        database.eventInTimelineDao().insert(eventInTimeline2)
        database.eventInTimelineDao().insert(eventInTimeline3)
    }

    companion object {
        private const val MOCK_1_ID = "id"
        private const val MOCK_1_EVENT_ID = "event id"
        private const val MOCK_1_TIMELINE_ID = "timeline id"
        private const val MOCK_1_ORDER = 0
        private const val MOCK_1_NAME = "event name"
        private val MOCK_1_EVENT_IN_TIMELINE =
                EventInTimeline(MOCK_1_ORDER, MOCK_1_EVENT_ID, MOCK_1_TIMELINE_ID, MOCK_1_ID)

        private const val MOCK_2_ID = "id 2"
        private const val MOCK_2_EVENT_ID = "event 2 id"
        private const val MOCK_2_TIMELINE_ID = "timeline 2 id"
        private const val MOCK_2_ORDER = 1
        private const val MOCK_2_NAME = "event 2 name"

        private const val MOCK_3_ID = "id 3"
        private const val MOCK_3_ORDER = 2

        private const val THIRD_VARIABLE_ORDER = 1234
    }

}