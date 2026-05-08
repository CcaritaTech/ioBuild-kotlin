package com.example.iobuild_kt.core.extension

import android.content.Context
import android.content.Intent
import com.example.iobuild_kt.projects.domain.model.Project

fun shareProject(context: Context, project: Project) {
    val message = buildString {
        appendLine("🏗️ ${project.name}")
        appendLine("📍 ${project.location}")
        appendLine("📊 ${project.occupiedUnits}/${project.totalUnits} unidades (${"%.1f".format(project.occupancyRate)}%)")
        appendLine("🔹 ${project.status}")
        appendLine()
        append("— Enviado desde IoBuild")
    }
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        `package` = "com.whatsapp"
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        val fallback = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        context.startActivity(Intent.createChooser(fallback, "Compartir proyecto"))
    }
}
