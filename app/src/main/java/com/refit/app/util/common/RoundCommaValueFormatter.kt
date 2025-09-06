package com.refit.app.util.common

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

/**
 * 막대 차트 값 표시를 위한 공통 포매터
 * - 항상 반올림
 * - 천 단위 콤마 적용
 * - 축약 없음 (K, M 같은 단위 사용하지 않음)
 */
class RoundCommaValueFormatter : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        val value = barEntry?.y ?: return ""
        return "%,d".format(value.roundToInt())
    }

    override fun getFormattedValue(value: Float): String {
        return "%,d".format(value.roundToInt())
    }
}
