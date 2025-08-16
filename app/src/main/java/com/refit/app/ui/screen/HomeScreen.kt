package com.refit.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헬스데이터 보여주기위한 임시버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            Button(
                onClick = { navController.navigate("health_dev") },
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = "헬스 커넥트")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "홈 화면입니다")
    }
}
