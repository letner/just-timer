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

package com.zipsted.justtimer.timeline

import com.zipsted.justtimer.BasePresenter
import com.zipsted.justtimer.BaseView
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent

interface TimelineContract {

    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun showEvents(events: List<Event>)

        fun showTimeline(events: List<TimelinedEvent>)

        fun showEmptyTimeline()

        fun showNoEvents()

        fun showTerribleError()

        fun switchToPlayMode()

        fun switchToEditMode()

        fun updateCountdown(min: Int, sec: Int)

        fun currentEventIsDone(size: Int)

        fun message(text: Int)
    }

    interface Presenter : BasePresenter {
        var isPlaying: Boolean

        fun loadTimeline()

        fun loadEvents()

        fun saveTimeline(timeline: Timeline)

        fun addEventToTimeline(event: Event, position: Int)

        fun updateEventInTimeline(event: TimelinedEvent)

        fun saveEventsForTimeline(events: List<TimelinedEvent>)

        fun deleteEventFromTimeline(event: TimelinedEvent)

        fun deleteEventFromTimeline(position: Int, size: Int)

        fun reorderEventsInTimeline(fromPosition: Int, toPosition: Int)

        fun stopCountdown()
    }
}