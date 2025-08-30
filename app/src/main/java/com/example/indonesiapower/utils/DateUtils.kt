package com.example.indonesiapower.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatTanggalIndonesia(tanggal: String?): String {
        if (tanggal.isNullOrEmpty()) return "-"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            val date = inputFormat.parse(tanggal)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "-"
        }
    }
}