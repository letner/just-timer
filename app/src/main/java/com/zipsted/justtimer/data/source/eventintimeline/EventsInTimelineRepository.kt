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

class EventsInTimelineRepository(private val local: EventsInTimelineDataSource) : EventsInTimelineDataSource {

    override fun loadEventsForTimeline(timeline: Timeline, callback: LoadCallback<TimelinedEvent>) {
        local.loadEventsForTimeline(timeline, callback)
    }

    override fun delete(id: String) {
        local.delete(id)
    }

    override fun add(eventInTimeline: EventInTimeline) {
        local.add(eventInTimeline)
    }

    override fun getItemByOrder(position: Int, callback: GetItemCallback<EventInTimeline>) {
        local.getItemByOrder(position, callback)
    }

    override fun updateOrder(fromPosition: Int, toPosition: Int) {
        local.updateOrder(fromPosition, toPosition)
    }

    override fun updateOrder(id: String, position: Int) {
        local.updateOrder(id, position)
    }

    override fun deleteByPosition(position: Int) {
        local.deleteByPosition(position)
    }

    override fun deleteByEventId(eventId: String) {
        local.deleteByEventId(eventId)
    }

    companion object {
        private var INSTANCE: EventsInTimelineRepository? = null

        fun getInstance(local: EventsInTimelineDataSource): EventsInTimelineRepository {
            return INSTANCE
                    ?: EventsInTimelineRepository(local).apply {
                        INSTANCE = this
                    }
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

}