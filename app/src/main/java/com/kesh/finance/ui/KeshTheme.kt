package com.kesh.finance.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin

enum class AnimeVisualType {
    NONE,
    ITACHI,
    MADARA,
    GOJO,
    YUTA,
    MIKASA,
    MARIN,
    ZENITSU,
    TANJIRO,
    HANJI,
    NARUTO,
    SHARINGAN,
    STORM,
    FLAME,
    MIST
}

enum class KeshThemeId(val id: String) {
    ITACHI("itachi"),
    MADARA("madara"),
    GOJO("gojo"),
    YUTA("yuta"),
    MIKASA("mikasa"),
    MARIN("marin"),
    DARK_PRO("dark_pro"),
    LIGHT_CLEAN("light_clean")
}

data class KeshThemeSpec(
    val id: String,
    val name: String,
    val motto: String,
    val subtitle: String,
    val symbolTag: String,
    val animeType: AnimeVisualType,
    val isAnime: Boolean,
    val isDark: Boolean,
    val isLight: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val textPrimary: Color,
    val textSecondary: Color
)

object KeshThemes {
    val all: List<KeshThemeSpec> = listOf(
        KeshThemeSpec(
            id = "itachi",
            name = "Itachi",
            motto = "Мангекё Шаринган • Красная луна и вороны",
            subtitle = "Красная луна, вороны и Шаринган",
            symbolTag = "SHARINGAN",
            animeType = AnimeVisualType.ITACHI,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF0A0506),
            surface = Color(0xFF170B0D),
            surfaceVariant = Color(0xFF281216),
            primary = Color(0xFFEF233C),
            secondary = Color(0xFF9B0000),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFFCBD5E1)
        ),
        KeshThemeSpec(
            id = "madara",
            name = "Madara",
            motto = "Вечный Мангекё • Пламя и Сусаноо",
            subtitle = "Багровое пламя и сила клана Учиха",
            symbolTag = "ETERNAL",
            animeType = AnimeVisualType.MADARA,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF0C0605),
            surface = Color(0xFF1C0D0A),
            surfaceVariant = Color(0xFF2F1510),
            primary = Color(0xFFFF4500),
            secondary = Color(0xFFDC2626),
            textPrimary = Color(0xFFFFF7ED),
            textSecondary = Color(0xFFFED7AA)
        ),
        KeshThemeSpec(
            id = "gojo",
            name = "Gojo",
            motto = "Безграничность • Шесть Глаз",
            subtitle = "Ледяной неон и бесконечность",
            symbolTag = "INFINITY",
            animeType = AnimeVisualType.GOJO,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF040B14),
            surface = Color(0xFF0A192F),
            surfaceVariant = Color(0xFF112240),
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFF818CF8),
            textPrimary = Color(0xFFF0F9FF),
            textSecondary = Color(0xFFBAE6FD)
        ),
        KeshThemeSpec(
            id = "yuta",
            name = "Yuta Okkotsu",
            motto = "Проклятая энергия • Чистая любовь",
            subtitle = "Глубокий фиолет и таинственная аура",
            symbolTag = "CURSED",
            animeType = AnimeVisualType.YUTA,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF090512),
            surface = Color(0xFF150D26),
            surfaceVariant = Color(0xFF23163D),
            primary = Color(0xFFA855F7),
            secondary = Color(0xFF6366F1),
            textPrimary = Color(0xFFFAF5FF),
            textSecondary = Color(0xFFE9D5FF)
        ),
        KeshThemeSpec(
            id = "mikasa",
            name = "Mikasa",
            motto = "Крылья Свободы • Алый шарф",
            subtitle = "Строгий графит и решительный алый акцент",
            symbolTag = "WINGS",
            animeType = AnimeVisualType.MIKASA,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF0B0C0E),
            surface = Color(0xFF16181D),
            surfaceVariant = Color(0xFF232730),
            primary = Color(0xFFE11D48),
            secondary = Color(0xFF64748B),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFFCBD5E1)
        ),
        KeshThemeSpec(
            id = "marin",
            name = "Marin",
            motto = "Закат в городе • Тёплые огни",
            subtitle = "Вечерний закат, мягкий розовый и тепло",
            symbolTag = "SUNSET",
            animeType = AnimeVisualType.MARIN,
            isAnime = true,
            isDark = true,
            isLight = false,
            background = Color(0xFF140B14),
            surface = Color(0xFF241322),
            surfaceVariant = Color(0xFF381E34),
            primary = Color(0xFFF472B6),
            secondary = Color(0xFFFB923C),
            textPrimary = Color(0xFFFFF1F2),
            textSecondary = Color(0xFFFBCFE8)
        ),
        KeshThemeSpec(
            id = "dark_pro",
            name = "Тёмный Графит",
            motto = "Строгий минимализм",
            subtitle = "Тёмный режим без лишних эффектов",
            symbolTag = "MINIMAL",
            animeType = AnimeVisualType.NONE,
            isAnime = false,
            isDark = true,
            isLight = false,
            background = Color(0xFF090D16),
            surface = Color(0xFF111827),
            surfaceVariant = Color(0xFF1F2937),
            primary = Color(0xFF10B981),
            secondary = Color(0xFF3B82F6),
            textPrimary = Color(0xFFF9FAFB),
            textSecondary = Color(0xFF9CA3AF)
        ),
        KeshThemeSpec(
            id = "light_clean",
            name = "Светлая классика",
            motto = "Чистый дневной режим",
            subtitle = "Светлый фон для яркого дня",
            symbolTag = "LIGHT",
            animeType = AnimeVisualType.NONE,
            isAnime = false,
            isDark = false,
            isLight = true,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE2E8F0),
            primary = Color(0xFF2563EB),
            secondary = Color(0xFF0EA5E9),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569)
        )
    )

    fun byId(id: String?): KeshThemeSpec {
        return all.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: all.first()
    }
}

