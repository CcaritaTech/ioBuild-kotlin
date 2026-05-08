package com.example.iobuild_kt.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.iobuild_kt.core.ui.components.ErrorScreen
import com.example.iobuild_kt.core.ui.components.LoadingScreen
import com.example.iobuild_kt.dashboard.presentation.components.DashboardStatsRow
import com.example.iobuild_kt.dashboard.presentation.components.DeviceDistributionChart
import com.example.iobuild_kt.dashboard.presentation.components.LineChart
import com.example.iobuild_kt.core.i18n.lang
import com.example.iobuild_kt.dashboard.presentation.components.ProjectCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val current = state) {
        is DashboardUiState.Loading -> LoadingScreen(message = lang("dashboard.loading"))
        is DashboardUiState.Error -> ErrorScreen(
            message = current.message,
            onRetry = { viewModel.loadDashboard() }
        )
        is DashboardUiState.Success -> {
            val dashboard = current.dashboard

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = lang("dashboard.title"),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                item {
                    DashboardStatsRow(
                        activeProjects = dashboard.activeProjectsCount,
                        connectedDevices = dashboard.onlineDevices,
                        totalDevices = dashboard.totalDevices,
                        occupiedUnits = dashboard.occupiedUnits,
                        totalUnits = dashboard.totalUnits,
                        occupancyRate = dashboard.occupancyRate,
                        energyEfficiency = dashboard.energyEfficiencyAvg
                    )
                }

                item {
                    DeviceDistributionChart(
                        devicesByType = dashboard.devicesByType
                    )
                }

                    item {
                        LineChart(
                            title = lang("dashboard.chart.occupancy"),
                        data = dashboard.monthlyOccupancy.map { it.occupancyRate },
                        labels = dashboard.monthlyOccupancy.map { it.month.take(3) },
                        unit = "%",
                        lineColor = MaterialTheme.colorScheme.primary
                    )
                }

                item {
                    LineChart(
                        title = lang("dashboard.chart.energy"),
                        subtitle = lang("dashboard.chart.energy_subtitle"),
                        data = dashboard.energyHistory.map { it.value },
                        unit = "kWh",
                        lineColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                item {
                    Text(
                        text = lang("dashboard.section.projects"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(dashboard.projectsOverview.size) { index ->
                    ProjectCard(project = dashboard.projectsOverview[index])
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
