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

package com.zipsted.justtimer.data.source

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context
import com.zipsted.justtimer.data.model.Event
import com.zipsted.justtimer.data.model.EventInTimeline
import com.zipsted.justtimer.data.model.Timeline
import com.zipsted.justtimer.data.source.event.EventDao
import com.zipsted.justtimer.data.source.eventintimeline.EventInTimelineDao
import com.zipsted.justtimer.data.source.timeline.TimelineDao

@Database(entities = [Event::class, Timeline::class, EventInTimeline::class], version = 4)
abstract class TimerDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun timelineDao(): TimelineDao
    abstract fun eventInTimelineDao(): EventInTimelineDao

    companion object {
        private const val MIGRATION_1_TO_2_SQL =
                "CREATE TABLE IF NOT EXISTS timelines (" +
                        "name TEXT NOT NULL, " +
                        "id TEXT NOT NULL, " +
                        "PRIMARY KEY(id)" +
                        ")"
        private const val MIGRATION_2_TO_3_SQL =
                "CREATE TABLE IF NOT EXISTS events_in_timelines (" +
                        "eventId TEXT NOT NULL, " +
                        "timelineId TEXT NOT NULL, " +
                        "id TEXT NOT NULL, " +
                        "PRIMARY KEY(id)); \n" +
                        ""
        private const val MIGRATION_3_TO_4_SQL =
                "ALTER TABLE events_in_timelines " +
                        "ADD order NUMBER NOT NULL"

        private var INSTANCE: TimerDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): TimerDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context,
                            TimerDatabase::class.java,
                            "Timer.db")
                            .addMigrations(Migration1To2())
                            .addMigrations(Migration2To3())
                            .addMigrations(Migration3To4())
                            .build()
                }
            }
            return INSTANCE!!
        }

        private class Migration1To2 : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(MIGRATION_1_TO_2_SQL)
            }
        }

        private class Migration2To3 : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(MIGRATION_2_TO_3_SQL)
            }
        }

        private class Migration3To4 : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(MIGRATION_3_TO_4_SQL)
            }
        }
    }
}