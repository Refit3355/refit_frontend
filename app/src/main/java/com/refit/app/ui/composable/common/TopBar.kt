package com.refit.app.ui.composable.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.refit.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    currentRoute: String
) {
    val bottomTabs = listOf("home", "category", "myfit", "community", "my")
    val isBottomTab = currentRoute in bottomTabs
    val isLoginScreen = currentRoute == "login"
    val isSignupScreen = currentRoute == "signup"

    TopAppBar(
        title = {
            if (!isLoginScreen && !isSignupScreen) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            currentRoute == "home" -> "홈"
                            currentRoute == "category" -> "카테고리"
                            currentRoute == "myfit" -> "마이핏"
                            currentRoute == "community" -> "커뮤니티"
                            currentRoute == "my" -> "마이페이지"
                            else -> "상세페이지"
                        },
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                }
            }
        },
        navigationIcon = {
            when {
                isBottomTab -> {
                    Box(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .size(50.dp)
                            .clickable { navController.navigate("home") }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_text),
                            contentDescription = "로고",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                else -> {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_icon_back),
                            contentDescription = "뒤로가기",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        },
        actions = {
            if (!isLoginScreen && !isSignupScreen) {
                IconButton(onClick = { navController.navigate("notifications") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_icon_alarm),
                        contentDescription = "알림함",
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { navController.navigate("cart") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_icon_bag),
                        contentDescription = "장바구니",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}