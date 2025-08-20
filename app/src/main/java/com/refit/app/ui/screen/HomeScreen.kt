package com.refit.app.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.LaunchedEffect
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs

@Composable
fun HomeScreen(navController: NavController) {

    /*
    // 로그 찍어보기
    LaunchedEffect(Unit) {
        // 토큰 읽기 (EncryptedSharedPreferences)
        val access = TokenManager.getAccessToken()
        val refresh = TokenManager.getRefreshToken()

        // 프로필 읽기 (일반 SharedPreferences)
        val memberId = UserPrefs.getMemberId()
        val nickname = UserPrefs.getNickname()
        val health   = UserPrefs.getHealth()

        fun mask(s: String?): String =
            when {
                s.isNullOrBlank() -> "null"
                s.length <= 12 -> "****"
                else -> "${s.take(6)}...${s.takeLast(6)}"
            }

        Log.d("AuthDebug", "Access = ${mask(access)}")
        Log.d("AuthDebug", "Refresh = ${mask(refresh)}")
        Log.d("AuthDebug", "User    = memberId=$memberId, nickname=$nickname, health=$health")
    }
    */

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 헬스데이터로 이동하는 주요 액션 카드
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clickable { navController.navigate("health_dev") },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = Color(0xFF0B5BD3)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "헬스",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "헬스 커넥트",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "삼성헬스 / Google Fit 데이터 보기",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "홈 화면입니다",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
