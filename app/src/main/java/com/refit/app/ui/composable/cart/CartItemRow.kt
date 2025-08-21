package com.refit.app.ui.composable.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.refit.app.R
import com.refit.app.data.cart.model.CartItemDto

@Composable
fun CartItemRow(
    item: CartItemDto,
    checked: Boolean,
    onCheckChanged: () -> Unit,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = checked, onCheckedChange = { onCheckChanged() })
            Spacer(Modifier.width(8.dp))

            AsyncImage(
                model = item.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(item.brandName, color = Color.Gray, fontSize = 12.sp)
                Text(item.productName, fontWeight = FontWeight.Medium, fontSize = 14.sp, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("%,d원".format(item.discountedPrice), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE5E5EA), RoundedCornerShape(8.dp))
                ) {
                    Text(" - ", modifier = Modifier.clickable { onMinus() }.padding(horizontal = 10.dp, vertical = 4.dp))
                    Text(item.cartCnt.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                    Text(" + ", modifier = Modifier.clickable { onPlus() }.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                painterResource(R.drawable.ic_icon_close),
                contentDescription = "삭제",
                modifier = Modifier.size(20.dp).clickable { onRemove() },
                tint = Color(0xFFB0B0B0)
            )
        }
    }
}
