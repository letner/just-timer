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

class TimelinesRepository(private val local: TimelinesDataSource) : TimelinesDataSource {

    override fun getOne(callback: GetItemCallback<Timeline>) {
        local.getOne(callback)
    }

    override fun getAll(callback: LoadCallback<Timeline>) {
        local.getAll(callback)
    }

    override fun get(id: String, callback: GetItemCallback<Timeline>) {
        TODO("not implemented")
    }

    override fun save(timeline: Timeline) {
        local.save(timeline)
    }

    companion object {
        private var INSTANCE: TimelinesRepository? = null

        fun getInstance(local: TimelinesDataSource): TimelinesRepository {
            return INSTANCE
                    ?: TimelinesRepository(local).apply {
                INSTANCE = this
            }
        }

        @VisibleForTesting
        fun destroyInstance() {
            INSTANCE = null
        }
    }

}