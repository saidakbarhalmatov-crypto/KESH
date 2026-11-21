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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    size: Dp = 44.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(spec.surfaceVariant, spec.background)
            ),
            radius = w * 0.48f,
            center = center
        )

        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(spec.primary, spec.secondary, spec.primary)
            ),
            radius = w * 0.46f,
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
            style = Stroke(
                width = w * 0.085f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = spec.secondary,
            radius = w * 0.055f,
            center = Offset(w * 0.72f, h * 0.28f)
        )
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            KeshLuxuryEmblem(spec = spec, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "KESH",
                        color = spec.textPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        letterSpacing = 1.2.sp
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
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isAutoBankActive) Color(0xFF22C55E) else Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAutoBankActive) "20 банков КР" else "Ручной режим",
                        color = spec.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { onOpenThemes() }
                .border(1.dp, spec.primary.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
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
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = spec.name,
                    color = spec.textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
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
