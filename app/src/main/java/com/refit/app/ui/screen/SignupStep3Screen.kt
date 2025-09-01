package com.refit.app.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard

@Composable
fun SignupStep3Screen(
    nickname: String? = null,
    onBack: () -> Unit,
    onLogin: () -> Unit,
    @DrawableRes illustrationRes: Int? = null
) {
    val displayName = nickname?.takeIf { it.isNotBlank() } ?: "리핏"
    val scroll = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            LinearProgressIndicator(
                progress = { 3f / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(4.dp),
                color = MainPurple,
                trackColor = Color(0xFFE5E5EA)
            )
        }
        // 본문 스크롤
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append(displayName)
                    }
                    append("님 반가워요!\n")
                    withStyle(SpanStyle(color = Color.Black)) {
                        append("회원가입이 완료되었어요.")
                    }
                },
                fontSize = 20.sp,
                lineHeight = 36.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Pretendard,
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MainPurple, fontWeight = FontWeight.Bold)) {
                        append("리핏")
                    }
                    append("과 함께\n나만의 뷰티·헬스 루틴을 시작해요!")
                },
                fontSize = 20.sp,
                lineHeight = 36.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = Pretendard,
            )

            Image(
                painter = painterResource(id = illustrationRes ?: R.drawable.ic_signup_success),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(450.dp)
                    .heightIn(min = 450.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            // 스크롤 내용이 버튼에 가리지 않도록 여유
            Spacer(Modifier.height(100.dp))
        }

        // 하단 고정 CTA 버튼
        Button(
            onClick = onLogin,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
                .height(60.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MainPurple,
                contentColor = Color.White
            )
        ) {
            Text("로그인", fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = Pretendard)
        }
    }
}

@Preview(showBackground = true, widthDp = 420, heightDp = 1000)
@Composable
private fun PreviewSignupComplete() {
    MaterialTheme {
        SignupStep3Screen(
            nickname = null,
            onBack = {},
            onLogin = {},
            illustrationRes = null
        )
    }
}
