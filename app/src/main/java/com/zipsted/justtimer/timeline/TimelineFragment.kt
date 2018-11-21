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

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.ui.EventsLayout
import com.zipsted.justtimer.ui.recyclerview.ItemTouchHelperCallback
import com.zipsted.justtimer.ui.recyclerview.RecyclerAdapter
import com.zipsted.justtimer.util.duration
import kotlinx.android.synthetic.main.timeline_fragment.view.*
import java.util.*

class TimelineFragment : Fragment(), TimelineContract.View {

    override lateinit var presenter: TimelineContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    private val handler = Handler()

    private lateinit var playingTimeline: LinearLayout
    private lateinit var eventsLayout: EventsLayout
    private lateinit var timelineContainer: LinearLayout
    private lateinit var noEventsView: LinearLayout
    private lateinit var playButton: FloatingActionButton

    private lateinit var timelineList: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerAdapter

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.timeline_fragment, container, false)
        setHasOptionsMenu(true)

        viewManager = LinearLayoutManager(context)
        viewAdapter = RecyclerAdapter(ArrayList(), object : RecyclerAdapter.OnStartDragListener {
            override fun onStartDrag(view: RecyclerView.ViewHolder) {
                itemTouchHelper.startDrag(view)
            }

        })

        with(root) {
            playingTimeline = findViewById(R.id.playing_timeline_layout)
            eventsLayout = findViewById(R.id.events_cloud)
            timelineContainer = findViewById(R.id.timeline)
            noEventsView = findViewById(R.id.noEvents)
            timelineList = findViewById<RecyclerView>(R.id.timeline_list).apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
                val callback = ItemTouchHelperCallback(object : ItemTouchHelperCallback.ItemTouchAdapter {
                    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
                        viewAdapter.moveAndNotify(fromPosition, toPosition)
                        presenter.reorderEventsInTimeline(fromPosition, toPosition)
                        return true
                    }

                    override fun onItemDismiss(position: Int) {
                        viewAdapter.removeAndNotify(position)
                        presenter.deleteEventFromTimeline(position, viewAdapter.events?.size ?: 0)
                    }

                })
                setOnTouchListener { _, _ -> presenter.isPlaying }
                itemTouchHelper = ItemTouchHelper(callback)
                itemTouchHelper.attachToRecyclerView(this)
            }
        }

        playButton = requireActivity().findViewById<FloatingActionButton>(R.id.floatingButton).apply {
            setImageResource(R.drawable.ic_play)
            setOnClickListener { presenter.isPlaying = !presenter.isPlaying }
        }

        return root
    }

    override fun onResume() {
        presenter.start()
        super.onResume()
    }

    override fun showEvents(events: List<Event>) {
        timelineContainer.visibility = View.VISIBLE
        noEventsView.visibility = View.GONE
        eventsLayout.visibility = View.VISIBLE

        eventsLayout.removeAllViews()

        for (event in events) {
            val view =
                    layoutInflater.inflate(R.layout.timeline_cloud_event_item, null, false).also { view ->
                        with(view) {
                            findViewById<TextView>(R.id.event_item_name).text = event.name
                            findViewById<View>(R.id.event_item_layout).setOnClickListener {
                                presenter.addEventToTimeline(event, viewAdapter.events?.size ?: 0)
                            }
                        }

                    }
            eventsLayout.addView(view)
        }
    }

    override fun showTimeline(events: List<TimelinedEvent>) {
        viewAdapter.events = events as java.util.ArrayList<TimelinedEvent>
        timelineContainer.visibility = View.VISIBLE
    }

    override fun showEmptyTimeline() {
        //TODO: Add some UI for empty Timeline
    }

    override fun showNoEvents() {
        timelineContainer.visibility = View.GONE
        noEventsView.visibility = View.VISIBLE
        eventsLayout.visibility = View.GONE
    }

    override fun showTerribleError() {
        println("DEBUG_TIMER: Timeline Terrible Error!")
    }

    override fun switchToPlayMode() {
        playButton.setImageResource(R.drawable.ic_stop)
        eventsLayout.visibility = View.GONE
        playingTimeline.visibility = View.VISIBLE

        updateCurrentEvent(viewAdapter.events.size - 1)
    }

    override fun switchToEditMode() {
        playButton.setImageResource(R.drawable.ic_play)
        eventsLayout.visibility = View.VISIBLE
        playingTimeline.visibility = View.GONE

        presenter.stopCountdown()

        presenter.loadTimeline()
    }

    override fun updateCountdown(min: Int, sec: Int) {
        handler.post {
            playingTimeline.event_duration.text = Pair(min, sec).duration()
        }
    }

    override fun currentEventIsDone(size: Int) {
        if (viewAdapter.events.size > 0) {
            updateCurrentEvent(size - 1)
        }
    }

    override fun message(text: Int) {
        Snackbar.make(eventsLayout, text, Snackbar.LENGTH_LONG).show()

    }

    private fun updateCurrentEvent(size: Int) {
        if (!isTimelineEmpty()) {
            playingTimeline.event_name.text = viewAdapter.events[0].name
            playingTimeline.event_duration.text = viewAdapter.events[0].durationAsString()

            viewAdapter.updateEvents(size)
        }
    }

    private fun isTimelineEmpty(): Boolean {
        return viewAdapter.events.isEmpty()
    }

    companion object {
        fun newInstance(): TimelineFragment {
            return TimelineFragment().apply {
                arguments = Bundle().apply {
                    //                    putString(EXTRA_EVENT_ID, eventId)
                }
            }
        }
    }

}