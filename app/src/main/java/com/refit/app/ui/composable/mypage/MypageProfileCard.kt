package com.refit.app.ui.composable.mypage

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.data.me.modelAndView.ProfileImageViewModel
import com.refit.app.network.UserPrefs
import com.refit.app.ui.composable.home.HashtagChip
import com.refit.app.ui.theme.MainPurple
import com.refit.app.ui.theme.Pretendard
import androidx.compose.runtime.getValue
import coil.compose.AsyncImage
import com.refit.app.util.file.FileUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


@Composable
fun MypageProfileCard(
    nickname: String,
    tags: List<String>,
    onEditClick: () -> Unit = {},
    onArrowClick: () -> Unit = {},
    vm: ProfileImageViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileUrl by vm.profileUrl.collectAsState()

    val savedUrl = UserPrefs.getProfileUrl()
    val finalUrl = profileUrl ?: savedUrl

    // 갤러리/카메라 런처 준비
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val file = FileUtils.getFileFromUri(context, it)
            file?.let { f ->
                val requestFile = f.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("profileImage", f.name, requestFile)
                vm.updateProfileImage(body)
            } ?: run {
                // 파일 변환 실패 처리
                Log.e("Profile", "Failed to convert URI to File")
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 이미지 박스
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.BottomEnd
            ) {
                // 프사 부분
                AsyncImage(
                    model = finalUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, MainPurple, CircleShape)
                        .background(Color.White),
                    contentScale = ContentScale.Crop
                )

                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = "프로필 변경",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Color.Black, CircleShape)
                        .padding(4.dp)
                )
            }
            Spacer(Modifier.width(12.dp))

            // 오른쪽 정보
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nickname,
                    fontFamily = Pretendard,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tags.forEachIndexed { idx, t ->
                        HashtagChip(text = t)
                        if (idx != tags.lastIndex) Spacer(Modifier.width(8.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MainPurple)
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "내 타입 수정",
                        fontFamily = Pretendard,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            IconButton(onClick = onArrowClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "이동",
                    tint = Color.Gray
                )
            }
        }
    }
}
