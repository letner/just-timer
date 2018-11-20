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

package com.zipsted.justtimer.data.source.event

import android.support.annotation.VisibleForTesting
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.util.AppExecutors

class EventsLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val eventDao: EventDao
) : EventsDataSource {

    override fun getAll(callback: LoadCallback<Event>) {
        appExecutors.diskIO.execute {
            val events = eventDao.getEvents()
            appExecutors.mainThread.execute {
                if (!events.isEmpty()) {
                    callback.onLoaded(events)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun get(eventId: String, callback: GetItemCallback<Event>) {
        appExecutors.diskIO.execute {
            val event = eventDao.getEvent(eventId)
            if (event != null) {
                callback.onGetItem(event)
            } else {
                callback.onDataNotAvailable()
            }
        }
    }

    override fun saveEvent(event: Event) {
        appExecutors.diskIO.execute { eventDao.insertEvent(event) }
    }

    override fun delete(event: Event) {
        appExecutors.diskIO.execute { eventDao.deleteEvent(event) }
    }

    override fun delete(id: String) {
        appExecutors.diskIO.execute { eventDao.deleteEvent(id) }
    }

    companion object {
        private var INSTANCE: EventsLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, eventDao: EventDao): EventsLocalDataSource {
            if (INSTANCE == null) {
                synchronized(EventsLocalDataSource::javaClass) {
                    INSTANCE = EventsLocalDataSource(appExecutors, eventDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}