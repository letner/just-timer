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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import com.zipsted.justtimer.R
import com.zipsted.justtimer.back.CountdownService
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.data.source.event.EventsRepository
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineRepository
import com.zipsted.justtimer.data.source.timeline.TimelinesRepository

class TimelinePresenter(
        private val eventsRepository: EventsRepository,
        private val timelinesRepository: TimelinesRepository,
        private val eventsInTimelineRepository: EventsInTimelineRepository,
        private val context: Context,
        val timelineView: TimelineContract.View) : TimelineContract.Presenter {

    init {
        timelineView.presenter = this
    }

    private var isTimelineEmpty = true
    private lateinit var timeline: Timeline

    //TODO: Have to move events from here and use repository cache instead.
    private var events: ArrayList<TimelinedEvent> = ArrayList()

    override var isPlaying = false
        set(isPlaying) {
            if (!isTimelineEmpty) {
                field = isPlaying
                switchMode(isPlaying)
            } else {
                timelineView.message(R.string.fill_timeline_first)
            }
        }

    private var isBound = false
    private var service: Messenger? = null

    private var messenger: Messenger = Messenger(IncomingHandler(this))

    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName) {
            service = null
            isBound = false
        }

        override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
            service = Messenger(binder)
            try {
                val msg = Message.obtain(null, CountdownService.MSG_START_COUNTDOWN)
                msg.replyTo = messenger
                service?.send(msg)
            } catch (e: RemoteException) {
                println("ERROR_TIMER: $e")
            }
            isBound = true
        }

    }

    class IncomingHandler(private val presenter: TimelinePresenter) : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                CountdownService.MSG_DURATION -> presenter.updateCountdown(msg.arg1 / 60, msg.arg1 % 60)
                CountdownService.MSG_EVENT_IS_DONE -> presenter.currentEventIsDone(msg.arg1)
                CountdownService.MSG_TIMELINE_IS_DONE -> presenter.isPlaying = false
                else -> super.handleMessage(msg)
            }
        }
    }



    override fun loadEvents() {
        eventsRepository.getAll(object : LoadCallback<Event> {
            override fun onLoaded(items: List<Event>) {
                if (!timelineView.isActive) {
                    return
                }

                if (items.isEmpty()) {
                    timelineView.showNoEvents()
                } else {
                    timelineView.showEvents(items)
                }
            }

            override fun onDataNotAvailable() {
                timelineView.showNoEvents()
            }

        })
    }

    override fun loadTimeline() {
        timelinesRepository.getOne(object : GetItemCallback<Timeline> {
            override fun onGetItem(item: Timeline) {
                timeline = item
                eventsInTimelineRepository.loadEventsForTimeline(item, object : LoadCallback<TimelinedEvent> {
                    override fun onDataNotAvailable() {
                        isTimelineEmpty = true
                        timelineView.showTimeline(null)
                    }

                    override fun onLoaded(items: List<TimelinedEvent>) {
                        isTimelineEmpty = false
                        events = items as ArrayList<TimelinedEvent>
                        timelineView.showTimeline(items)
                    }
                })
            }

            override fun onDataNotAvailable() {
                saveTimeline(Timeline("Timeline"))
            }
        })
    }

    override fun saveTimeline(timeline: Timeline) {
        timelinesRepository.save(timeline)
    }

    override fun addEventToTimeline(event: Event, position: Int) {
        val eventInTimeline = EventInTimeline(position, event.id, timeline.id)
        eventsInTimelineRepository.add(eventInTimeline)
        //TODO: Replace the code below with a cache
        loadTimeline()
    }

    override fun updateEventInTimeline(event: TimelinedEvent) {
        eventsInTimelineRepository.add(event.toEventInTimeline())
    }

    override fun saveEventsForTimeline(events: List<TimelinedEvent>) {
        for (sourceEvent in events) {
            eventsInTimelineRepository.add(sourceEvent.toEventInTimeline())
        }
    }

    override fun deleteEventFromTimeline(event: TimelinedEvent) {
        eventsInTimelineRepository.delete(event.id)
        //TODO: Replace the code below with a cache
        loadTimeline()
    }

    override fun deleteEventFromTimeline(position: Int, size: Int) {
        isTimelineEmpty = events.isEmpty()
        eventsInTimelineRepository.deleteByPosition(position)
        for (i in position..size) {
            eventsInTimelineRepository.updateOrder(i, i - 1)
        }
    }

    override fun reorderEventsInTimeline(fromPosition: Int, toPosition: Int) {
        val step = if (fromPosition > toPosition) -1 else 1
        val thirdVariable = Int.MIN_VALUE

        eventsInTimelineRepository.updateOrder(toPosition, thirdVariable)
        for (i in fromPosition toward toPosition) {
            eventsInTimelineRepository.updateOrder(i, i + step)
        }
        eventsInTimelineRepository.updateOrder(thirdVariable, fromPosition)
    }

    override fun start() {
        if (!isPlaying) {
            loadEvents()
            loadTimeline()
        }
    }

    override fun stopCountdown() {
        if (isPlaying) {
            isPlaying = false
        }
    }

    fun activityIsInactive() {
        if (!isBound) return

        val message = Message.obtain(null, CountdownService.MSG_ACTIVITY_IS_INACTIVE)
        try {
            service?.send(message)
        } catch (e: RemoteException) {
            println("ERROR_TIMER: Error during receiving control from service: " + e.toString())
        }
    }

    fun activityIsActive() {
        if (!isBound) return

        val message = Message.obtain(null, CountdownService.MSG_ACTIVITY_IS_ACTIVE)
        try {
            service?.send(message)
        } catch (e: RemoteException) {
            println("ERROR_TIMER: Error during send control to service: " + e.toString())
        }
    }

    fun updateCountdown(min: Int, sec: Int) {
        timelineView.updateCountdown(min, sec)
    }

    fun currentEventIsDone(size: Int) {
        for (i in 0 until (events.size - size)) {
            events.removeAt(0)
        }
        timelineView.currentEventIsDone(events.size)
    }

    private fun bindService() {
        Intent(context, CountdownService::class.java).also {
            it.putExtra(CountdownService.EXTRA_EVENTS_ARRAY, events)
            context.bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindService(context: Context) {
        if (isBound) {
            val msg = Message.obtain(null, CountdownService.MSG_STOP_COUNTDOWN)
            msg.replyTo = messenger
            service?.send(msg)

            context.unbindService(connection)
            isBound = false
        }
    }

    private fun switchMode(isPlaying: Boolean) {
        if (isPlaying) {
            bindService()
            timelineView.switchToPlayMode()
        } else {
            unbindService(context)
            timelineView.switchToEditMode()
        }
    }

    private infix fun Int.toward(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to - step, step)
    }

}