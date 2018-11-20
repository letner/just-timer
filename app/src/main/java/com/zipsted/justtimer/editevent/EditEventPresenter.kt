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

package com.zipsted.justtimer.editevent

import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.data.source.event.EventsRepository
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineRepository
import com.zipsted.justtimer.data.source.timeline.TimelinesRepository

class EditEventPresenter(
        private val eventsRepository: EventsRepository,
        private val eventsInTimelineRepository: EventsInTimelineRepository,
        private val timelinesRepository: TimelinesRepository,
        private val eventId: String?,
        private val editEventView: EditEventContract.View,
        override var isDataMissing: Boolean
) : EditEventContract.Presenter, GetItemCallback<Event> {

    init {
        editEventView.presenter = this
    }

    override fun saveEvent(name: String, min: Int, sec: Int) {
        if (eventId == null) {
            createEvent(name, min, sec)
        } else {
            updateEvent(name, min, sec)
        }
    }

    override fun deleteEvent(id: String) {
        eventsRepository.delete(id)
        eventsInTimelineRepository.deleteByEventId(id)

        timelinesRepository.getAll(object : LoadCallback<Timeline> {
            override fun onDataNotAvailable() {
                println("DEBUG_TIMER: Timelines are unavailable!")
            }

            override fun onLoaded(items: List<Timeline>) {
                for (item in items) {
                    eventsInTimelineRepository.loadEventsForTimeline(item, object : LoadCallback<TimelinedEvent> {
                        override fun onDataNotAvailable() {
                            println("DEBUG_TIMER: Events in timeline are unavailable!")
                        }

                        override fun onLoaded(items: List<TimelinedEvent>) {
                            items.forEachIndexed { index, timelinedEvent ->
                                eventsInTimelineRepository.updateOrder(timelinedEvent.id, index)
                            }
                        }

                    })
                }
            }
        })
    }

    override fun populateEvent() {
        if (eventId == null) {
            throw RuntimeException("populateEvent() was called but the event is new")
        }
        eventsRepository.get(eventId, this)
    }

    override fun start() {
        if (eventId != null && isDataMissing) {
            populateEvent()
            editEventView.hideDeleteButton(false)
        } else {
            editEventView.hideDeleteButton(true)
        }
    }

    override fun changeDuration(min: Int, sec: Int) {
        editEventView.setDuration(min, sec)
    }

    override fun onGetItem(event: Event) {
        if (editEventView.isActive) {
            editEventView.setName(event.name)
            editEventView.setDuration(event.min, event.sec)
        }
        isDataMissing = false
    }

    override fun onDataNotAvailable() {
        if (editEventView.isActive) {
            editEventView.showEmptyEventError()
        }
    }

    private fun createEvent(name: String, min: Int, sec: Int) {
        val event = Event(name, min, sec)
        if (event.isEmpty) {
            editEventView.showEmptyEventError()
        } else {
            eventsRepository.saveEvent(event)
            editEventView.showEventsList()
        }
    }

    private fun updateEvent(name: String, min: Int, sec: Int) {
        if (eventId == null) {
            throw RuntimeException("updateEvent() was called but the event is new")
        }
        eventsRepository.saveEvent(Event(name, min, sec, eventId))
        editEventView.showEventsList()
    }

}