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

package com.zipsted.justtimer.data.source.event

import android.arch.persistence.room.*
import com.zipsted.justtimer.data.model.Event

@Dao interface EventDao {

    @Query("SELECT * FROM events") fun getEvents(): List<Event>

    @Query("SELECT * FROM events WHERE id = :eventId") fun getEvent(eventId: String): Event?

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertEvent(event: Event)

    @Update fun updateEvent(event: Event)

    @Delete fun deleteEvent(event: Event)

    @Query("DELETE FROM events WHERE id = :id") fun deleteEvent(id: String)
}