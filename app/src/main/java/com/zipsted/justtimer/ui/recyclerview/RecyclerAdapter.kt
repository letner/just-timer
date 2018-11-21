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

package com.zipsted.justtimer.ui.recyclerview

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.zipsted.justtimer.R
import com.zipsted.justtimer.data.model.TimelinedEvent
import com.zipsted.justtimer.ui.recyclerview.RecyclerAdapter.RecyclerHolderView
import java.util.*

class RecyclerAdapter(
        events: List<TimelinedEvent>,
        private val dragStartListener: OnStartDragListener
) : RecyclerView.Adapter<RecyclerHolderView>() {

    var events: ArrayList<TimelinedEvent> = events as ArrayList<TimelinedEvent>
        set(events) {
            field = events
            notifyDataSetChanged()
        }

    class RecyclerHolderView(val view: View) : RecyclerView.ViewHolder(view)

    interface OnStartDragListener { fun onStartDrag(view: RecyclerView.ViewHolder) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerHolderView {
        val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_item_reorderable, parent, false)
        return RecyclerHolderView(root)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: RecyclerHolderView, position: Int) {
        with(holder.view) {
            findViewById<TextView>(R.id.event_name).text = events[position].name
            findViewById<TextView>(R.id.event_duration).text = events[position].durationAsString()

            findViewById<ImageView>(R.id.reorder_handle)
                    .setOnTouchListener { view, motionEvent ->
                        when (motionEvent.actionMasked) {
                            MotionEvent.ACTION_DOWN -> dragStartListener.onStartDrag(holder)
                        }
                        false
                    }
        }
    }

    override fun getItemCount(): Int = events.size

    fun updateEvents(size: Int) {
        if (events.isNotEmpty()) {
            val amountToDelete = events.size - size
            for (i in 0 until amountToDelete) {
                removeAndNotify(0)
            }
        }
    }

    fun moveAndNotify(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
        Collections.swap(events, fromPosition, toPosition)
    }

    fun removeAndNotify(position: Int) {
        notifyItemRemoved(position)
        events.removeAt(position)
    }

}