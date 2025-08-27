package com.refit.app.ui.composable.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.refit.app.data.push.PushRegistrar
import com.refit.app.data.push.repository.NotificationRepository
import com.refit.app.network.RetrofitInstance
import com.refit.app.network.TokenManager
import com.refit.app.network.UserPrefs
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import kotlinx.coroutines.launch

@Composable
fun MypageMenuSection(navController: NavController) {
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Column(Modifier.padding(horizontal = 16.dp)) {
        // 찜 목록
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("wish") }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Favorite, contentDescription = null, tint = MainPurple)
            Spacer(Modifier.width(12.dp))
            Text("찜 목록", fontSize = 16.sp, fontFamily = Pretendard, color = MainPurple)
        }

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(thickness = 1.dp, color = androidx.compose.ui.graphics.Color.LightGray)
        Spacer(Modifier.height(4.dp))

        // 저장한 조합 목록
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("liked_combinations") }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = MainPurple)
            Spacer(Modifier.width(12.dp))
            Text("저장한 조합 목록", fontSize = 16.sp, fontFamily = Pretendard, color = MainPurple)
        }

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(thickness = 1.dp, color = androidx.compose.ui.graphics.Color.LightGray)
        Spacer(Modifier.height(4.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("created_combinations") }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = MainPurple)
            Spacer(Modifier.width(12.dp))
            Text("내가 생성한 조합", fontSize = 16.sp, fontFamily = Pretendard, color = MainPurple)
        }

        Spacer(Modifier.height(4.dp))
        HorizontalDivider(thickness = 1.dp, color = androidx.compose.ui.graphics.Color.LightGray)
        Spacer(Modifier.height(4.dp))

        // 로그아웃
        Row(
            Modifier
                .fillMaxWidth()
                .clickable {
                    scope.launch {
                        val repo = NotificationRepository()

                        // 1) 서버 등록 해제 + 로컬 FCM 토큰 폐기 (인증 살아있을 때)
                        PushRegistrar.unregister(ctx, repo)

                        // 2) 인증정보/유저정보 삭제
                        TokenManager.clearAll()
                        UserPrefs.clear()

                        // 3) 이동
                        navController.navigate("auth/login") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, tint = MainPurple)
            Spacer(Modifier.width(12.dp))
            Text("로그아웃", fontSize = 16.sp, fontFamily = Pretendard, color = MainPurple)
        }

        Spacer(Modifier.height(4.dp))
    }
}
