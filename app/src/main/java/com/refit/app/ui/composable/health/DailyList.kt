package com.refit.app.ui.composable.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.refit.app.model.health.DailyRow
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

/**
 * Health 데이터 리스트를 카드 형태로 보여줌
 */
@Composable
fun DailyList(rows: List<DailyRow>) {
    val fmt = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(rows) { row ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        row.date.format(fmt),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(6.dp))

                    InfoLine("걸음수(steps)", row.steps?.toString())
                    InfoLine("총 소모 칼로리(total kcal)", row.totalKcal?.round1())

                    val bp = when {
                        row.systolicMmhg != null && row.diastolicMmhg != null ->
                            "${row.systolicMmhg.round1()}/${row.diastolicMmhg.round1()} mmHg"
                        row.systolicMmhg != null -> "${row.systolicMmhg.round1()} mmHg"
                        row.diastolicMmhg != null -> "${row.diastolicMmhg.round1()} mmHg"
                        else -> null
                    }
                    InfoLine("혈압(blood pressure)", bp)

                    val mgdl = row.bloodGlucoseMgdl
                    val mmol = mgdl?.let { it * 0.0555 }
                    val glucoseText = when (mgdl) {
                        null -> null
                        else -> "${mgdl.round1()} mg/dL (${mmol?.round2()} mmol/L)"
                    }
                    InfoLine("혈당(blood glucose)", glucoseText)

                    InfoLine("영양 섭취(nutrition kcal)", row.intakeKcal?.round1())
                    val sleep = row.sleepMinutes?.let { "${it} min (${(it / 60.0).round1()} h)" }
                    InfoLine("수면(sleep)", sleep)
                }
            }
        }
    }
}

/**
 * Double 소수점 반올림용
 */
private fun Double.round1(): String =
    ((this * 10).roundToInt() / 10.0).toString()

private fun Double.round2(): String =
    ((this * 100).roundToInt() / 100.0).toString()
