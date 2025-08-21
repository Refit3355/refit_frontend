package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import com.refit.app.ui.theme.Pretendard

@Composable
fun MypageMenuSection(navController: NavController) {
    Column(Modifier.padding(horizontal = 16.dp)){
        // 찜 목록
        Row(
            Modifier
                .fillMaxWidth()
                // TODO: 찜 목록 화면 구현 후 연결
                .clickable { /* 이동 없음 → ripple만 발생 */ }
                // .clickable { navController.navigate("favorites") }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(12.dp))
            Text("찜 목록", fontSize = 16.sp, fontFamily = Pretendard)
        }

        Spacer(Modifier.height(4.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )

        Spacer(Modifier.height(4.dp))

        // 저장한 조합 목록
        Row(
            Modifier
                .fillMaxWidth()
                // TODO: 내 조합 목록 화면 구현 후 연결
                .clickable { /* 이동 없음 → ripple만 발생 */ }
                // .clickable { navController.navigate("savedCombos") }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(12.dp))
            Text("저장한 조합 목록", fontSize = 16.sp, fontFamily = Pretendard)
        }

        Spacer(Modifier.height(4.dp))

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray
        )

        Spacer(Modifier.height(4.dp))

        // 로그아웃
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    TokenManager.clearAll()
                    UserPrefs.clear()
                    navController.navigate("auth/login") {
                        popUpTo("home") { inclusive = true }
                        launchSingleTop = true
                    }
                }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Black)
            Spacer(Modifier.width(12.dp))
            Text("로그아웃", fontSize = 16.sp, fontFamily = Pretendard)
        }

        Spacer(Modifier.height(4.dp))
    }
}
