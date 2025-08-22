package com.refit.app.ui.composable.common.topbar.variants

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.basic.model.AppBarConfig
import com.refit.app.ui.composable.common.topbar.TopBarTokens
import com.refit.app.ui.theme.MainPurple
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchOnlyTopBar(config: AppBarConfig.SearchOnly) {
    var editing by rememberSaveable { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(editing) {
        if (editing) {
            focusRequester.requestFocus()
            keyboard?.show()
        } else {
            keyboard?.hide()
        }
    }

    val handleBack = remember(config.onBack) {
        {
            keyboard?.hide()
            editing = false
            config.onQueryChange("")
            config.onBack?.invoke()
        }
    }

    BackHandler(enabled = true) { handleBack() }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            actionIconContentColor = Color.Unspecified
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    keyboard?.hide()
                    editing = false
                    config.onQueryChange("")
                    config.onBack?.invoke()
                },
                modifier = Modifier.size(TopBarTokens.Touch)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_icon_back),
                    contentDescription = "뒤로가기",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(TopBarTokens.Icon)
                )
            }
        },
        title = {
            if (!editing) {
                val interaction = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F1F5))
                        .clickable(interactionSource = interaction, indication = null) {
                            editing = true
                        }
                        .padding(horizontal = TopBarTokens.HPad),
                    contentAlignment = Alignment.CenterStart
                )            {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_icon_search),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = if (config.query.isBlank()) "검색어를 입력하세요." else config.query,
                            color = if (config.query.isBlank()) Color(0xFF9E9E9E) else Color.Black,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            } else {
                BasicTextField(
                    value = config.query,
                    onValueChange = config.onQueryChange,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), // 또는 ImeAction.Done
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            config.onSubmit(config.query.trim())
                            keyboard?.hide()
                            editing = false
                        },
                        onDone = {
                            // "완료" 키보드가 뜨는 기종/키보드 대비
                            config.onSubmit(config.query.trim())
                            keyboard?.hide()
                            editing = false
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF1F1F5))
                        .padding(horizontal = TopBarTokens.HPad)
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
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(R.drawable.ic_icon_close),
                                    contentDescription = "지우기",
                                    tint = Color.Unspecified,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clickable {
                                            config.onQueryChange("")
                                            config.onClear?.invoke()
                                            editing = false
                                        }
                                )
                            }
                        }
                    }
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(TopBarTokens.SymWidth)
                    .padding(end = TopBarTokens.HPad),
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
    )
}
