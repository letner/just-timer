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

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.DrawerActions.open
import android.support.test.espresso.contrib.DrawerMatchers.isClosed
import android.support.test.espresso.contrib.NavigationViewActions.navigateTo
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.view.Gravity
import com.zipsted.justtimer.R
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test

@LargeTest class NavigationTest {

    @Rule @JvmField var activityTestRule = ActivityTestRule(TimelineActivity::class.java)

    @Test fun showAllEvents() {
        openEventsScreen()
        onView(withId(R.id.events_fragment)).check(matches(isDisplayed()))
    }

    @Test fun backToTimeline() {
        openEventsScreen()
        pressBack()
        onView(withId(R.id.timeline)).check(matches(isDisplayed()))
    }

    @Test fun showAddEvent() {
        openEventsScreen()
        onView(withId(R.id.floatingButton)).perform(click())
        onView(withId(R.id.add_event_name)).check(matches(isDisplayed()))
        onView(withId(R.id.floatingButtonDelete)).check(matches(not(isDisplayed())))
    }

    private fun openEventsScreen() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.START)))
                .perform(open())
        onView(withId(R.id.navigation_view))
                .perform(navigateTo(R.id.events))
    }
}