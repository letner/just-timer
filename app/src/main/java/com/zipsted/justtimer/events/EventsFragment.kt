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

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.editevent.EditEventActivity
import com.zipsted.justtimer.editevent.EditEventFragment

class EventsFragment : Fragment(), EventsContract.View {

    override lateinit var presenter: EventsContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded


    private lateinit var eventsView: LinearLayout
    private lateinit var noEventsView: View

    private val itemListener: EventItemListener = object : EventItemListener {
        override fun onEventClick(event: Event) {
            presenter.editEvent(event)
        }
    }
    private val listAdapter = EventsAdapter(ArrayList(), itemListener)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.events_fragment, container, false)
        setHasOptionsMenu(true)

        with(root) {
            val listView = findViewById<ListView>(R.id.eventsList).apply { adapter = listAdapter }
            eventsView = findViewById(R.id.events)
            noEventsView = findViewById(R.id.noEvents)
            findViewById<ImageView>(R.id.noEventsImage).setOnClickListener { showAddEvent() }
        }

        requireActivity().findViewById<FloatingActionButton>(R.id.floatingButton).apply {
            setImageResource(R.drawable.ic_add)
            setOnClickListener { presenter.addNewEvent() }
        }
        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun showEvents(events: List<Event>) {
        listAdapter.events = events
        eventsView.visibility = View.VISIBLE
        noEventsView.visibility = View.GONE
    }

    override fun showAddEvent() {
        val intent = Intent(context, EditEventActivity::class.java)
        startActivityForResult(intent, EditEventActivity.REQUEST_ADD_EVENT)
    }

    override fun showEditEvent(event: Event) {
        val intent = Intent(context, EditEventActivity::class.java).apply {
            putExtra(EditEventFragment.EXTRA_EVENT_ID, event.id)
        }
        startActivityForResult(intent, EditEventActivity.REQUEST_ADD_EVENT)
    }

    override fun showNoEvents() {
        eventsView.visibility = View.GONE
        noEventsView.visibility = View.VISIBLE
    }

    override fun showError() {

    }

    private class EventsAdapter(events: List<Event>, private val itemListener: EventItemListener) : BaseAdapter() {

        var events: List<Event> = events
            set(events) {
                field = events
                notifyDataSetChanged()
            }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
            val event = getItem(i)
            val rowView = view
                    ?: LayoutInflater.from(viewGroup.context).inflate(R.layout.event_item, viewGroup, false)

            with(rowView) {
                findViewById<TextView>(R.id.event_name).text = event.name
                findViewById<TextView>(R.id.event_duration).text = event.durationAsString()
                setOnClickListener { itemListener.onEventClick(event) }
            }
            return rowView
        }

        override fun getItem(i: Int): Event = events[i]

        override fun getItemId(i: Int): Long = i.toLong()

        override fun getCount(): Int = events.size

    }

    interface EventItemListener {
        fun onEventClick(event: Event)
    }

    companion object {

        fun newInstance(): EventsFragment {
            return EventsFragment().apply {
                arguments = Bundle().apply {
                    //                    putString(EXTRA_EVENT_ID, eventId)
                }
            }
        }
    }

}