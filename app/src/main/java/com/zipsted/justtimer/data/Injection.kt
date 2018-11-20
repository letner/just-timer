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

package com.zipsted.justtimer.data

import android.content.Context
import com.zipsted.justtimer.data.source.*
import com.zipsted.justtimer.data.source.event.EventsLocalDataSource
import com.zipsted.justtimer.data.source.event.EventsRepository
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineLocalDataSource
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineRepository
import com.zipsted.justtimer.data.source.timeline.TimelinesLocalDataSource
import com.zipsted.justtimer.data.source.timeline.TimelinesRepository
import com.zipsted.justtimer.util.AppExecutors

object Injection {
    fun provideEventsRepository(context: Context): EventsRepository {
        val database = TimerDatabase.getInstance(context)
        return EventsRepository.getInstance(
                EventsLocalDataSource.getInstance(AppExecutors(), database.eventDao())
        )
    }

    fun provideTimelinesRepository(context: Context): TimelinesRepository {
        val database = TimerDatabase.getInstance(context)
        return TimelinesRepository.getInstance(
                TimelinesLocalDataSource.getInstance(AppExecutors(), database.timelineDao())
        )
    }

    fun provideEventsInTimelineRepository(context: Context): EventsInTimelineRepository {
        val database = TimerDatabase.getInstance(context)
        return EventsInTimelineRepository.getInstance(
                EventsInTimelineLocalDataSource.getInstance(AppExecutors(), database.eventInTimelineDao())
        )
    }
}