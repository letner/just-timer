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

package com.zipsted.justtimer.data.source.eventintimeline

import android.arch.persistence.room.*
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.model.TimelinedEvent

@Dao interface EventInTimelineDao {

    @Query("SELECT events_in_timelines.id as id, events.name as name, events_in_timelines.eventId as eventId, events_in_timelines.timelineId as timelineId, events.min as min, events.sec as sec, events_in_timelines.`order` as `order` FROM events INNER JOIN events_in_timelines ON events.id = eventId WHERE timelineId = :timelineId ORDER BY `order` ASC") fun getEventsForTimeline(timelineId: String): List<TimelinedEvent>

    @Query("SELECT * FROM events_in_timelines WHERE `order` = :position") fun getItemByOrder(position: Int): EventInTimeline?

    @Query("DELETE FROM events_in_timelines WHERE id = :id") fun delete(id: String)

    @Query("DELETE FROM events_in_timelines WHERE `order` = :position") fun deleteByPosition(position: Int)

    @Query("DELETE FROM events_in_timelines WHERE eventId = :eventId") fun deleteByEventId(eventId: String)

    @Delete fun delete(eventInTimeline: EventInTimeline)

    @Query("UPDATE events_in_timelines SET `order` = :newPosition WHERE `order` = :lastPosition") fun updateOrder(lastPosition: Int, newPosition: Int)

    @Query("UPDATE events_in_timelines SET `order` = :position WHERE id = :id") fun updateOrder(id: String, position: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insert(eventInTimeline: EventInTimeline)

}