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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.*
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.Injection
import com.zipsted.justtimer.events.EventsActivity
import com.zipsted.justtimer.util.replaceFragment
import com.zipsted.justtimer.util.setupToolbar

class TimelineActivity : AppCompatActivity() {

    private val IS_PLAYING_KEY = "IS_PLAYING_KEY"

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var timelinePresenter: TimelinePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timeline_activity)

        setupToolbar(R.id.toolbar) {
            setTitle(R.string.timeline)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            setDisplayHomeAsUpEnabled(true)
        }

        drawerLayout = (findViewById<DrawerLayout>(R.id.drawer_layout)).apply {
            setStatusBarBackground(R.color.colorPrimaryDark)
        }
        setupDrawerContent(findViewById(R.id.navigation_view))

        createNotificationChannel()

        val timelineFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as TimelineFragment? ?: TimelineFragment.newInstance().also {
            replaceFragment(it, R.id.contentFrame)
        }

        timelinePresenter = TimelinePresenter(
                Injection.provideEventsRepository(applicationContext),
                Injection.provideTimelinesRepository(applicationContext),
                Injection.provideEventsInTimelineRepository(applicationContext),
                applicationContext,
                timelineFragment)
    }

    override fun onStart() {
        super.onStart()
        timelinePresenter.activityIsActive()
    }

    override fun onStop() {
        super.onStop()
        timelinePresenter.activityIsInactive()
    }

    override fun onDestroy() {
        super.onDestroy()
        timelinePresenter.stopCountdown()
    }

    override fun onBackPressed() {
        if (timelinePresenter.isPlaying) {
            val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                    .setTitle(R.string.confirm_exit_alert_title)
                    .setMessage(R.string.confirm_exit_alert_text)
                    .apply {
                        setPositiveButton(android.R.string.yes) { dialogInterface, _ ->
                            dialogInterface.cancel()
                            super.onBackPressed()
                        }
                        setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                            dialogInterface.cancel()
                            // Do nothing
                        }
                    }
            alertBuilder.create().show()
        } else {
            super.onBackPressed()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            timelinePresenter.isPlaying = savedInstanceState.getBoolean(IS_PLAYING_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState.apply {
            putBoolean(IS_PLAYING_KEY, timelinePresenter.isPlaying)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            if (menuItem.itemId == R.id.events) {
                val intent = Intent(this@TimelineActivity, EventsActivity::class.java)
                startActivity(intent)
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "JUST_TIMER_NOTIFICATION_CHANNEL"
    }

}