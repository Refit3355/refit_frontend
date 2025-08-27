package com.refit.app.ui.composable.combiking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.refit.app.data.combination.model.CombinationProductDto

@Composable
fun SmallProductCard(product: CombinationProductDto, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = product.thumbnailUrl,
            contentDescription = product.productName,
            modifier = Modifier.size(80.dp)
        )
        Text(product.brandName, style = MaterialTheme.typography.bodySmall)
        Text(product.productName, style = MaterialTheme.typography.bodySmall)
        Text("${product.discountedPrice}원", fontWeight = FontWeight.Bold)
    }
}