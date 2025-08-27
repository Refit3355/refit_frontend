package com.refit.app.ui.composable.weather

import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard


@Composable
fun WeatherChart(title: String, values: List<Double>, unit: String, iconRes: Int) {
    Spacer(Modifier.height(24.dp))
    Row(verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = title,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleMedium.copy(fontFamily = Pretendard))
    }
    Spacer(Modifier.height(8.dp))

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { ctx ->
            LineChart(ctx).apply {
                val entries = values.mapIndexed { idx, value ->
                    Entry(idx.toFloat(), value.toFloat())
                }
                val dataSet = LineDataSet(entries, "$title ($unit)").apply {
                    color = MainPurple.hashCode()
                    valueTextColor = Color.BLACK
                    lineWidth = 2f
                    setDrawCircles(true)
                    setCircleColor(MainPurple.hashCode())
                    circleRadius = 4f
                }
                data = LineData(dataSet)

                description.isEnabled = false
                axisRight.isEnabled = false
                xAxis.granularity = 1f
                xAxis.setDrawGridLines(false)
                axisLeft.setDrawGridLines(false)
                legend.isEnabled = false

                val totalDays = values.size
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val idx = value.toInt()
                        val daysAgo = (totalDays - 1) - idx
                        return if (daysAgo == 0) "오늘" else "${daysAgo}일 전"
                    }
                }

                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}$unit"
                    }
                }
            }
        }
    )
}
