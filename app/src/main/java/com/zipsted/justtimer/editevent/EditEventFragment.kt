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

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zipsted.justtimer.R
import com.zipsted.justtimer.util.duration
import com.zipsted.justtimer.util.parseDuration

class EditEventFragment : Fragment(), EditEventContract.View {

    override lateinit var presenter: EditEventContract.Presenter
    override var isActive = false
        get() = isAdded

    private lateinit var name: TextView
    private lateinit var duration: TextView
    private lateinit var deleteButton: FloatingActionButton


    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        with(requireActivity()) {
            val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
            findViewById<FloatingActionButton>(R.id.floatingButtonDone).apply {
                setImageResource(R.drawable.ic_done)
                setOnClickListener {
                    val (min, sec) = duration.text.toString().parseDuration()
                    presenter.saveEvent(name.text.toString(), min, sec)
                }
            }
            deleteButton =
                    findViewById<FloatingActionButton>(R.id.floatingButtonDelete).apply {
                        setImageResource(R.drawable.ic_delete)
                        setOnClickListener {
                            presenter.deleteEvent(eventId)
                            showEventsList()
                        }
                    }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.add_event_fragment, container, false)
        with(root) {
            name = findViewById(R.id.add_event_name)
            duration = findViewById(R.id.add_event_duration)
            duration.setOnClickListener { showDurationPicker() }
        }
        setHasOptionsMenu(true)
        return root
    }

    override fun showEventsList() {
        requireActivity().apply {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun showEmptyEventError() {
        Snackbar.make(name, R.string.name_empty_error, Snackbar.LENGTH_LONG).show()
    }

    override fun setName(param: String) {
        name.text = param
    }

    override fun setDuration(min: Int, sec: Int) {
        duration.text = Pair(min, sec).duration()
    }

    override fun hideDeleteButton(hidden: Boolean) {
        if (hidden) deleteButton.hide() else deleteButton.show()
    }

    private fun showDurationPicker() {
        val (min, sec) = duration.text.toString().parseDuration()
        DurationPickerFragment.newInstance(min, sec).show(fragmentManager, DurationPickerFragment.TAG)
    }

    companion object {
        const val EXTRA_EVENT_ID = "EXTRA_EVENT_ID"

        fun newInstance(eventId: String?): EditEventFragment {
            return EditEventFragment().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_EVENT_ID, eventId)
                }
            }
        }
    }
}