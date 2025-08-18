package com.refit.app.ui.composable.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupTopBar(
    title: String,
    stepIndex: Int,
    stepCount: Int = 3,
    onBack: () -> Unit
) {
   val progress = (stepIndex + 1).toFloat() / stepCount

   Column (
       modifier = Modifier
           .fillMaxWidth()
           .background(MaterialTheme.colorScheme.surface)
   ){
       TopAppBar(
           title = {Text(title)},
           navigationIcon = {
               IconButton(onClick = onBack) {
                   Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
               }
           }
       )
       // 진행바
       Box(
           Modifier
               .padding(horizontal = 16.dp)
               .height(8.dp)
               .fillMaxWidth()
               .clip(RoundedCornerShape(100))
               .background(MaterialTheme.colorScheme.surfaceVariant)
       ) {
           Box(
               Modifier
                   .fillMaxHeight()
                   .fillMaxWidth(progress)
                   .clip(RoundedCornerShape(100))
                   .background(MaterialTheme.colorScheme.primary)
           )
       }
       Spacer(Modifier.height(12.dp))
   }
}