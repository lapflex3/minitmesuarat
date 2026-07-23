package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meetings")
data class Meeting(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String,
    val time: String,
    val location: String = "Bilik Mesyuarat / Atas Talian",
    val participants: String = "",
    val glossary: String = "",
    val rawTranscription: String = "",
    val refinedText: String = "",
    val executiveSummary: String = "",
    val agendas: String = "",
    val decisions: String = "",
    val actionItems: String = "",
    val status: String = "Draft", // Draft, Processing, Completed
    val durationSeconds: Int = 0,
    val chairmanName: String = "Pengerusi Mesyuarat",
    val secretaryName: String = "Pencatat Minit",
    val refNumber: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
