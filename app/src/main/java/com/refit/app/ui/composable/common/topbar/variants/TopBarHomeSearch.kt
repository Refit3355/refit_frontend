package com.refit.app.ui.composable.common.topbar.variants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.R
import com.refit.app.data.basic.model.AppBarConfig
import com.refit.app.ui.composable.common.topbar.TopBarTokens
import com.refit.app.ui.composable.common.topbar.ActionsRowCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeSearchTopBar(config: AppBarConfig.HomeSearch) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
        title = {
            val interaction = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF1F1F5))
                    .clickable(interactionSource = interaction, indication = null) {
                        config.onSearchClick()
                    }
                    .padding(horizontal = TopBarTokens.HPad),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "검색어를 입력하세요",
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
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
            Icon(
                painter = painterResource(R.drawable.ic_logo_text),
                contentDescription = "Re:fit",
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(start = TopBarTokens.HPad, end = TopBarTokens.HPad)
                    .size(TopBarTokens.Touch)
                    .clickable { config.onLogoClick() }
            )
        },
        actions = {
            if (config.showActions) {
                ActionsRowCompact(
                    onAlarmClick = config.onAlarmClick,
                    onCartClick = config.onCartClick
                )
            }
        }
    )
}
