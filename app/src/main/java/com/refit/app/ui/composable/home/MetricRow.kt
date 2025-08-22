package com.refit.app.ui.composable.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.refit.app.data.health.model.MetricItem

@Composable
fun MetricRow(items: List<MetricItem>) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { MetricCard(item = it, modifier = Modifier.weight(1f)) }
    }
}
