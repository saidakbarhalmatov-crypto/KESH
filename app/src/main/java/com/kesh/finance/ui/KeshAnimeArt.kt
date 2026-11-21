package com.kesh.finance.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.dp
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
                val boltColor = Color(0xFFFACC15).copy(alpha = 0.18f)
                drawLightningBolt(Offset(w * 0.85f, h * 0.05f), Offset(w * 0.62f, h * 0.34f), boltColor, 5f)
                drawLightningBolt(Offset(w * 0.12f, h * 0.55f), Offset(w * 0.35f, h * 0.85f), boltColor, 4f)
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
                val infCenter = Offset(w * 0.82f, h * 0.16f)
                for (i in 1..4) {
                    drawCircle(
                        color = Color(0xFF38BDF8).copy(alpha = 0.14f / i),
                        radius = w * 0.11f * i,
                        center = infCenter,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
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

fun DrawScope.drawSharinganArt(
    center: Offset,
    radius: Float,
    isMangekyo: Boolean = true
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFDC2626), Color(0xFF991B1B), Color(0xFF450A0A)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
    drawCircle(
        color = Color(0xFF090507),
        radius = radius,
        center = center,
        style = Stroke(width = radius * 0.11f)
    )
    val orbitRadius = radius * 0.56f
    drawCircle(
        color = Color(0xFF090507).copy(alpha = 0.75f),
        radius = orbitRadius,
        center = center,
        style = Stroke(width = radius * 0.045f)
    )

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
            val tomoeCenter = Offset(center.x, center.y - orbitRadius)
            drawCircle(
                color = Color(0xFF090507),
                radius = radius * 0.13f,
                center = tomoeCenter
            )
        }
    }

    drawCircle(color = Color(0xFF090507), radius = radius * 0.20f, center = center)
    drawCircle(color = Color(0xFFEF4444), radius = radius * 0.055f, center = center)
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
    val leftWing = Path().apply {
        moveTo(center.x - radius * 0.05f, center.y + radius * 0.55f)
        quadraticTo(center.x - radius * 0.85f, center.y + radius * 0.20f, center.x - radius * 0.72f, center.y - radius * 0.62f)
        quadraticTo(center.x - radius * 0.25f, center.y - radius * 0.25f, center.x - radius * 0.05f, center.y + radius * 0.55f)
        close()
    }
    val rightWing = Path().apply {
        moveTo(center.x + radius * 0.05f, center.y + radius * 0.55f)
        quadraticTo(center.x + radius * 0.85f, center.y + radius * 0.20f, center.x + radius * 0.72f, center.y - radius * 0.62f)
        quadraticTo(center.x + radius * 0.25f, center.y - radius * 0.25f, center.x + radius * 0.05f, center.y + radius * 0.55f)
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
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
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
    drawPath(path = path, color = color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
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
            quadraticTo(center.x - wingSpan * 0.55f, center.y - wingSpan * 0.55f, center.x - wingSpan, center.y - wingSpan * 0.12f)
            quadraticTo(center.x - wingSpan * 0.45f, center.y - wingSpan * 0.15f, center.x, center.y + wingSpan * 0.14f)
            quadraticTo(center.x + wingSpan * 0.45f, center.y - wingSpan * 0.15f, center.x + wingSpan, center.y - wingSpan * 0.12f)
            quadraticTo(center.x + wingSpan * 0.55f, center.y - wingSpan * 0.55f, center.x, center.y)
            close()
        }
        drawPath(crow, color = color)
    }
}
