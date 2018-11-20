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

import com.zipsted.justtimer.data.source.event.EventsDataSource
import com.zipsted.justtimer.data.source.event.EventsRepository
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineDataSource
import com.zipsted.justtimer.data.source.eventintimeline.EventsInTimelineRepository
import com.zipsted.justtimer.data.source.timeline.TimelinesDataSource
import com.zipsted.justtimer.data.source.timeline.TimelinesRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class EditEventPresenterTest {

    @Mock private lateinit var editEventView: EditEventContract.View
    @Mock private lateinit var eventsDataSource: EventsDataSource
    @Mock private lateinit var timelinesDataSource: TimelinesDataSource
    @Mock private lateinit var eventsInTimelineDataSource: EventsInTimelineDataSource

    private lateinit var eventsRepository: EventsRepository
    private lateinit var timelinesRepository: TimelinesRepository
    private lateinit var eventsInTimelinesRepository: EventsInTimelineRepository
    private lateinit var editEventPresenter: EditEventPresenter

    @Before fun setupMocks() {
        MockitoAnnotations.initMocks(this)

        eventsRepository = EventsRepository.getInstance(eventsDataSource)
        timelinesRepository = TimelinesRepository.getInstance(timelinesDataSource)
        eventsInTimelinesRepository = EventsInTimelineRepository.getInstance(eventsInTimelineDataSource)

        `when`(editEventView.isActive).thenReturn(true)
    }

    @Test fun `create presenter and set the presenter to view`() {
        editEventPresenter = EditEventPresenter(
                eventsRepository,
                eventsInTimelinesRepository,
                timelinesRepository,
                null,
                editEventView,
                true)
        verify(editEventView).presenter = editEventPresenter
    }
}