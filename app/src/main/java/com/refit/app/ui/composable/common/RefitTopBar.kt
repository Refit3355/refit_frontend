package com.refit.app.ui.composable.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.ui.composable.model.basic.AppBarConfig
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import com.refit.app.ui.theme.MainPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefitTopBar(config: AppBarConfig) {
    when (config) {
        // 검색창 있는 페이지 앱바
        is AppBarConfig.HomeSearch -> TopAppBar(
            title = {
                val interaction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F1F5))
                        .clickable(
                            interactionSource = interaction,
                            indication = null
                        ) { config.onSearchClick() }
                        .padding(horizontal = 12.dp)
                        .semantics { contentDescription = "검색 열기" },
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (config.query.isBlank()) "검색어를 입력하세요" else config.query,
                            color = if (config.query.isBlank()) Color(0xFF9E9E9E) else Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )

                        Icon(
                            painter = painterResource(R.drawable.ic_icon_search),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            navigationIcon = {
                Image(
                    painterResource(R.drawable.ic_logo_text),
                    contentDescription = "Re:fit",
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .size(48.dp)
                        .clickable { config.onLogoClick() }
                )
            },
            actions = {
                if (config.showActions) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_alarm),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onAlarmClick() }
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_bag),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onCartClick() }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // 좌측 로고 + 페이지 이름 + 우측 아이콘
        is AppBarConfig.HomeTitle -> TopAppBar(
            navigationIcon = {
                Box(
                    modifier = Modifier
                        .width(78.dp)
                        .padding(start = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Image(
                        painterResource(R.drawable.ic_logo_text),
                        contentDescription = "Re:fit",
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { config.onLogoClick() }
                    )
                }
            },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        config.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            actions = {
                Box(
                    modifier = Modifier
                        .width(78.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_alarm),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onAlarmClick() }
                        )
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_bag),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onCartClick() }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        // 좌측 뒤로가기 + 중앙 페이지 이름
        is AppBarConfig.BackWithActions -> TopAppBar(
            title = {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(config.title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                }
            },
            navigationIcon = {
                IconButton(onClick = config.onBack) {
                    Icon(painterResource(R.drawable.ic_icon_back), null, tint = Color.Unspecified, modifier = Modifier.size(28.dp))
                }
            },
            actions = {
                if (config.showActions) {
                    Row (
                        modifier = Modifier
                            .padding(end = 12.dp)
                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_alarm),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onAlarmClick() }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_bag),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { config.onCartClick() }
                        )
                    }

                }
            } ,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        is AppBarConfig.BackOnly -> CenterAlignedTopAppBar(
            title = {
                Text(
                    text = config.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            },
            navigationIcon = {
                IconButton(onClick = config.onBack, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_icon_back),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            actions = { /* 필요하면 아이콘 배치 */ },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        is AppBarConfig.SearchOnly -> TopAppBar(
            navigationIcon = {
                IconButton(onClick = { config.onBack}, modifier = Modifier.size(48.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_icon_back),
                        contentDescription = "뒤로가기",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            title = {
                var editing by rememberSaveable { mutableStateOf(false) }
                val focusRequester = remember { FocusRequester() }
                val keyboard = LocalSoftwareKeyboardController.current

                LaunchedEffect(editing) {
                    if (editing) {
                        focusRequester.requestFocus()
                        keyboard?.show()
                    } else {
                        keyboard?.hide()
                    }
                }

                if (!editing) {
                    val interaction = remember { MutableInteractionSource() }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F1F5))
                            .clickable(interactionSource = interaction, indication = null) { editing = true }
                            .padding(horizontal = 12.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_icon_search),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = if (config.query.isBlank()) "검색어를 입력하세요" else config.query,
                                color = if (config.query.isBlank()) Color(0xFF9E9E9E) else Color.Black,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                } else {
                    BasicTextField(
                        value = config.query,
                        onValueChange = config.onQueryChange,
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F1F5))
                            .padding(horizontal = 12.dp)
                            .focusRequester(focusRequester),
                        decorationBox = { inner ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_icon_search),
                                    contentDescription = null,
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Box(Modifier.weight(1f)) { inner() }
                                if (config.query.isNotEmpty()) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_icon_close),
                                        contentDescription = "지우기",
                                        tint = Color.Unspecified,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { config.onQueryChange("") }
                                    )
                                    Spacer(Modifier.width(8.dp))
                                }
                            }
                        }
                    )
                }
            }
            ,
            actions = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(78.dp)
                        .padding(end = 12.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = { config.onSubmit(config.query.trim()) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MainPurple,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("검색", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            ,
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )



        is AppBarConfig.Custom -> TopAppBar(
            title = { config.title() },
            navigationIcon = { config.navIcon?.invoke() },
            actions = { config.actions?.invoke() },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )
    }
}
