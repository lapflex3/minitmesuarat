package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.CompanyLetterheadDao
import com.example.data.dao.MeetingDao
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting

@Database(entities = [Meeting::class, CompanyLetterhead::class], version = 1, exportSchema = false)
abstract class MinitAuraDatabase : RoomDatabase() {
    abstract fun meetingDao(): MeetingDao
    abstract fun companyLetterheadDao(): CompanyLetterheadDao

    companion object {
        @Volatile
        private var INSTANCE: MinitAuraDatabase? = null

        fun getDatabase(context: Context): MinitAuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MinitAuraDatabase::class.java,
                    "minitaura_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
