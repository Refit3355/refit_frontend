package com.refit.app.util.health

import com.github.mikephil.charting.formatter.ValueFormatter

class SleepFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val h = value.toInt() / 60
        val m = value.toInt() % 60
        return "${h}h ${m}m"
    }
}
