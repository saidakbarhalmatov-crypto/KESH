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
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun KeshAnimeAtmosphere(
    spec: KeshThemeSpec,
    modifier: Modifier = Modifier
) {
    if (!spec.isAnime) return

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        when (spec.animeType) {
            AnimeVisualType.ITACHI -> {
                // Алая луна и силуэты летящих ворон Итачи
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEF4444).copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(w * 0.85f, h * 0.14f),
                        radius = w * 0.45f
                    ),
                    radius = w * 0.45f,
                    center = Offset(w * 0.85f, h * 0.14f)
                )
                drawCrowSilhouette(Offset(w * 0.78f, h * 0.12f), w * 0.13f, -15f, Color(0xFFEF4444).copy(alpha = 0.28f))
                drawCrowSilhouette(Offset(w * 0.90f, h * 0.20f), w * 0.09f, -25f, Color(0xFFEF4444).copy(alpha = 0.20f))
                drawCrowSilhouette(Offset(w * 0.15f, h * 0.68f), w * 0.14f, 18f, Color(0xFFEF4444).copy(alpha = 0.14f))
                drawCrowSilhouette(Offset(w * 0.82f, h * 0.82f), w * 0.11f, -10f, Color(0xFFEF4444).copy(alpha = 0.16f))
            }

            AnimeVisualType.ZENITSU -> {
                // Золотые молнии и треугольники хаори Зеницу
                val boltColor = Color(0xFFFACC15).copy(alpha = 0.18f)
                drawLightningBolt(Offset(w * 0.85f, h * 0.05f), Offset(w * 0.62f, h * 0.34f), boltColor, 5f)
                drawLightningBolt(Offset(w * 0.12f, h * 0.55f), Offset(w * 0.35f, h * 0.85f), boltColor, 4f)
                // Треугольники уроко
                val triColor = Color(0xFFFB923C).copy(alpha = 0.10f)
                for (i in 0..3) {
                    val cx = w * (0.72f + i * 0.07f)
                    val cy = h * (0.78f + (i % 2) * 0.05f)
                    val r = 18.dp.toPx()
                    val tri = Path().apply {
                        moveTo(cx, cy - r)
                        lineTo(cx - r * 0.86f, cy + r * 0.5f)
                        lineTo(cx + r * 0.86f, cy + r * 0.5f)
                        close()
                    }
                    drawPath(tri, color = triColor)
                }
            }

            AnimeVisualType.GOJO -> {
                // Бесконечность: кольца Муген + Красный и Синий
                val infCenter = Offset(w * 0.82f, h * 0.16f)
                for (i in 1..4) {
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = 0.14f / i),
                        radius = w * 0.11f * i,
                        center = infCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
                // Синий (Ао) и Красный (Ака)
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF38BDF8).copy(alpha = 0.35f), Color.Transparent),
                        center = Offset(w * 0.18f, h * 0.76f),
                        radius = w * 0.18f
                    ),
                    radius = w * 0.18f,
                    center = Offset(w * 0.18f, h * 0.76f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFFEF4444).copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(w * 0.84f, h * 0.72f),
                        radius = w * 0.18f
                    ),
                    radius = w * 0.18f,
                    center = Offset(w * 0.84f, h * 0.72f)
                )
            }

            AnimeVisualType.TANJIRO -> {
                // Шахматный узор хаори и поток воды/огня
                val sq = 22.dp.toPx()
                val checkColor = Color(0xFF14B8A6).copy(alpha = 0.08f)
                for (row in 0..3) {
                    for (col in 0..3) {
                        if ((row + col) % 2 == 0) {
                            drawRect(
                                color = checkColor,
                                topLeft = Offset(w - (4 - col) * sq, h * 0.72f + row * sq),
                                size = Size(sq, sq)
                            )
                        }
                    }
                }
                val wavePath = Path().apply {
                    moveTo(0f, h * 0.24f)
                    cubicTo(w * 0.3f, h * 0.16f, w * 0.65f, h * 0.32f, w, h * 0.20f)
                }
                drawPath(
                    path = wavePath,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF14B8A6).copy(alpha = 0.22f), Color(0xFFF97316).copy(alpha = 0.22f))
                    ),
                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            AnimeVisualType.HANJI -> {
                // Крылья Свободы и тактическая сетка Разведкорпуса
                drawWingsOfFreedom(
                    center = Offset(w * 0.80f, h * 0.16f),
                    radius = w * 0.18f,
                    leftColor = Color.White.copy(alpha = 0.12f),
                    rightColor = Color(0xFF38BDF8).copy(alpha = 0.16f)
                )
                drawCircle(
                    color = Color(0xFF84CC16).copy(alpha = 0.10f),
                    radius = w * 0.35f,
                    center = Offset(w * 0.15f, h * 0.82f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            AnimeVisualType.NARUTO -> {
                // Спираль Узумаки и сферы Шести Путей
                drawUzumakiSpiral(
                    center = Offset(w * 0.82f, h * 0.15f),
                    maxRadius = w * 0.17f,
                    color = Color(0xFFF97316).copy(alpha = 0.22f),
                    strokeWidth = 3.dp.toPx()
                )
                val orbCenters = listOf(
                    Offset(w * 0.14f, h * 0.74f),
                    Offset(w * 0.26f, h * 0.82f),
                    Offset(w * 0.86f, h * 0.78f)
                )
                orbCenters.forEach { pos ->
                    drawCircle(
                        color = Color(0xFFFACC15).copy(alpha = 0.16f),
                        radius = 12.dp.toPx(),
                        center = pos
                    )
                }
            }

            AnimeVisualType.NONE -> Unit
        }
    }
}

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

