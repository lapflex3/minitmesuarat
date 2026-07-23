package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiApiClient
import com.example.audio.AudioRecorderManager
import com.example.data.database.MinitAuraDatabase
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting
import com.example.data.repository.MeetingRepository
import com.example.pdf.PdfExporter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MeetingViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MinitAuraDatabase.getDatabase(application)
    private val repository = MeetingRepository(db.meetingDao(), db.companyLetterheadDao())
    val audioRecorder = AudioRecorderManager(application)
    private val geminiApiClient = GeminiApiClient()
    val pdfExporter = PdfExporter(application)

    // Search filter state
    val searchQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val meetingsList: StateFlow<List<Meeting>> = searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.allMeetings
            else repository.searchMeetings(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val letterheadState: StateFlow<CompanyLetterhead?> = repository.letterhead
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CompanyLetterhead()
        )

    // Active working meeting
    private val _currentMeeting = MutableStateFlow<Meeting?>(null)
    val currentMeeting: StateFlow<Meeting?> = _currentMeeting.asStateFlow()

    // AI Processing state
    private val _isProcessingAi = MutableStateFlow(false)
    val isProcessingAi: StateFlow<Boolean> = _isProcessingAi.asStateFlow()

    private val _processingMessage = MutableStateFlow("")
    val processingMessage: StateFlow<String> = _processingMessage.asStateFlow()

    // Exported PDF notification
    private val _lastExportedPdf = MutableStateFlow<File?>(null)
    val lastExportedPdf: StateFlow<File?> = _lastExportedPdf.asStateFlow()

    init {
        // Seed default letterhead & sample meeting if empty
        viewModelScope.launch {
            repository.letterhead.collect { lh ->
                if (lh == null) {
                    repository.saveLetterhead(CompanyLetterhead())
                }
            }
        }
        seedSampleDataIfNeeded()
    }

    private fun seedSampleDataIfNeeded() {
        viewModelScope.launch {
            val existing = repository.getMeetingById(1)
            if (existing == null) {
                val sampleDate = SimpleDateFormat("dd MMMM yyyy", Locale("ms", "MY")).format(Date())
                val sample = Meeting(
                    title = "Mesyuarat Kemajuan & Transformasi Digital AI",
                    date = sampleDate,
                    time = "10:00 AM - 11:30 AM",
                    location = "Bilik Mesyuarat Utama / Cyberjaya",
                    participants = "Dato' Dr. Ahmad Zaki (Pengerusi), Nurul Huda Ismail (Pencatat), En. Razif (Urusetia), Cik Aina (Sistem)",
                    glossary = "MinitAura, KPI Q3, Digitalisasi, ReportLab",
                    rawTranscription = "Mesyuarat memfokuskan kepada pelaksanaan MinitAura AI untuk menukar suara kepada teks, memperhalusi tatabahasa rasmi, dan penjanaan dokumen PDF berserta letterhead rasmi syarikat.",
                    refinedText = "Mesyuarat Kemajuan Digitalisasi telah membincangkan pengoperasian sistem MinitAura AI. Pihak jawatan kuasa bersetuju menerima pakai alir kerja pengurus minit digital.",
                    executiveSummary = "Mesyuarat bersetuju mempercepatkan penggunaan sistem MinitAura AI bagi mengurangkan masa penyediaan minit mesyuarat daripada 3 hari kepada serta-merta.",
                    agendas = "1. Perutusan Pengerusi\n2. Pengesahan Minit Lepas\n3. Pembentangan Prototaip MinitAura AI\n4. Tindakan & Penutup",
                    decisions = "1. Bersetuju meluluskan pelancaran MinitAura di seluruh jabatan.\n2. Melantik Nurul Huda Ismail sebagai pentadbir dokumen rasmi.",
                    actionItems = "• Edaran Draf PDF — En. Razif (Tarikh: 30 Julai 2026)\n• Integrasi Letterhead — Cik Aina (Tarikh: 2 Ogos 2026)",
                    status = "Completed",
                    chairmanName = "Dato' Dr. Ahmad Zaki",
                    secretaryName = "Nurul Huda Ismail",
                    refNumber = "MA/MINIT/2026/001"
                )
                repository.insertMeeting(sample)
            }
        }
    }

    fun prepareNewMeeting(
        title: String,
        date: String,
        time: String,
        location: String,
        participants: String,
        glossary: String,
        chairman: String,
        secretary: String
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ms", "MY"))
            val todayStr = if (date.isBlank()) dateFormat.format(Date()) else date
            val timeStr = if (time.isBlank()) "09:00 AM - 10:30 AM" else time

            val currentLh = letterheadState.value ?: CompanyLetterhead()
            val refNo = "${currentLh.refNoPrefix}/${System.currentTimeMillis().toString().takeLast(4)}"

            val newMeeting = Meeting(
                title = if (title.isBlank()) "Mesyuarat Rasmi MinitAura" else title,
                date = todayStr,
                time = timeStr,
                location = if (location.isBlank()) "Bilik Mesyuarat / Atas Talian" else location,
                participants = participants,
                glossary = glossary,
                chairmanName = if (chairman.isBlank()) currentLh.defaultChairman else chairman,
                secretaryName = if (secretary.isBlank()) currentLh.defaultSecretary else secretary,
                refNumber = refNo,
                status = "Draft"
            )

            val newId = repository.insertMeeting(newMeeting)
            _currentMeeting.value = newMeeting.copy(id = newId)
        }
    }

    fun loadMeeting(id: Long) {
        viewModelScope.launch {
            val meeting = repository.getMeetingById(id)
            _currentMeeting.value = meeting
        }
    }

    fun startRecording() {
        val audioFile = audioRecorder.startRecording()
        val meeting = _currentMeeting.value
        if (meeting != null) {
            val updated = meeting.copy(status = "Processing")
            _currentMeeting.value = updated
            viewModelScope.launch { repository.updateMeeting(updated) }
        }
    }

    fun stopAndProcessMeeting() {
        val recordedFile = audioRecorder.stopRecording()
        val meeting = _currentMeeting.value ?: return

        viewModelScope.launch {
            _isProcessingAi.value = true
            _processingMessage.value = "1/3: AI MinitAura sedang menganalisis audio & teks pertuturan..."

            val liveText = audioRecorder.liveSpeechText.value

            _processingMessage.value = "2/3: Memperhalusi tatabahasa Bahasa Melayu rasmi & istilah syarikat..."
            val result = geminiApiClient.processAudioOrTranscript(
                audioFile = recordedFile,
                rawSpeechInput = liveText,
                title = meeting.title,
                participants = meeting.participants,
                glossary = meeting.glossary
            )

            _processingMessage.value = "3/3: Menjana Ringkasan Eksekutif & Format Minit Mesyuarat Rasmi..."

            val duration = audioRecorder.elapsedSeconds.value

            val updatedMeeting = meeting.copy(
                rawTranscription = result.rawTranscription,
                refinedText = result.refinedText,
                executiveSummary = result.executiveSummary,
                agendas = result.agendas,
                decisions = result.decisions,
                actionItems = result.actionItems,
                status = "Completed",
                durationSeconds = duration
            )

            repository.updateMeeting(updatedMeeting)
            _currentMeeting.value = updatedMeeting

            _isProcessingAi.value = false
            _processingMessage.value = ""
        }
    }

    fun updateCurrentMeeting(meeting: Meeting) {
        _currentMeeting.value = meeting
        viewModelScope.launch {
            repository.updateMeeting(meeting)
        }
    }

    fun deleteMeeting(id: Long) {
        viewModelScope.launch {
            repository.deleteMeeting(id)
            if (_currentMeeting.value?.id == id) {
                _currentMeeting.value = null
            }
        }
    }

    fun saveLetterhead(companyName: String, tagline: String, address: String, phone: String, email: String, refPrefix: String) {
        viewModelScope.launch {
            val updated = CompanyLetterhead(
                id = 1,
                companyName = companyName,
                tagline = tagline,
                address = address,
                phone = phone,
                email = email,
                refNoPrefix = refPrefix
            )
            repository.saveLetterhead(updated)
        }
    }

    fun exportCurrentMeetingToPdf(meeting: Meeting) {
        viewModelScope.launch {
            val lh = letterheadState.value ?: CompanyLetterhead()
            val pdfFile = pdfExporter.generateMeetingMinutesPdf(meeting, lh)
            _lastExportedPdf.value = pdfFile
            if (pdfFile != null) {
                pdfExporter.sharePdf(pdfFile)
            }
        }
    }
}
