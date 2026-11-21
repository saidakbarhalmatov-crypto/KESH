package com.kesh.finance.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.Category
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun KeshLuxuryEmblem(
    spec: KeshThemeSpec,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)
        val r = w * 0.46f

        when (spec.animeType) {
            AnimeVisualType.ITACHI -> {
                drawSharinganArt(center = center, radius = r, isMangekyo = true)
            }
            AnimeVisualType.ZENITSU -> {
                drawCircle(color = Color(0xFF201A0B), radius = r, center = center)
                drawCircle(color = spec.primary, radius = r, center = center, style = Stroke(w * 0.06f))
                drawLightningBolt(
                    start = Offset(w * 0.56f, h * 0.18f),
                    end = Offset(w * 0.44f, h * 0.82f),
                    color = spec.primary,
                    strokeWidth = w * 0.09f
                )
            }
            AnimeVisualType.GOJO -> {
                drawSixEyesArt(center = center, radius = r)
            }
            AnimeVisualType.TANJIRO -> {
                drawCircle(color = Color(0xFF0F221E), radius = r, center = center)
                drawArc(
                    color = spec.primary,
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.08f, h * 0.08f),
                    size = Size(w * 0.84f, h * 0.84f),
                    style = Stroke(w * 0.09f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = spec.secondary,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(w * 0.08f, h * 0.08f),
                    size = Size(w * 0.84f, h * 0.84f),
                    style = Stroke(w * 0.09f, cap = StrokeCap.Round)
                )
                drawCircle(color = spec.secondary, radius = r * 0.25f, center = center)
            }
            AnimeVisualType.HANJI -> {
                drawCircle(color = Color(0xFF171E16), radius = r, center = center)
                drawCircle(color = spec.primary, radius = r, center = center, style = Stroke(w * 0.05f))
                drawWingsOfFreedom(
                    center = center,
                    radius = r * 0.78f,
                    leftColor = Color.White,
                    rightColor = Color(0xFF38BDF8)
                )
            }
            AnimeVisualType.NARUTO -> {
                drawCircle(color = Color(0xFF23150A), radius = r, center = center)
                drawCircle(color = spec.secondary, radius = r, center = center, style = Stroke(w * 0.06f))
                drawUzumakiSpiral(
                    center = center,
                    maxRadius = r * 0.72f,
                    color = spec.primary,
                    strokeWidth = w * 0.07f
                )
            }
            AnimeVisualType.NONE -> {
                drawCircle(
                    brush = Brush.linearGradient(listOf(spec.surfaceVariant, spec.background)),
                    radius = w * 0.48f,
                    center = center
                )
                drawCircle(
                    brush = Brush.sweepGradient(listOf(spec.primary, spec.secondary, spec.primary)),
                    radius = r,
                    center = center,
                    style = Stroke(width = w * 0.055f)
                )
                val kPath = Path().apply {
                    moveTo(w * 0.35f, h * 0.27f)
                    lineTo(w * 0.35f, h * 0.73f)
                    moveTo(w * 0.65f, h * 0.27f)
                    lineTo(w * 0.38f, h * 0.50f)
                    lineTo(w * 0.67f, h * 0.73f)
                }
                drawPath(
                    path = kPath,
                    color = spec.primary,
                    style = Stroke(width = w * 0.085f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawCircle(color = spec.secondary, radius = w * 0.055f, center = Offset(w * 0.72f, h * 0.28f))
            }
        }
    }
}

@Composable
fun KeshTopHeader(
    userName: String,
    spec: KeshThemeSpec,
    isAutoBankActive: Boolean,
    onOpenThemes: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            KeshLuxuryEmblem(spec = spec, size = 44.dp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "KESH",
                        color = spec.textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.1.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = spec.primary.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "2.0 PRO",
                            color = spec.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        color = spec.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isAutoBankActive) Color(0xFF22C55E) else Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAutoBankActive) "20 банков" else "Ручной",
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onOpenThemes() }
                .border(1.dp, spec.primary.copy(alpha = 0.45f), RoundedCornerShape(14.dp)),
            color = spec.surface
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Palette,
                    contentDescription = "Темы",
                    tint = spec.primary,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = spec.name,
                    color = spec.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(
    category: Category,
    size: Dp = 42.dp
) {
    val catColor = Color(category.colorHex)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(13.dp))
            .background(catColor.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = categoryIcon(category),
            contentDescription = category.title,
            tint = catColor,
            modifier = Modifier.size(size * 0.52f)
        )
    }
}

fun categoryIcon(category: Category): ImageVector {
    return when (category) {
        Category.FOOD -> Icons.Rounded.Fastfood
        Category.GROCERIES -> Icons.Rounded.ShoppingCart
        Category.TRANSPORT -> Icons.Rounded.DirectionsCar
        Category.SHOPPING -> Icons.Rounded.ShoppingBag
        Category.BILLS -> Icons.Rounded.Bolt
        Category.HEALTH -> Icons.Rounded.LocalHospital
        Category.ENTERTAINMENT -> Icons.Rounded.Movie
        Category.TRANSFER -> Icons.Rounded.SwapHoriz
        Category.OTHER_EXPENSE -> Icons.Rounded.MoreHoriz
        Category.SALARY -> Icons.Rounded.Payments
        Category.FREELANCE -> Icons.Rounded.Work
        Category.TRANSFER_IN -> Icons.Rounded.AccountBalance
        Category.CASHBACK -> Icons.Rounded.CardGiftcard
        Category.OTHER_INCOME -> Icons.Rounded.AccountBalanceWallet
    }
}

fun formatSom(amount: Double): String {
    val symbols = DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }
    val pattern = if (amount % 1.0 == 0.0) "#,##0" else "#,##0.00"
    val formatter = DecimalFormat(pattern, symbols)
    return "${formatter.format(amount)} сом"
}
