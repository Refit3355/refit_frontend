package com.refit.app.ui.composable.myfit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.refit.app.data.myfit.model.PurchasedProductDto
import com.refit.app.ui.theme.GreyText
import com.refit.app.ui.theme.DarkBlack
import com.refit.app.ui.theme.GreyOutline

@Composable
fun MyfitProductCard(
    item: PurchasedProductDto,
    onStartUsing: () -> Unit,
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
                BrandPill(item.brandName)
                Spacer(Modifier.height(6.dp))
                Text(item.productName, style = MaterialTheme.typography.labelMedium, maxLines = 2,
                    minLines = 2, overflow = TextOverflow.Ellipsis, color = DarkBlack, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("구매 일시: ${item.purchaseDate}  /  ${item.itemCount}개", style = MaterialTheme.typography.bodySmall, color = GreyText)
            }
            Spacer(Modifier.width(12.dp))
            RegisterCircleButton(onClick = onStartUsing)
        }
    }
}
