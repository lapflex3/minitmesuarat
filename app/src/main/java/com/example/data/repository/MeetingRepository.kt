package com.example.data.repository

import com.example.data.dao.CompanyLetterheadDao
import com.example.data.dao.MeetingDao
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting
import kotlinx.coroutines.flow.Flow

class MeetingRepository(
    private val meetingDao: MeetingDao,
    private val letterheadDao: CompanyLetterheadDao
) {
    val allMeetings: Flow<List<Meeting>> = meetingDao.getAllMeetings()
    val letterhead: Flow<CompanyLetterhead?> = letterheadDao.getLetterhead()

    fun searchMeetings(query: String): Flow<List<Meeting>> = meetingDao.searchMeetings(query)

    suspend fun getMeetingById(id: Long): Meeting? = meetingDao.getMeetingById(id)

    suspend fun insertMeeting(meeting: Meeting): Long = meetingDao.insertMeeting(meeting)

    suspend fun updateMeeting(meeting: Meeting) = meetingDao.updateMeeting(meeting)

    suspend fun deleteMeeting(id: Long) = meetingDao.deleteMeetingById(id)

    suspend fun saveLetterhead(letterhead: CompanyLetterhead) = letterheadDao.saveLetterhead(letterhead)
}
