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

package com.zipsted.justtimer.data.source.timeline

import android.arch.persistence.room.*
import com.zipsted.justtimer.data.model.Timeline

@Dao interface TimelineDao {

    @Query("SELECT * FROM timelines") fun getTimelines(): List<Timeline>

    @Query("SELECT * FROM timelines WHERE id = :timelineId") fun getTimeline(timelineId: String): Timeline?

    @Insert(onConflict = OnConflictStrategy.REPLACE) fun insertTimeLine(timeline: Timeline)

    @Update fun updateTimeline(timeline: Timeline)

    @Delete fun deleteTimeline(timeline: Timeline)

    @Query("DELETE FROM timelines WHERE id = :id") fun deleteTimeline(id: String)
}