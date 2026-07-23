package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "company_letterhead")
data class CompanyLetterhead(
    @PrimaryKey
    val id: Int = 1,
    val companyName: String = "SYARIKAT BERSATU SDN BHD",
    val tagline: String = "Peneraju Inovasi & Digitalisasi Mesyuarat Rasmi",
    val address: String = "Aras 15, Menara AI Studio, Persiaran Multimedia, 63000 Cyberjaya",
    val phone: String = "+60 3-8888 9999",
    val email: String = "pentadbiran@syarikat.com.my",
    val website: String = "www.syarikat.com.my",
    val refNoPrefix: String = "MA/MINIT/2026",
    val defaultChairman: String = "Dato' Dr. Ahmad Zaki",
    val defaultSecretary: String = "Nurul Huda Ismail"
)
