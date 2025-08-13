package com.refit.app.ui.composable.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.refit.app.R
import com.refit.app.ui.composable.model.basic.BottomNavItem
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

val bottomNavItems = listOf(
    BottomNavItem("home", "홈", R.drawable.ic_home_selected, R.drawable.ic_home_unselected),
    BottomNavItem("category", "카테고리", R.drawable.ic_category_selected, R.drawable.ic_category_unselected),
    BottomNavItem("myfit", "마이핏", R.drawable.ic_closet_selected, R.drawable.ic_closet_unselected),
    BottomNavItem("community", "커뮤니티", R.drawable.ic_community_selected, R.drawable.ic_community_unselected),
    BottomNavItem("my", "MY", R.drawable.ic_my_selected, R.drawable.ic_my_unselected),
)

@Composable
fun BottomBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val selectedTab = when {
        currentRoute == null -> ""
        currentRoute.startsWith("myfit") -> "myfit"
        currentRoute.startsWith("community") -> "community"
        currentRoute.startsWith("category") -> "category"
        currentRoute.startsWith("my") -> "my"
        currentRoute.startsWith("home") -> "home"
        else -> ""
    }

    Column {
        Divider(
            color = Color.LightGray,
            thickness = 1.dp
        )
        NavigationBar(containerColor = Color.White) {
            bottomNavItems.forEach { item ->
                NavigationBarItem(
                    selected = item.route == selectedTab,
                    onClick = {
                        //val targetRoute = if (item.route == "category") "category/전체" else item.route
                        val targetRoute = item.route

                        if (selectedTab != item.route) {
                            navController.navigate(targetRoute) {
                                popUpTo(targetRoute) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            painter = painterResource(
                                id = if (item.route == selectedTab) item.selectedIcon else item.unselectedIcon
                            ),
                            contentDescription = item.label
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                fontFamily = Pretendard
                            )
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedIconColor = MainPurple,
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = MainPurple,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    }
}