@Composable
fun KeshAppTheme(
    spec: KeshThemeSpec,
    content: @Composable () -> Unit
) {
    val colorScheme = if (spec.isLight) {
        lightColorScheme(
            primary = spec.primary,
            secondary = spec.secondary,
            background = spec.background,
            surface = spec.surface,
            surfaceVariant = spec.surfaceVariant,
            onPrimary = Color.White,
            onBackground = spec.textPrimary,
            onSurface = spec.textPrimary
        )
    } else {
        darkColorScheme(
            primary = spec.primary,
            secondary = spec.secondary,
            background = spec.background,
            surface = spec.surface,
            surfaceVariant = spec.surfaceVariant,
            onPrimary = Color.White,
            onBackground = spec.textPrimary,
            onSurface = spec.textPrimary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun KeshAnimeAtmosphere(spec: KeshThemeSpec) {
    if (!spec.isAnime) return

    val infiniteTransition = rememberInfiniteTransition(label = "anime_bg")
    val drift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        val auraCenter = Offset(
            x = w * (0.72f - drift * 0.08f),
            y = h * (0.14f + drift * 0.03f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    spec.primary.copy(alpha = 0.24f),
                    spec.secondary.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = auraCenter,
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = auraCenter
        )

        val bottomGlow = Offset(x = w * 0.25f, y = h * 0.88f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    spec.secondary.copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = bottomGlow,
                radius = w * 0.7f
            ),
            radius = w * 0.7f,
            center = bottomGlow
        )

        val particlePositions = listOf(
            Offset(w * (0.18f + drift * 0.06f), h * 0.22f),
            Offset(w * (0.82f - drift * 0.05f), h * 0.31f),
            Offset(w * (0.28f + drift * 0.04f), h * 0.62f),
            Offset(w * (0.76f - drift * 0.07f), h * 0.74f)
        )

        particlePositions.forEachIndexed { idx, pos ->
            if (spec.id == "itachi" || spec.id == "madara" || spec.id == "mikasa") {
                val wingSpan = 16f + idx * 3f
                val path = Path().apply {
                    moveTo(pos.x - wingSpan, pos.y + 4f)
                    quadraticBezierTo(pos.x - wingSpan * 0.4f, pos.y - 8f, pos.x, pos.y)
                    quadraticBezierTo(pos.x + wingSpan * 0.4f, pos.y - 8f, pos.x + wingSpan, pos.y + 4f)
                }
                drawPath(
                    path = path,
                    color = spec.primary.copy(alpha = 0.35f),
                    style = Stroke(width = 2.5f, cap = StrokeCap.Round)
                )
            } else {
                drawCircle(
                    color = spec.primary.copy(alpha = 0.28f),
                    radius = 5f + idx * 2f,
                    center = pos
                )
            }
        }
    }
}

@Composable
fun ThemeEmblemBadge(
    spec: KeshThemeSpec,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emblem_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(spec.primary.copy(alpha = 0.45f), Color.Transparent),
                center = center,
                radius = r
            ),
            radius = r,
            center = center
        )

        drawCircle(
            color = spec.surface,
            radius = r * 0.82f,
            center = center
        )
        drawCircle(
            color = spec.primary,
            radius = r * 0.82f,
            center = center,
            style = Stroke(width = r * 0.08f)
        )

        rotate(degrees = if (spec.isAnime) angle else 0f, pivot = center) {
            when (spec.id) {
                "itachi", "madara" -> {
                    drawCircle(
                        color = spec.primary.copy(alpha = 0.55f),
                        radius = r * 0.48f,
                        center = center,
                        style = Stroke(width = r * 0.05f)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = r * 0.16f,
                        center = center
                    )
                    for (i in 0 until 3) {
                        val rad = Math.toRadians((i * 120.0) - 90.0)
                        val tx = center.x + (r * 0.48f * cos(rad)).toFloat()
                        val ty = center.y + (r * 0.48f * sin(rad)).toFloat()
                        drawCircle(
                            color = Color.Black,
                            radius = r * 0.11f,
                            center = Offset(tx, ty)
                        )
                    }
                }
                "gojo", "yuta" -> {
                    drawCircle(
                        color = spec.primary,
                        radius = r * 0.42f,
                        center = Offset(center.x - r * 0.14f, center.y),
                        style = Stroke(width = r * 0.06f)
                    )
                    drawCircle(
                        color = spec.secondary,
                        radius = r * 0.42f,
                        center = Offset(center.x + r * 0.14f, center.y),
                        style = Stroke(width = r * 0.06f)
                    )
                }
                else -> {
                    drawCircle(
                        color = spec.primary,
                        radius = r * 0.32f,
                        center = center
                    )
                }
            }
        }
    }
}

