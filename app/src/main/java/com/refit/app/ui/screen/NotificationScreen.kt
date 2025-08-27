package com.refit.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.refit.app.R
import com.refit.app.data.push.modelAndView.NotificationViewModel
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.abs

@Composable
fun NotificationScreen(vm: NotificationViewModel = viewModel()) {
    // 1) 먼저 목록을 가져와 화면에 그린다.
    LaunchedEffect(Unit) { vm.refresh() }
    val ui by vm.ui.collectAsState()

    // 2) 목록이 화면에 보이기 시작하면(최초 1회만) 서버에 전체 읽음 처리 호출
    var readAllSent by remember { mutableStateOf(false) }
    LaunchedEffect(ui.items) {
        if (!readAllSent && ui.items.isNotEmpty()) {
            // 서버에 전체 읽음 처리 — 리스트는 새로고침하지 않는다
            vm.markAllReadSilently()
            readAllSent = true
        }
    }

    when {
        ui.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        ui.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("알림을 불러오지 못했습니다. 잠시 후 다시 시도해주세요.")
        }
        else -> LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                    start = 28.dp,
                    end   = 28.dp,
                    top   = 12.dp,
                    bottom= 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(ui.items) { row ->
                // 서버가 내려준 '들어오기 직전'의 읽음 상태를 그대로 사용해 점을 표시
                val wasUnread = !anyToBool(row.isRead)
                NotificationCard(
                    title = row.title.orEmpty(),
                    body = row.body.orEmpty(),
                    type = row.type.orEmpty(),
                    createdAt = row.createdAt,
                    showUnreadDot = wasUnread,
                    deeplink = row.deeplink
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// 0/1 → Boolean 변환 헬퍼
private fun anyToBool(v: Any?): Boolean = when (v) {
    is Boolean -> v
    is Number  -> v.toInt() != 0
    is String  -> v == "1" || v.equals("true", true) || v.equals("y", true) || v.equals("yes", true)
    else       -> false
}

@Composable
private fun NotificationCard(
    title: String,
    body: String,
    type: String,
    createdAt: String?,
    showUnreadDot: Boolean,
    deeplink: String?
) {
    val ctx = LocalContext.current
    val hasLink = !deeplink.isNullOrBlank()

    val timeText = remember(createdAt) {
        val dt = parseZonedOrNull(createdAt)
        formatRelativeOrDate(dt, ZonedDateTime.now(ZoneId.systemDefault()))
    }

    val bg = Color(0xFFEBEBEB)
    val radius = 20.dp

    Surface(
        color = bg,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (hasLink) Modifier.clickable { openDeeplink(ctx, deeplink!!) } else Modifier
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                painter = painterResource(typeIconRes(type)),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(20.dp)
                    .padding(top = 2.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("·", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = timeText,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (showUnreadDot) {
                        Spacer(Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun openDeeplink(context: android.content.Context, url: String) {
    runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}

// 타입별 아이콘 매핑
@DrawableRes
private fun typeIconRes(type: String): Int = when (type.uppercase()) {
    "PAYMENT_COMPLETED", "ORDER_PAID", "PAYMENT_SUCCESS" -> R.drawable.ic_check
    "PAYMENT_CANCELED", "ORDER_CANCELLED", "PAYMENT_CANCEL" -> R.drawable.ic_x
    "EXPIRY_IMMINENT", "EXPIRATION_SOON", "MYFIT_EXPIRY_SOON" -> R.drawable.ic_refresh
    else -> R.drawable.ic_check
}

// createdAt을 ZonedDateTime으로 파싱 (ISO-8601 우선)
private fun parseZonedOrNull(raw: String?): ZonedDateTime? {
    if (raw.isNullOrBlank()) return null
    return try {
        ZonedDateTime.parse(raw)
    } catch (_: DateTimeParseException) {
        try {
            LocalDateTime.parse(raw).atZone(ZoneId.systemDefault())
        } catch (_: DateTimeParseException) {
            null
        }
    }
}

// 7일 이내면 상대시간, 7일 이상이면 YYYY.MM.DD
private fun formatRelativeOrDate(
    dt: ZonedDateTime?,
    now: ZonedDateTime = ZonedDateTime.now()
): String {
    if (dt == null) return ""
    val d = Duration.between(dt, now)
    val days = abs(d.toDays())
    return if (days >= 7) {
        dt.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
    } else {
        val hours = abs(d.toHours())
        val mins = abs(d.toMinutes())
        when {
            hours >= 24 -> "${days}일 전"
            hours >= 1  -> "${hours}시간 전"
            mins >= 1   -> "${mins}분 전"
            else        -> "방금 전"
        }
    }
}
