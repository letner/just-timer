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

package com.zipsted.justtimer.events

import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.data.source.event.EventsRepository

class EventsPresenter(
        private val eventsRepository: EventsRepository,
        private val eventsView: EventsContract.View) : EventsContract.Presenter {

    init {
        eventsView.presenter = this
    }

    override fun loadEvents() {
        eventsRepository.getAll( object : LoadCallback<Event> {
            override fun onLoaded(events: List<Event>) {
                if (!eventsView.isActive) {
                    return
                }

                if (events.isEmpty()) {
                    eventsView.showNoEvents()
                } else {
                    eventsView.showEvents(events)
                }
            }

            override fun onDataNotAvailable() {
                eventsView.showNoEvents()
            }

        })
    }

    override fun addNewEvent() {
        eventsView.showAddEvent()
    }

    override fun editEvent(event: Event) {
        eventsView.showEditEvent(event)
    }

    override fun start() {
        loadEvents()
    }
}