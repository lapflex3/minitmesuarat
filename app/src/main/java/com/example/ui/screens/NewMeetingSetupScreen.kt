package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewMeetingSetupScreen(
    onBackClick: () -> Unit,
    onStartRecordingClick: (
        title: String,
        date: String,
        time: String,
        location: String,
        participants: String,
        glossary: String,
        chairman: String,
        secretary: String
    ) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale("ms", "MY")) }
    val todayDate = remember { dateFormat.format(Date()) }

    var title by remember { mutableStateOf("Mesyuarat Kemajuan & Perancangan AI") }
    var date by remember { mutableStateOf(todayDate) }
    var time by remember { mutableStateOf("10:00 AM - 11:30 AM") }
    var location by remember { mutableStateOf("Bilik Mesyuarat Perdana / Atas Talian") }
    var participants by remember { mutableStateOf("Puan Fatimah (Pengerusi), En. Razif (Pencatat Minit), Cik Aina (Sistem)") }
    var glossary by remember { mutableStateOf("MinitAura, KPI Q3, SOP, Belanjawan") }
    var chairman by remember { mutableStateOf("Puan Fatimah") }
    var secretary by remember { mutableStateOf("En. Razif") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Persediaan Mesyuarat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            onStartRecordingClick(
                                title, date, time, location,
                                participants, glossary, chairman, secretary
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("start_recording_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mulakan Rakaman",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info Card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Tips AI: Masukkan nama peserta dan istilah khas syarikat supaya pengecaman suara & tatabahasa lebih tepat.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Section 1: Maklumat Utama
            Text(
                text = "1. Maklumat Mesyuarat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tajuk Mesyuarat") },
                placeholder = { Text("Contoh: Mesyuarat Kemajuan Projek Suku 3") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_title_input"),
                leadingIcon = { Icon(Icons.Outlined.Title, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Tarikh") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Event, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Masa") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Lokasi / Platform") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            // Section 2: Peserta & Kenal Pasti Suara
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "2. Senarai Peserta Mesyuarat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = participants,
                onValueChange = { participants = it },
                label = { Text("Senarai Nama Peserta (Pisahkan dengan koma)") },
                placeholder = { Text("Puan Fatimah (Pengerusi), En. Razif (Pencatat), Cik Aina") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .testTag("setup_participants_input"),
                leadingIcon = { Icon(Icons.Outlined.People, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val participantChips = listOf("+ Puan Fatimah (Pengerusi)", "+ En. Razif (Pencatat)", "+ Cik Aina", "+ Encik Zaki")
                participantChips.forEach { chipLabel ->
                    AssistChip(
                        onClick = {
                            val cleanName = chipLabel.removePrefix("+ ")
                            participants = if (participants.isBlank()) cleanName else "$participants, $cleanName"
                        },
                        label = { Text(chipLabel, fontSize = 12.sp) }
                    )
                }
            }

            // Section 3: Glosari & Istilah Syarikat
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "3. Glosari & Istilah Khas Syarikat",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = glossary,
                onValueChange = { glossary = it },
                label = { Text("Istilah Khas / Kod Projek / Akronim") },
                placeholder = { Text("KPI, SOP, MinitAura, ReportLab, Belanjawan Q3") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_glossary_input"),
                leadingIcon = { Icon(Icons.Outlined.Spellcheck, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val glossaryPresets = listOf("+ KPI", "+ SOP", "+ MinitAura", "+ Belanjawan Q3", "+ ISO 9001")
                glossaryPresets.forEach { preset ->
                    AssistChip(
                        onClick = {
                            val cleanTerm = preset.removePrefix("+ ")
                            glossary = if (glossary.isBlank()) cleanTerm else "$glossary, $cleanTerm"
                        },
                        label = { Text(preset, fontSize = 12.sp) }
                    )
                }
            }

            // Section 4: Tandatangan Dokumen Rasmi
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "4. Pengesahan Document",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = chairman,
                    onValueChange = { chairman = it },
                    label = { Text("Pengerusi") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = secretary,
                    onValueChange = { secretary = it },
                    label = { Text("Pencatat Minit") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