// --- Векторные аниме-элементы для эмблем, фона и круга аналитики ---

fun DrawScope.drawSharinganArt(
    center: Offset,
    radius: Float,
    isMangekyo: Boolean = true
) {
    // Алая радужка Шарингана
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFDC2626), Color(0xFF991B1B), Color(0xFF450A0A)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
    // Чёрный внешний обод
    drawCircle(
        color = Color(0xFF090507),
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.11f)
    )
    // Внутреннее кольцо орбиты томоэ
    val orbitRadius = radius * 0.56f
    drawCircle(
        color = Color(0xFF090507).copy(alpha = 0.75f),
        radius = orbitRadius,
        center = center,
        style = Stroke(width = radius * 0.045f)
    )

    // 3 лопасти Мангекё или 3 томоэ
    for (i in 0 until 3) {
        val angleDeg = i * 120f - 90f
        rotate(degrees = angleDeg, pivot = center) {
            if (isMangekyo) {
                val blade = Path().apply {
                    moveTo(center.x, center.y - radius * 0.16f)
                    quadraticTo(
                        center.x + radius * 0.42f,
                        center.y - radius * 0.45f,
                        center.x,
                        center.y - radius * 0.86f
                    )
                    quadraticTo(
                        center.x + radius * 0.12f,
                        center.y - radius * 0.45f,
                        center.x,
                        center.y - radius * 0.16f
                    )
                    close()
                }
                drawPath(blade, color = Color(0xFF090507))
            }
            // Томоэ-капля на орбите
            val tomoeCenter = Offset(center.x, center.y - orbitRadius)
            drawCircle(
                color = Color(0xFF090507),
                radius = radius * 0.13f,
                center = tomoeCenter
            )
        }
    }

    // Центральный зрачок
    drawCircle(
        color = Color(0xFF090507),
        radius = radius * 0.20f,
        center = center
    )
    drawCircle(
        color = Color(0xFFEF4444),
        radius = radius * 0.055f,
        center = center
    )
}

fun DrawScope.drawSixEyesArt(center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFE0F2FE), Color(0xFF38BDF8), Color(0xFF1D4ED8)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
    drawCircle(
        color = Color(0xFF93C5FD),
        radius = radius * 0.68f,
        center = center,
        style = Stroke(width = radius * 0.05f)
    )
    for (i in 0 until 12) {
        val rad = (i * 30.0) * PI / 180.0
        drawLine(
            color = Color.White.copy(alpha = 0.55f),
            start = Offset(
                center.x + (radius * 0.28f * cos(rad)).toFloat(),
                center.y + (radius * 0.28f * sin(rad)).toFloat()
            ),
            end = Offset(
                center.x + (radius * 0.85f * cos(rad)).toFloat(),
                center.y + (radius * 0.85f * sin(rad)).toFloat()
            ),
            strokeWidth = radius * 0.03f
        )
    }
    drawCircle(color = Color(0xFF060A17), radius = radius * 0.22f, center = center)
}

fun DrawScope.drawWingsOfFreedom(
    center: Offset,
    radius: Float,
    leftColor: Color,
    rightColor: Color
) {
    // Левое крыло
    val leftWing = Path().apply {
        moveTo(center.x - radius * 0.05f, center.y + radius * 0.55f)
        quadraticTo(
            center.x - radius * 0.85f,
            center.y + radius * 0.20f,
            center.x - radius * 0.72f,
            center.y - radius * 0.62f
        )
        quadraticTo(
            center.x - radius * 0.25f,
            center.y - radius * 0.25f,
            center.x - radius * 0.05f,
            center.y + radius * 0.55f
        )
        close()
    }
    // Правое крыло
    val rightWing = Path().apply {
        moveTo(center.x + radius * 0.05f, center.y + radius * 0.55f)
        quadraticTo(
            center.x + radius * 0.85f,
            center.y + radius * 0.20f,
            center.x + radius * 0.72f,
            center.y - radius * 0.62f
        )
        quadraticTo(
            center.x + radius * 0.25f,
            center.y - radius * 0.25f,
            center.x + radius * 0.05f,
            center.y + radius * 0.55f
        )
        close()
    }
    drawPath(leftWing, color = leftColor)
    drawPath(rightWing, color = rightColor)
}

fun DrawScope.drawUzumakiSpiral(
    center: Offset,
    maxRadius: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    val turns = 2.6f
    val steps = 70
    for (i in 0..steps) {
        val progress = i.toFloat() / steps
        val angle = progress * turns * 2f * PI.toFloat()
        val r = progress * maxRadius
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

fun DrawScope.drawLightningBolt(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    val midX = (start.x + end.x) / 2f
    val midY = (start.y + end.y) / 2f
    val dx = (end.x - start.x) * 0.22f
    val path = Path().apply {
        moveTo(start.x, start.y)
        lineTo(midX - dx, midY - (end.y - start.y) * 0.06f)
        lineTo(midX + dx, midY + (end.y - start.y) * 0.06f)
        lineTo(end.x, end.y)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

private fun DrawScope.drawCrowSilhouette(
    center: Offset,
    wingSpan: Float,
    angleDeg: Float,
    color: Color
) {
    rotate(degrees = angleDeg, pivot = center) {
        val crow = Path().apply {
            moveTo(center.x, center.y)
            quadraticTo(
                center.x - wingSpan * 0.55f,
                center.y - win
