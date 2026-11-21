package com.kesh.finance.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AnimeVisualType {
    NONE,
    ITACHI,
    ZENITSU,
    GOJO,
    TANJIRO,
    HANJI,
    NARUTO
}

data class KeshThemeSpec(
    val id: String,
    val name: String,
    val subtitle: String,
    val motto: String,
    val isAnime: Boolean,
    val isLight: Boolean,
    val animeType: AnimeVisualType,
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
            motto = "Классический контроль капитала",
            isAnime = false,
            isLight = false,
            animeType = AnimeVisualType.NONE,
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
            motto = "Спокойный поток финансов",
            isAnime = false,
            isLight = false,
            animeType = AnimeVisualType.NONE,
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
            motto = "Устойчивый рост накоплений",
            isAnime = false,
            isLight = false,
            animeType = AnimeVisualType.NONE,
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
            motto = "Скорость и точность учёта",
            isAnime = false,
            isLight = false,
            animeType = AnimeVisualType.NONE,
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
            motto = "Минимализм и ясность",
            isAnime = false,
            isLight = true,
            animeType = AnimeVisualType.NONE,
            background = Color(0xFFF4F6FA),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFE8EEF5),
            primary = Color(0xFF0F172A),
            secondary = Color(0xFF0284C7),
            textPrimary = Color(0xFF0F172A),
            textSecondary = Color(0xFF475569)
        ),

        // 6 уникальных аниме-тем
        KeshThemeSpec(
            id = "itachi",
            name = "Itachi",
            subtitle = "Мангекё Шаринган и вороны",
            motto = "Мангекё Шаринган • Абсолютный контроль",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.ITACHI,
            background = Color(0xFF0B0507),
            surface = Color(0xFF180A0F),
            surfaceVariant = Color(0xFF2A1119),
            primary = Color(0xFFEF4444),
            secondary = Color(0xFFF87171),
            textPrimary = Color(0xFFFFF1F2),
            textSecondary = Color(0xFFFDA4AF)
        ),
        KeshThemeSpec(
            id = "zenitsu",
            name = "Zenitsu",
            subtitle = "Дыхание Грома: Первый стиль",
            motto = "Громовой раскат • Молниеносный расчёт",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.ZENITSU,
            background = Color(0xFF110E05),
            surface = Color(0xFF201A0B),
            surfaceVariant = Color(0xFF332910),
            primary = Color(0xFFFACC15),
            secondary = Color(0xFFFB923C),
            textPrimary = Color(0xFFFEFCE8),
            textSecondary = Color(0xFFFDE047)
        ),
        KeshThemeSpec(
            id = "gojo",
            name = "Gojo",
            subtitle = "Шесть Глаз и Бесконечность",
            motto = "Муген • Безграничный баланс",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.GOJO,
            background = Color(0xFF060A17),
            surface = Color(0xFF0E162E),
            surfaceVariant = Color(0xFF18254A),
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFFC084FC),
            textPrimary = Color(0xFFEFF6FF),
            textSecondary = Color(0xFF93C5FD)
        ),
        KeshThemeSpec(
            id = "tanjiro",
            name = "Tanjiro",
            subtitle = "Танец Бога Огня и Воды",
            motto = "Хиноками Кагура • Сила стихий",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.TANJIRO,
            background = Color(0xFF071311),
            surface = Color(0xFF0F221E),
            surfaceVariant = Color(0xFF17352F),
            primary = Color(0xFF14B8A6),
            secondary = Color(0xFFF97316),
            textPrimary = Color(0xFFF0FDFA),
            textSecondary = Color(0xFF5EEAD4)
        ),
        KeshThemeSpec(
            id = "hanji",
            name = "Hanji",
            subtitle = "Крылья Свободы и исследования",
            motto = "Разведкорпус • Точный анализ данных",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.HANJI,
            background = Color(0xFF0C100B),
            surface = Color(0xFF171E16),
            surfaceVariant = Color(0xFF242F22),
            primary = Color(0xFF84CC16),
            secondary = Color(0xFF38BDF8),
            textPrimary = Color(0xFFF7FEE7),
            textSecondary = Color(0xFFBEF264)
        ),
        KeshThemeSpec(
            id = "naruto",
            name = "Naruto",
            subtitle = "Режим Мудреца Шести Путей",
            motto = "Печать Восьми Триграмм • Воля Огня",
            isAnime = true,
            isLight = false,
            animeType = AnimeVisualType.NARUTO,
            background = Color(0xFF130B05),
            surface = Color(0xFF23150A),
            surfaceVariant = Color(0xFF36200F),
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
