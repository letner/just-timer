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

package com.zipsted.justtimer.back

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.timeline.TimelineActivity
import com.zipsted.justtimer.timeline.TimelineActivity.Companion.CHANNEL_ID
import com.zipsted.justtimer.util.duration
import java.util.*
import kotlin.collections.ArrayList

class CountdownService : Service() {

    private lateinit var messenger: Messenger
    private val clients = ArrayList<Messenger>()

    private lateinit var events: ArrayList<TimelinedEvent>
    private var timer: Timer? = null

    private var isActivityForeground = true

    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent): IBinder? {
        messenger = Messenger(IncomingHandler(this))
        events = intent.getParcelableArrayListExtra<TimelinedEvent>(CountdownService.EXTRA_EVENTS_ARRAY)
        val timelineIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, TimelineActivity::class.java),
                0)
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_transparent)
                .setContentTitle("Just Timer")
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setColorized(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true)
                .setContentIntent(timelineIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
        return messenger.binder
    }

    fun sendSeconds(seconds: Int) {
        for (client in clients) {
            val msg = Message.obtain(null, MSG_DURATION, seconds, 0)
            client.send(msg)
        }
    }

    fun sendEventIsDone(size: Int) {
        for (client in clients) {
            val msg = Message.obtain(null, MSG_EVENT_IS_DONE, size, 0)
            client.send(msg)
        }
    }

    fun sendTimelineIsDone() {
        for (client in clients) {
            val msg = Message.obtain(null, MSG_TIMELINE_IS_DONE)
            client.send(msg)
        }
    }

    fun startNextEventCountdown() {
        if (events.isNotEmpty()) {
            startCountdownForEvent(events[0])
            events.removeAt(0)
        } else {
            sendTimelineIsDone()
        }
    }

    fun stopCountdown() {
        timer?.cancel()
        timer = null

        hideNotification()
    }

    private class IncomingHandler(private val service: CountdownService) : Handler() {

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_START_COUNTDOWN -> {
                    service.clients.add(msg.replyTo)
                    service.startNextEventCountdown()
                }
                MSG_STOP_COUNTDOWN -> {
                    service.stopCountdown()
                    service.clients.remove(msg.replyTo)
                }
                MSG_ACTIVITY_IS_ACTIVE -> service.hideNotification()
                MSG_ACTIVITY_IS_INACTIVE -> service.isActivityForeground = false
            }
        }
    }

    private interface CountdownCallback {

        fun onTick(min: Int, sec: Int)

        fun onEventDone()

        fun onTimelineDone()
    }

    private class CountdownTask(var min: Int, var sec: Int, val callback: CountdownCallback) : TimerTask() {

        override fun run() {
            sec -= 1
            min = if (sec < 0) {
                sec = 59
                min - 1
            } else {
                min
            }
            if (min + sec == 0) {
                callback.onEventDone()
                cancel()
            } else {
                callback.onTick(min, sec)
            }
        }
    }

    private fun startCountdownForEvent(event: TimelinedEvent) {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(this, ringtoneUri)
        ringtone.play()

        val min = event.min
        val sec = event.sec

        val countdownTask = CountdownTask(min, sec, object : CountdownCallback {
            override fun onTick(min: Int, sec: Int) {
                sendSeconds(min * 60 + sec)
                if (!isActivityForeground) {
                    updateNotification(event.name, Pair(min, sec).duration())
                }
            }

            override fun onEventDone() {
                sendEventIsDone(events.size)
                if (!isActivityForeground) {
                    updateNotification(event.name, Pair(0, 0).duration())
                }
                startNextEventCountdown()
            }

            override fun onTimelineDone() {
                sendTimelineIsDone()
            }
        })
        timer = Timer()
        timer?.scheduleAtFixedRate(countdownTask, 1000, 1000)
    }

    private fun updateNotification(name: String, duration: String) {
        notificationBuilder
                .setContentText("$name: $duration")
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText("$name: $duration"))

        with(NotificationManagerCompat.from(this)) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun hideNotification() {
        isActivityForeground = true
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
    }

    companion object {
        const val MSG_START_COUNTDOWN = 1
        const val MSG_STOP_COUNTDOWN = 2
        const val MSG_ACTIVITY_IS_ACTIVE = 3
        const val MSG_ACTIVITY_IS_INACTIVE = 4
        const val MSG_DURATION = 7
        const val MSG_EVENT_IS_DONE = 8
        const val MSG_TIMELINE_IS_DONE = 9

        const val EXTRA_EVENTS_ARRAY = "EVENTS_ARRAY"

        const val NOTIFICATION_ID = 1
    }

}