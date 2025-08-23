package com.refit.app.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.refit.app.R
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs

@Composable
fun MyScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "마이페이지 화면입니다")

        Spacer(Modifier.height(12.dp))

        // 찜 목록으로 이동 버튼
        Button(
            onClick = { navController.navigate("wish") },
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_heart_red),
                contentDescription = "찜 목록"
            )
            Spacer(Modifier.width(8.dp))
            Text("찜 목록 보기")
        }
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                // 토큰/유저정보 모두 삭제
                TokenManager.clearAll()
                UserPrefs.clear()

                // 로그인 화면으로 이동 (뒤로가기 시 홈 안 뜨도록 스택 정리)
                navController.navigate("auth/login") {
                    popUpTo("home") { inclusive = true } // 필요 시 시작 지점 route로 조정
                    launchSingleTop = true
                }
            },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("로그아웃(임시)")
        }

        Button(
            onClick = { navController.navigate("account/edit") },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("내 정보 수정(임시)")
        }

        Button(
            onClick = { navController.navigate("account/health/edit") },
            shape = RoundedCornerShape(12.dp)
        ) { Text("건강 정보 수정 (임시)") }


    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun PreviewMyScreen() {
    MaterialTheme {
        val nav = rememberNavController()
        MyScreen(navController = nav)
    }
}
