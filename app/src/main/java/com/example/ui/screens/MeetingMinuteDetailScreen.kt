package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CompanyLetterhead
import com.example.data.model.Meeting

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeetingMinuteDetailScreen(
    meeting: Meeting?,
    letterhead: CompanyLetterhead?,
    onBackClick: () -> Unit,
    onSaveEdits: (Meeting) -> Unit,
    onExportPdfClick: (Meeting) -> Unit
) {
    if (meeting == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val lh = letterhead ?: CompanyLetterhead()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Editable state fields
    var editTitle by remember(meeting) { mutableStateOf(meeting.title) }
    var editSummary by remember(meeting) { mutableStateOf(meeting.executiveSummary.ifBlank { meeting.refinedText }) }
    var editDecisions by remember(meeting) { mutableStateOf(meeting.decisions) }
    var editActionItems by remember(meeting) { mutableStateOf(meeting.actionItems) }
    var editAgendas by remember(meeting) { mutableStateOf(meeting.agendas) }
    var editChairman by remember(meeting) { mutableStateOf(meeting.chairmanName) }
    var editSecretary by remember(meeting) { mutableStateOf(meeting.secretaryName) }

    val tabTitles = listOf("Ringkasan AI", "Keputusan & Tugasan", "Pertuturan Asal", "Pengesahan")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Semakan & Penyuntingan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Sunting Minit")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sunting")
                    }

                    Button(
                        onClick = { onExportPdfClick(meeting) },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("export_pdf_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Eksport PDF", fontWeight = FontWeight.Bold)
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
        ) {
            // Company Letterhead Preview Card Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = lh.companyName.uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = lh.tagline,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${lh.address} | Tel: ${lh.phone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.primary)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MINIT MESYUARAT RASMI",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (meeting.refNumber.isNotBlank()) meeting.refNumber else "${lh.refNoPrefix}/${meeting.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = meeting.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Tarikh: ${meeting.date} (${meeting.time}) | Lokasi: ${meeting.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tabs Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            // Tab Contents
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> TabRingkasanEksekutif(meeting)
                    1 -> TabKeputusanDanTugasan(meeting)
                    2 -> TabPertuturanAsal(meeting)
                    3 -> TabPengesahan(meeting, lh)
                }
            }
        }
    }

    // Edit Dialog Modal
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Sunting Minit Mesyuarat", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Tajuk Mesyuarat") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editSummary,
                        onValueChange = { editSummary = it },
                        label = { Text("Ringkasan Eksekutif") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    )

                    OutlinedTextField(
                        value = editDecisions,
                        onValueChange = { editDecisions = it },
                        label = { Text("Senarai Keputusan") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    OutlinedTextField(
                        value = editActionItems,
                        onValueChange = { editActionItems = it },
                        label = { Text("Senarai Tindakan / Tugasan") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    OutlinedTextField(
                        value = editChairman,
                        onValueChange = { editChairman = it },
                        label = { Text("Nama Pengerusi") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editSecretary,
                        onValueChange = { editSecretary = it },
                        label = { Text("Nama Pencatat Minit") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = meeting.copy(
                            title = editTitle,
                            executiveSummary = editSummary,
                            decisions = editDecisions,
                            actionItems = editActionItems,
                            chairmanName = editChairman,
                            secretaryName = editSecretary
                        )
                        onSaveEdits(updated)
                        showEditDialog = false
                    }
                ) {
                    Text("Simpan Perubahan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun TabRingkasanEksekutif(meeting: Meeting) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionCard(
            title = "Ringkasan Eksekutif (MinitAura AI)",
            icon = Icons.Outlined.AutoAwesome,
            content = meeting.executiveSummary.ifBlank { meeting.refinedText.ifBlank { "Tiada ringkasan dijana." } }
        )

        DetailSectionCard(
            title = "Perhalusan Tatabahasa & Bahasa Rasmi",
            icon = Icons.Outlined.Gavel,
            content = meeting.refinedText.ifBlank { "Pertuturan telah diselaraskan mengikut tatabahasa Bahasa Melayu rasmi." }
        )
    }
}

@Composable
fun TabKeputusanDanTugasan(meeting: Meeting) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionCard(
            title = "Agenda Mesyuarat",
            icon = Icons.Outlined.ListAlt,
            content = meeting.agendas.ifBlank { "1. Perutusan Pengerusi\n2. Pengesahan Minit\n3. Hal-Hal Lain" }
        )

        DetailSectionCard(
            title = "Senarai Keputusan",
            icon = Icons.Outlined.CheckCircle,
            content = meeting.decisions.ifBlank { "• Meluluskan cadangan perancangan projek." }
        )

        DetailSectionCard(
            title = "Senarai Tindakan & Tugasan",
            icon = Icons.Outlined.Assignment,
            content = meeting.actionItems.ifBlank { "• Tiada tindakan khas ditetapkan." }
        )
    }
}

@Composable
fun TabPertuturanAsal(meeting: Meeting) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        DetailSectionCard(
            title = "Teks Pertuturan Asal (Speech-To-Text)",
            icon = Icons.Outlined.RecordVoiceOver,
            content = meeting.rawTranscription.ifBlank { "Rakaman audio dirakamkan secara langsung." }
        )
    }
}

@Composable
fun TabPengesahan(meeting: Meeting, letterhead: CompanyLetterhead) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pengesahan Minit Mesyuarat", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Disediakan Oleh:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(meeting.secretaryName.ifBlank { letterhead.defaultSecretary }, fontWeight = FontWeight.Bold)
                        Text("Pencatat Minit", style = MaterialTheme.typography.bodySmall)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Disahkan Oleh:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(meeting.chairmanName.ifBlank { letterhead.defaultChairman }, fontWeight = FontWeight.Bold)
                        Text("Pengerusi Mesyuarat", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailSectionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
        }
    }
}
