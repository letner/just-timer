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

import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback

interface EventsInTimelineDataSource {

    fun loadEventsForTimeline(timeline: Timeline, callback: LoadCallback<TimelinedEvent>)

    fun delete(id: String)

    fun add(eventInTimeline: EventInTimeline)

    fun getItemByOrder(position: Int, callback: GetItemCallback<EventInTimeline>)

    fun updateOrder(fromPosition: Int, toPosition: Int)

    fun updateOrder(id: String, position: Int)

    fun deleteByPosition(position: Int)

    fun deleteByEventId(eventId: String)
}