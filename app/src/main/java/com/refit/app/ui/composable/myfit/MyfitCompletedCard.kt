package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.myfit.model.MemberProductItem
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.GreyOutline

@Composable
fun MyfitCompletedCard(
    item: MemberProductItem,
    onRecommend: () -> Unit,
    onClickItem: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = Color.White,
        tonalElevation = 0.dp,
        border = BorderStroke(0.5.dp, GreyOutline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            MyfitThumb(item.thumbnailUrl)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f).clickable { onClickItem() }) {
                BrandPill(item.brandName); Spacer(Modifier.height(6.dp))
                Text(
                    item.productName,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = DarkBlack,
                    fontSize = 13.sp
                )
                item.usagePeriodText?.let {
                    Spacer(Modifier.height(6.dp));
                    Text(
                        it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            RecommendCircleButton(onClick = onRecommend)
        }
    }
}
