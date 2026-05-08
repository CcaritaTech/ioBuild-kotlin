package com.example.iobuild_kt.core.extension

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun String.formatDate(): String {
    return try {
        val parser = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateTime = LocalDateTime.parse(this, parser)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "PE"))
        dateTime.format(formatter)
    } catch (e: Exception) {
        this
    }
}

fun String.formatTime(): String {
    return try {
        val parser = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val dateTime = LocalDateTime.parse(this, parser)
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale("es", "PE"))
        dateTime.format(formatter)
    } catch (e: Exception) {
        this
    }
}

fun Double.formatPercent(): String {
    return String.format(Locale("es", "PE"), "%.1f%%", this)
}

fun Double.formatCurrency(): String {
    return String.format(Locale("es", "PE"), "S/ %.2f", this)
}
