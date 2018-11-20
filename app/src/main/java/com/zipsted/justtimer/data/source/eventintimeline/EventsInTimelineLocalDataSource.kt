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

package com.zipsted.justtimer.data.source.eventintimeline

import android.support.annotation.VisibleForTesting
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.util.AppExecutors

class EventsInTimelineLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val eventInTimelineDao: EventInTimelineDao
): EventsInTimelineDataSource {

    override fun loadEventsForTimeline(timeline: Timeline, callback: LoadCallback<TimelinedEvent>) {
        appExecutors.diskIO.execute {
            val eventsInTimeline = eventInTimelineDao.getEventsForTimeline(timeline.id)
            appExecutors.mainThread.execute {
                if (eventsInTimeline.isNotEmpty()) {
                    callback.onLoaded(eventsInTimeline)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun delete(id: String) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.delete(id)
        }
    }

    override fun add(eventInTimeline: EventInTimeline) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.insert(eventInTimeline)
        }
    }

    override fun getItemByOrder(position: Int, callback: GetItemCallback<EventInTimeline>) {
        appExecutors.diskIO.execute {
            val event = eventInTimelineDao.getItemByOrder(position)
            appExecutors.mainThread.execute {
                if (event != null) {
                    callback.onGetItem(event)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun updateOrder(fromPosition: Int, toPosition: Int) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.updateOrder(fromPosition, toPosition)
        }
    }

    override fun updateOrder(id: String, position: Int) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.updateOrder(id, position)
        }
    }

    override fun deleteByPosition(position: Int) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.deleteByPosition(position)
        }
    }

    override fun deleteByEventId(eventId: String) {
        appExecutors.diskIO.execute {
            eventInTimelineDao.deleteByEventId(eventId)
        }
    }

    companion object {
        private var INSTANCE: EventsInTimelineLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, eventInTimelineDao: EventInTimelineDao): EventsInTimelineLocalDataSource {
            if (INSTANCE == null) {
                synchronized(EventsInTimelineLocalDataSource::javaClass) {
                    INSTANCE = EventsInTimelineLocalDataSource(appExecutors, eventInTimelineDao)
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