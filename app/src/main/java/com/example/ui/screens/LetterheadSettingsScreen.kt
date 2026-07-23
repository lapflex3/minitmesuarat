package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.CompanyLetterhead

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterheadSettingsScreen(
    letterhead: CompanyLetterhead?,
    onBackClick: () -> Unit,
    onSaveClick: (
        companyName: String,
        tagline: String,
        address: String,
        phone: String,
        email: String,
        refPrefix: String
    ) -> Unit
) {
    val lh = letterhead ?: CompanyLetterhead()

    var companyName by remember(letterhead) { mutableStateOf(lh.companyName) }
    var tagline by remember(letterhead) { mutableStateOf(lh.tagline) }
    var address by remember(letterhead) { mutableStateOf(lh.address) }
    var phone by remember(letterhead) { mutableStateOf(lh.phone) }
    var email by remember(letterhead) { mutableStateOf(lh.email) }
    var refPrefix by remember(letterhead) { mutableStateOf(lh.refNoPrefix) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Letterhead Rasmi Syarikat", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            onSaveClick(companyName, tagline, address, phone, email, refPrefix)
                            onBackClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_letterhead_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Tetapan Letterhead", fontWeight = FontWeight.Bold)
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
            // Live Letterhead Preview Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("PRATONTON LETTERHEAD DOKUMEN", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(companyName.ifBlank { "NAMA SYARIKAT" }.uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text(tagline.ifBlank { "Slogan Syarikat" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${address.ifBlank { "Alamat Syarikat" }} | Tel: ${phone.ifBlank { "+60 3-XXXX XXXX" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary)
                    Text("No. Rujukan: $refPrefix/001", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            Text("Tetapan Maklumat Syarikat", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Nama Syarikat / Organisasi") },
                modifier = Modifier.fillMaxWidth().testTag("letterhead_company_input"),
                leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = tagline,
                onValueChange = { tagline = it },
                label = { Text("Tagline / Slogan Syarikat") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.FormatQuote, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat Rasmi") },
                modifier = Modifier.fillMaxWidth().height(90.dp),
                leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                shape = RoundedCornerShape(10.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("No. Telefon") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mel") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }

            OutlinedTextField(
                value = refPrefix,
                onValueChange = { refPrefix = it },
                label = { Text("Awalan No. Rujukan Minit") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Outlined.Numbers, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }
    }
}
