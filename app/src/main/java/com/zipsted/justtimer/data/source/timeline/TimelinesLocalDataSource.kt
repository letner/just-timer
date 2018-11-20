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

package com.zipsted.justtimer.data.source.timeline

import android.support.annotation.VisibleForTesting
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.source.GetItemCallback
import com.zipsted.justtimer.data.source.LoadCallback
import com.zipsted.justtimer.util.AppExecutors

class TimelinesLocalDataSource private constructor(
        private val appExecutors: AppExecutors,
        private val timelineDao: TimelineDao
) : TimelinesDataSource {
    override fun getOne(callback: GetItemCallback<Timeline>) {
        appExecutors.diskIO.execute {
            val timelines = timelineDao.getTimelines()
            if (timelines.isEmpty()) {
                callback.onDataNotAvailable()
            } else {
                callback.onGetItem(timelines[0])
            }
        }
    }

    override fun getAll(callback: LoadCallback<Timeline>) {
        appExecutors.diskIO.execute {
            val timelines = timelineDao.getTimelines()
            appExecutors.mainThread.execute {
                if (timelines.isNotEmpty()) {
                    callback.onLoaded(timelines)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun get(id: String, callback: GetItemCallback<Timeline>) {
        appExecutors.diskIO.execute {
            val timeline = timelineDao.getTimeline(id)
            appExecutors.mainThread.execute {
                if (timeline != null) {
                    callback.onGetItem(timeline)
                } else {
                    callback.onDataNotAvailable()
                }
            }
        }
    }

    override fun save(timeline: Timeline) {
        appExecutors.diskIO.execute {
            timelineDao.insertTimeLine(timeline)
        }

    }

    companion object {
        private var INSTANCE: TimelinesLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, timelineDao: TimelineDao): TimelinesLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TimelinesLocalDataSource::javaClass) {
                    INSTANCE = TimelinesLocalDataSource(appExecutors, timelineDao)
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