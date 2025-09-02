package com.refit.app.ui.composable.analysis

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

@Composable
fun ErrorCard(msg: String) {
    Surface(
        color = Color(0xFFFFECEC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text("오류: $msg", modifier = Modifier.padding(16.dp), color = Color(0xFFB00020))
    }
}

@Composable
fun EmptyStateCard(title: String, body: String) {
    Surface(
        color = Color(0xFFF6F6F6),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title)
            Text(body, modifier = Modifier.padding(top = 6.dp), color = Color.DarkGray)
        }
    }
}