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

class EventsRepository(private val local: EventsDataSource) : EventsDataSource {
    var cachedEvents: LinkedHashMap<String, Event> = LinkedHashMap()

    override fun getAll(callback: LoadCallback<Event>) {
        if (cachedEvents.isNotEmpty()) {
            callback.onLoaded(cachedEvents.values.toList())
            return
        }

        local.getAll(object : LoadCallback<Event> {
            override fun onLoaded(events: List<Event>) {
                refreshCache(events)
                callback.onLoaded(events)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun get(eventId: String, callback: GetItemCallback<Event>) {
        val eventInCache = cachedEvents[eventId]

        if (eventInCache != null) {
            callback.onGetItem(eventInCache)
            return
        }

        local.get(eventId, object : GetItemCallback<Event> {
            override fun onGetItem(event: Event) {
                cacheEvent(event) {
                    callback.onGetItem(event)
                }
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override fun saveEvent(event: Event) {
        cacheEvent(event) {
            local.saveEvent(event)
        }
    }

    override fun delete(event: Event) {
        local.delete(event)
        cachedEvents.remove(event.id)
    }

    override fun delete(id: String) {
        local.delete(id)
        cachedEvents.remove(id)
    }

    private fun refreshCache(events: List<Event>) {
        cachedEvents.clear()
        events.forEach { event ->
            cacheEvent(event) {}
        }
    }

    private fun cacheEvent(event: Event, perform: (Event) -> Unit) {
        val cachedEvent = Event(event.name, event.min, event.sec, event.id)
        cachedEvents[cachedEvent.id] = cachedEvent
        perform(cachedEvent)
    }

    companion object {
        private var INSTANCE: EventsRepository? = null

        fun getInstance(local: EventsDataSource): EventsRepository {
            return INSTANCE
                    ?: EventsRepository(local).apply {
                INSTANCE = this
            }
        }

        @VisibleForTesting fun destroyInstance() {
            INSTANCE = null
        }
    }
}