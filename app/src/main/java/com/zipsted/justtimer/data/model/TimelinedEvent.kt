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

package com.zipsted.justtimer.data.model

import android.os.Parcel
import android.os.Parcelable

data class TimelinedEvent(
        var id: String,
        var eventId: String,
        var timelineId: String,
        var name: String,
        var min: Int,
        var sec: Int,
        var order: Int) : Parcelable {

    private constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt())

    fun toEventInTimeline(): EventInTimeline {
        return EventInTimeline(order, eventId, timelineId, id)
    }

    fun durationAsString(): String {
        return String.format("%d:%02d", min, sec)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(eventId)
        parcel.writeString(timelineId)
        parcel.writeString(name)
        parcel.writeInt(min)
        parcel.writeInt(sec)
        parcel.writeInt(order)
    }

    override fun describeContents() = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<TimelinedEvent> {
            override fun createFromParcel(parcel: Parcel): TimelinedEvent {
                return TimelinedEvent(parcel)
            }

            override fun newArray(size: Int): Array<TimelinedEvent?> {
                return arrayOfNulls(size)
            }
        }

        fun copy(sourceEvent: TimelinedEvent): TimelinedEvent {
            return TimelinedEvent(
                    sourceEvent.id,
                    sourceEvent.eventId,
                    sourceEvent.timelineId,
                    sourceEvent.name,
                    sourceEvent.min,
                    sourceEvent.sec,
                    sourceEvent.order)
        }


    }
}
