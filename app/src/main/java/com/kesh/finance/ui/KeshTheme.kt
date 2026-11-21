package com.kesh.finance.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class KeshThemeSpec(
    val id: String,
    val name: String,
    val subtitle: String,
    val isAnime: Boolean,
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
        // 5 базовых тем
        KeshThemeSpec(
            id = "royal_gold",
            name = "Royal Gold",
            subtitle = "Премиальный чёрно-золотой",
            isAnime = false,
            isLight = false,
            background = Color(0xFF090C14),
            surface = Color(0xFF121826),
            surfaceVariant = Color(0xFF1C2538),
            primary = Color(0xFFD4AF37),
            secondary = Color(0xFF38BDF8),
            textPrimary = Color(0xFFF8FAFC),
            textSecondary = Color(0xFF94A3B8)
        ),
        KeshThemeSpec(
            id = "ocean",
            name = "Ocean Deep",
            subtitle = "Глубокий синий и бирюза",
            isAnime = false,
            isLight = false,
            background = Color(0xFF061320),
            surface = Color(0xFF0D2136),
            surfaceVariant = Color(0xFF15314F),
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFF2DD4BF),
            textPrimary = Color(0xFFF0F9FF),
            textSecondary = Color(0xFF93C5FD)
        ),
        KeshThemeSpec(
            id = "forest_green",
            name = "Forest Green",
            subtitle = "Изумрудный финансовый стиль",
            isAnime = false,
            isLight = false,
            background = Color(0xFF071510),
            surface = Color(0xFF0F261E),
            surfaceVariant = Color(0xFF183A2E),
            primary = Color(0xFF10B981),
            secondary = Color(0xFFA3E635),
            textPrimary = Color(0xFFECFDF5),
            textSecondary = Color(0xFF86EFAC)
        ),
        KeshThemeSpec(
            id = "thunder",
            name = "Thunder Volt",
            subtitle = "Графит и электрический неон",
            isAnime = false,
            isLight = false,
            background = Color(0xFF0D0B14),
            surface = Color(0xFF181426),
            surfaceVariant = Color(0xFF251F3B),
            primary = Color(0xFFA855F7),
            secondary = Color(0xFFFACC15),
            textPrimary = Color(0xFFFAF5FF),
            textSecondary = Color(0xFFC4B5FD)
        ),
        KeshThemeSpec(
            id = "pure_light",
            name = "Pure Light",
            subtitle = "Чистая светлая тема",
            isAnime = false,
            isLight = true,
            background = Color(0xFFF4F6FA),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE8EEF5),
            primary = Color(0xFF0F172A),
            secondary = Color(0xFF0284C7),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569)
        ),

        // 6 аниме-тем
        KeshThemeSpec(
            id = "itachi",
            name = "Itachi",
            subtitle = "Алый Шаринган и вороны",
            isAnime = true,
            isLight = false,
            background = Color(0xFF0D0709),
            surface = Color(0xFF1A0D12),
            surfaceVariant = Color(0xFF2B141C),
            primary = Color(0xFFEF4444),
            secondary = Color(0xFFF87171),
            textPrimary = Color(0xFFFFF1F2),
            textSecondary = Color(0xFFFDA4AF)
        ),
        KeshThemeSpec(
            id = "zenitsu",
            name = "Zenitsu",
            subtitle = "Дыхание Грома: Первый стиль",
            isAnime = true,
            isLight = false,
            background = Color(0xFF120F07),
            surface = Color(0xFF211B0D),
            surfaceVariant = Color(0xFF332912),
            primary = Color(0xFFFACC15),
            secondary = Color(0xFFFB923C),
            textPrimary = Color(0xFFFEFCE8),
            textSecondary = Color(0xFFFDE047)
        ),
        KeshThemeSpec(
            id = "gojo",
            name = "Gojo",
            subtitle = "Шесть Глаз и Бесконечность",
            isAnime = true,
            isLight = false,
            background = Color(0xFF070B19),
            surface = Color(0xFF0F1730),
            surfaceVariant = Color(0xFF1A264D),
            primary = Color(0xFF60A5FA),
            secondary = Color(0xFFC084FC),
            textPrimary = Color(0xFFEFF6FF),
            textSecondary = Color(0xFF93C5FD)
        ),
        KeshThemeSpec(
            id = "tanjiro",
            name = "Tanjiro",
            subtitle = "Танец Бога Огня и Воды",
            isAnime = true,
            isLight = false,
            background = Color(0xFF081412),
            surface = Color(0xFF102420),
            surfaceVariant = Color(0xFF183630),
            primary = Color(0xFF14B8A6),
            secondary = Color(0xFFF97316),
            textPrimary = Color(0xFFF0FDFA),
            textSecondary = Color(0xFF5EEAD4)
        ),
        KeshThemeSpec(
            id = "hanji",
            name = "Hanji",
            subtitle = "Крылья Свободы и исследования",
            isAnime = true,
            isLight = false,
            background = Color(0xFF0E110D),
            surface = Color(0xFF1A2018),
            surfaceVariant = Color(0xFF273024),
            primary = Color(0xFF84CC16),
            secondary = Color(0xFFD97706),
            textPrimary = Color(0xFFF7FEE7),
            textSecondary = Color(0xFFBEF264)
        ),
        KeshThemeSpec(
            id = "naruto",
            name = "Naruto",
            subtitle = "Режим Мудреца Шести Путей",
            isAnime = true,
            isLight = false,
            background = Color(0xFF140D07),
            surface = Color(0xFF24170C),
            surfaceVariant = Color(0xFF382312),
            primary = Color(0xFFF97316),
            secondary = Color(0xFFFACC15),
            textPrimary = Color(0xFFFFF7ED),
            textSecondary = Color(0xFFFDBA74)
        )
    )

    fun byId(id: String): KeshThemeSpec {
        return all.find { it.id == id } ?: all.first()
    }
}

val LocalKeshTheme = staticCompositionLocalOf { KeshThemes.all.first() }

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
            onSecondary = Color.White,
            onBackground = spec.textPrimary,
            onSurface = spec.textPrimary,
            onSurfaceVariant = spec.textSecondary
        )
    } else {
        darkColorScheme(
            primary = spec.primary,
            secondary = spec.secondary,
            background = spec.background,
            surface = spec.surface,
            surfaceVariant = spec.surfaceVariant,
            onPrimary = Color(0xFF090C14),
            onSecondary = Color(0xFF090C14),
            onBackground = spec.textPrimary,
            onSurface = spec.textPrimary,
            onSurfaceVariant = spec.textSecondary
        )
    }

    CompositionLocalProvider(LocalKeshTheme provides spec) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
