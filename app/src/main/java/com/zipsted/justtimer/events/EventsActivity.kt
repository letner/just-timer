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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.Injection
import com.zipsted.justtimer.util.replaceFragment
import com.zipsted.justtimer.util.setupToolbar

class EventsActivity : AppCompatActivity() {

    private lateinit var eventsPresenter: EventsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.events_activity)

        setupToolbar(R.id.toolbar) {
            setTitle(R.string.events)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val eventsView = supportFragmentManager.findFragmentById(R.id.contentFrame) as EventsFragment?
                ?: EventsFragment.newInstance().also {
                    replaceFragment(it, R.id.contentFrame)
                }
        eventsPresenter = EventsPresenter(Injection.provideEventsRepository(applicationContext), eventsView)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_EVENT_ID: String = "EXTRA_EVENT_ID"
    }

}