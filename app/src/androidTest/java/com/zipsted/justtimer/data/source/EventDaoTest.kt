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
import com.zipsted.justtimer.data.model.Event
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class EventDaoTest {

    private lateinit var database: TimerDatabase

    @Before fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                TimerDatabase::class.java).build()
    }

    @After fun closeDb() = database.close()

    @Test fun insertEventAndGetById() {
        database.eventDao().insertEvent(MOCK_1_EVENT)
        val loadedEvent = database.eventDao().getEvent(MOCK_1_ID)
        assertEvent(loadedEvent, MOCK_1_ID, MOCK_1_NAME, MOCK_1_MIN, MOCK_1_SEC)
    }

    @Test fun insertEventWithReplacement() {
        database.eventDao().insertEvent(MOCK_1_EVENT)

        val updatedEvent = Event(MOCK_2_NAME, MOCK_2_MIN, MOCK_2_SEC, MOCK_1_ID)
        database.eventDao().insertEvent(updatedEvent)

        val loadedEvent = database.eventDao().getEvent(MOCK_1_ID)
        assertEvent(loadedEvent, MOCK_1_ID, MOCK_2_NAME, MOCK_2_MIN, MOCK_2_SEC)
    }

    @Test fun getEvents() {
        database.eventDao().insertEvent(MOCK_1_EVENT)

        val events = database.eventDao().getEvents()

        assertThat(events.size, `is`(1))
        assertEvent(events[0], MOCK_1_ID, MOCK_1_NAME, MOCK_1_MIN, MOCK_1_SEC)
    }

    @Test fun deleteEvent() {
        database.eventDao().insertEvent(MOCK_1_EVENT)

        database.eventDao().deleteEvent(MOCK_1_ID)
        val events = database.eventDao().getEvents()

        assertThat(events.size, `is`(0))
    }

    private fun assertEvent(
            event: Event?,
            id: String,
            name: String,
            min: Int,
            sec: Int
    ) {
        assertThat<Event>(event as Event, notNullValue())
        assertThat(event.id, `is`(id))
        assertThat(event.name, `is`(name))
        assertThat(event.min, `is`(min))
        assertThat(event.sec, `is`(sec))
    }

    companion object {
        private const val MOCK_1_ID = "id"
        private const val MOCK_1_NAME = "First event"
        private const val MOCK_1_MIN = 1
        private const val MOCK_1_SEC = 0
        private val MOCK_1_EVENT = Event(MOCK_1_NAME, MOCK_1_MIN, MOCK_1_SEC, MOCK_1_ID)

        private const val MOCK_2_NAME = "Second event"
        private const val MOCK_2_MIN = 2
        private const val MOCK_2_SEC = 25

    }

}