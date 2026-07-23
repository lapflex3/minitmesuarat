package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting
import kotlinx.coroutines.flow.Flow

@Dao
interface MeetingDao {
    @Query("SELECT * FROM meetings ORDER BY createdAt DESC")
    fun getAllMeetings(): Flow<List<Meeting>>

    @Query("SELECT * FROM meetings WHERE id = :id")
    suspend fun getMeetingById(id: Long): Meeting?

    @Query("SELECT * FROM meetings WHERE title LIKE '%' || :query || '%' OR participants LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchMeetings(query: String): Flow<List<Meeting>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeeting(meeting: Meeting): Long

    @Update
    suspend fun updateMeeting(meeting: Meeting)

    @Query("DELETE FROM meetings WHERE id = :id")
    suspend fun deleteMeetingById(id: Long)
}

@Dao
interface CompanyLetterheadDao {
    @Query("SELECT * FROM company_letterhead WHERE id = 1 LIMIT 1")
    fun getLetterhead(): Flow<CompanyLetterhead?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLetterhead(letterhead: CompanyLetterhead)
}
