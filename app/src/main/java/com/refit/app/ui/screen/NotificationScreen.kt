package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun NotificationScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "알림 목록 화면입니다")
    }
}