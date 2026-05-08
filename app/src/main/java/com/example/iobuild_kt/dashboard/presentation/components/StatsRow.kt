package com.example.iobuild_kt.dashboard.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.iobuild_kt.core.i18n.lang

@Composable
fun DashboardStatsRow(
    activeProjects: Int,
    connectedDevices: Int,
    totalDevices: Int,
    occupiedUnits: Int,
    totalUnits: Int,
    occupancyRate: Double,
    energyEfficiency: Double,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = lang("dashboard.stats.active_projects"),
                value = "$activeProjects",
                subtitle = lang("dashboard.stats.total"),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = lang("dashboard.stats.devices"),
                value = "$connectedDevices/$totalDevices",
                subtitle = lang("dashboard.stats.connected"),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = lang("dashboard.stats.occupied_units"),
                value = "$occupiedUnits/$totalUnits",
                subtitle = "${"%.1f".format(occupancyRate)}% ${lang("dashboard.stats.occupancy")}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = lang("dashboard.stats.energy"),
                value = "${"%.0f".format(energyEfficiency)}%",
                subtitle = lang("dashboard.stats.avg"),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
