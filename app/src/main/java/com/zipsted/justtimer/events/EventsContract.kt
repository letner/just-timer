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

import com.zipsted.justtimer.BasePresenter
import com.zipsted.justtimer.BaseView
import com.zipsted.justtimer.data.model.Event

interface EventsContract {

    interface View : BaseView<Presenter> {

        var isActive : Boolean

        fun showEvents(events: List<Event>)

        fun showAddEvent()

        fun showEditEvent(event: Event)

        fun showNoEvents()

        fun showError()

    }

    interface Presenter : BasePresenter {

        fun loadEvents()

        fun addNewEvent()

        fun editEvent(event: Event)
    }
}