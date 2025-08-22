package com.refit.app.ui.composable.common

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import androidx.compose.ui.platform.LocalConfiguration
import android.widget.NumberPicker
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("NewApi")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SpinnerDatePicker(
    modifier: Modifier = Modifier,
    initial: LocalDate = LocalDate.now(),
    onDateChange: (LocalDate) -> Unit,
    pickerHeight: Dp = 104.dp,
    dividerHeightPx: Int = 2
) {
    var year by remember { mutableIntStateOf(initial.year) }
    var month by remember { mutableIntStateOf(initial.monthValue) }
    var day by remember { mutableIntStateOf(initial.dayOfMonth) }

    fun maxDay(y: Int, m: Int): Int = LocalDate.of(y, m, 1).lengthOfMonth()
    fun notify() {
        val d = day.coerceAtMost(maxDay(year, month))
        if (d != day) day = d
        onDateChange(LocalDate.of(year, month, d))
    }

    val conf = LocalConfiguration.current
    val isCompact = conf.screenWidthDp < 360

    Row(
        modifier = modifier,
        horizontalArrangement = if (isCompact) Arrangement.SpaceBetween else Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AndroidView(
            modifier = Modifier.weight(1f).height(pickerHeight),
            factory = { ctx ->
                NumberPicker(ctx).apply {
                    minValue = 2024; maxValue = 2027
                    value = year
                    setOnValueChangedListener { _, _, new -> year = new; notify() }
                    try { this.selectionDividerHeight = dividerHeightPx } catch (_: Throwable) {}
                    setPadding(0, 0, 0, 0) // 상하 여백 최소화
                }
            },
            update = { it.value = year }
        )
        AndroidView(
            modifier = Modifier.weight(1f).height(pickerHeight),
            factory = { ctx ->
                NumberPicker(ctx).apply {
                    minValue = 1; maxValue = 12
                    displayedValues = (1..12).map { it.toString() }.toTypedArray()
                    value = month
                    setOnValueChangedListener { _, _, new -> month = new; notify() }
                    try { this.selectionDividerHeight = dividerHeightPx } catch (_: Throwable) {}
                    setPadding(0, 0, 0, 0)
                }
            },
            update = { it.value = month }
        )
        AndroidView(
            modifier = Modifier.weight(1f).height(pickerHeight),
            factory = { ctx ->
                NumberPicker(ctx).apply {
                    minValue = 1; maxValue = maxDay(year, month)
                    value = day
                    setOnValueChangedListener { _, _, new -> day = new; notify() }
                    try { this.selectionDividerHeight = dividerHeightPx } catch (_: Throwable) {}
                    setPadding(0, 0, 0, 0)
                }
            },
            update = {
                it.maxValue = maxDay(year, month)
                it.value = day.coerceAtMost(it.maxValue)
            }
        )
    }
}
