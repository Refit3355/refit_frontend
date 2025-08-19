package com.refit.app.ui.composable.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTopBar(
    title: String,
    stepIndex: Int = 1,
    stepCount: Int = 3,
    onBack: () -> Unit
) {
    val progress = stepIndex.toFloat() / stepCount.toFloat()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        painterResource(id = R.drawable.ic_icon_back),
                        contentDescription = "뒤로가기"
                    )
                }
            },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        textAlign = TextAlign.Center,
                        style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        ),
                        color = Color.Black,
                        fontFamily = Pretendard
                    )
                }
            },
            actions = {
                Spacer(modifier = Modifier.height(0.dp).padding(end = 0.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_back),
                    contentDescription = null,
                    tint = Color.Transparent
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(4.dp),
            color = MainPurple,
            trackColor = Color(0xFFE5E5EA)
        )
    }
}

