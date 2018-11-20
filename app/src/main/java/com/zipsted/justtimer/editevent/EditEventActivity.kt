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

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.Injection
import com.zipsted.justtimer.util.replaceFragment
import com.zipsted.justtimer.util.setupToolbar

class EditEventActivity : AppCompatActivity(), DurationPickerFragment.DurationPickerListener {

    private lateinit var editEventPresenter: EditEventPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_event_activity)
        val eventId = intent.getStringExtra(EditEventFragment.EXTRA_EVENT_ID)

        setupToolbar(R.id.toolbar) {
            setTitle(if (eventId == null) R.string.addEvent else R.string.editEvent)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val editEventView =
                supportFragmentManager.findFragmentById(R.id.contentFrame) as EditEventFragment?
                        ?: EditEventFragment.newInstance(eventId).also {
                            replaceFragment(it, R.id.contentFrame)
                        }
        editEventPresenter = EditEventPresenter(
                Injection.provideEventsRepository(applicationContext),
                Injection.provideEventsInTimelineRepository(applicationContext),
                Injection.provideTimelinesRepository(applicationContext),
                eventId, editEventView, true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDurationSet(min: Int, sec: Int) {
        editEventPresenter.changeDuration(min, sec)
    }

    override fun onDurationCancel() {
    }

    companion object {
        const val REQUEST_ADD_EVENT = 1
    }

}
