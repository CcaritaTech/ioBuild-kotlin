package com.example.iobuild_kt.dashboard.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun LineChart(
    title: String,
    subtitle: String? = null,
    data: List<Double>,
    labels: List<String> = emptyList(),
    unit: String = "",
    lineColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            if (data.isEmpty()) {
                Text(
                    text = "No hay datos disponibles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                val displayData = if (data.size > 60) data.takeLast(60) else data
                val minVal = displayData.min()
                val maxVal = displayData.max()
                val range = (maxVal - minVal).coerceAtLeast(1.0)
                val textMeasurer = rememberTextMeasurer()

                val yLabels = remember(displayData) {
                    (0..4).map { i ->
                        val value = maxVal - (range * i / 4.0)
                        val labelValue = value.roundToInt()
                        "$labelValue$unit"
                    }
                }

                val xLabels = remember(displayData, labels) {
                    val count = minOf(displayData.size, 5)
                    val step = maxOf(1, displayData.size / count)
                    (0 until displayData.size step step).map { i ->
                        labels.getOrElse(i) { "${i + 1}" }
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    val padding = 50f
                    val chartWidth = size.width - padding * 2
                    val chartHeight = size.height - padding * 2

                    // Draw horizontal grid lines + Y-axis labels
                    val gridColor = Color.LightGray.copy(alpha = 0.3f)
                    val labelStyle = TextStyle(fontSize = 10.sp, color = Color.Gray)

                    yLabels.forEachIndexed { i, label ->
                        val y = padding + (chartHeight * i / 4f)
                        drawLine(
                            color = gridColor,
                            start = Offset(padding, y),
                            end = Offset(size.width - padding, y),
                            strokeWidth = 1f
                        )
                        val textLayout = textMeasurer.measure(
                            text = AnnotatedString(label),
                            style = labelStyle
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(padding - textLayout.size.width - 6f, y - textLayout.size.height / 2f)
                        )
                    }

                    // Draw line
                    val stepX = if (displayData.size > 1) chartWidth / (displayData.size - 1) else chartWidth
                    val points = displayData.mapIndexed { index, value ->
                        val x = padding + index * stepX
                        val ratio = ((value - minVal) / range).toFloat()
                        val y = (padding + chartHeight * (1f - ratio)).coerceIn(padding, size.height - padding)
                        Offset(x, y)
                    }

                    val path = Path()
                    points.forEachIndexed { index, point ->
                        if (index == 0) path.moveTo(point.x, point.y)
                        else path.lineTo(point.x, point.y)
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )

                    // Draw data dots
                    if (displayData.size <= 31) {
                        points.forEach { point ->
                            drawCircle(color = lineColor, radius = 4f, center = point)
                            drawCircle(color = Color.White, radius = 2f, center = point)
                        }
                    }

                    // Draw X-axis labels
                    val xLabelStyle = TextStyle(fontSize = 9.sp, color = Color.Gray)
                    val labelCount = minOf(displayData.size, 5)
                    val labelStep = maxOf(1, displayData.size / labelCount)
                    for (i in 0 until displayData.size step labelStep) {
                        val point = points[i]
                        val label = labels.getOrElse(i) { "${i + 1}" }
                        val textLayout = textMeasurer.measure(
                            text = AnnotatedString(label),
                            style = xLabelStyle
                        )
                        drawText(
                            textLayoutResult = textLayout,
                            topLeft = Offset(
                                point.x - textLayout.size.width / 2f,
                                size.height - textLayout.size.height
                            )
                        )
                    }
                }
            }
        }
    }
}
