package com.refit.app.data.health.model

import androidx.annotation.DrawableRes

data class MetricItem(
    val title: String,
    val value: String,       // 값 ("3000", "7시간 30분", "--" 등)
    val unit: String,        // 단위 ("보", "시간", "℃")
    @DrawableRes val iconRes: Int,
    val iconOffsetY: Int = -20, // 젤뽀 위치 (위로 20dp가 기본)
    val onClick: (() -> Unit)? = null
)
