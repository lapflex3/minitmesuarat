package com.example.api

data class ProcessedMinutesResult(
    val rawTranscription: String,
    val refinedText: String,
    val executiveSummary: String,
    val agendas: String,
    val decisions: String,
    val actionItems: String,
    val aiSuggestions: String
)
