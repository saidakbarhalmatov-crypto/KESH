package com.kesh.finance.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.Category
import com.kesh.finance.data.TransactionEntity
import com.kesh.finance.data.TransactionType
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalyticsScreen(
    spec: KeshThemeSpec,
    userName: String,
    isAutoBankActive: Boolean,
    transactions: List<TransactionEntity>,
    onOpenThemes: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val filtered = remember(transactions, selectedType) {
        transactions.filter { it.transactionType == selectedType }
    }
    val totalSum = remember(filtered) { filtered.sumOf { it.amount } }

    val categoryStats: List<Triple<Category, Double, Int>> = remember(filtered) {
        filtered
            .groupBy { it.categoryEnum }
            .map { (cat, list) -> Triple(cat, list.sumOf { it.amount }, list.size) }
            .sortedByDescending { it.second }
    }

    val bankStats: List<Pair<String, Double>> = remember(transactions) {
        transactions
            .groupBy { it.bankName }
            .map { (bank, list) -> bank to list.sumOf { it.amount } }
            .sortedByDescending { it.second }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(spec.background)
    ) {
        KeshAnimeAtmosphere(spec = spec)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 104.dp)
        ) {
            item(key = "header") {
                KeshTopHeader(
                    userName = userName,
                    spec = spec,
                    isAutoBankActive = isAutoBankActive,
                    onOpenThemes = onOpenThemes
                )
            }

            item(key = "type_switch") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TransactionType.entries.forEach { type ->
                        val isSelected = selectedType == type
                        val title = if (type == TransactionType.EXPENSE) "Расходы" else "Доходы"
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { selectedType = type },
                            color = if (isSelected) spec.primary else spec.surface
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = if (isSelected) {
                                        if (spec.isLight) Color.White else Color(0xFF090C14)
                                    } else {
                                        spec.textPrimary
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item(key = "donut_card") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(
                            width = if (spec.isAnime) 1.dp else 0.dp,
                            color = if (spec.isAnime) spec.primary.copy(alpha = 0.35f) else Color.Transparent,
                            shape = RoundedCornerShape(22.dp)
                        ),
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (spec.isAnime) {
                            Text(
                                text = spec.motto,
                                color = spec.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Box(
                            modifier = Modifier.size(220.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(210.dp)) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val strokeWidth = 24.dp.toPx()
                                val diameter = size.minDimension - strokeWidth
                                val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                                val arcSize = Size(diameter, diameter)
                                val innerRadius = (diameter / 2f) - (strokeWidth / 2f) - 6.dp.toPx()

                                drawAnalyticsAnimeCenter(spec = spec, center = center, innerRadius = innerRadius)

                                if (totalSum <= 0.0 || categoryStats.isEmpty()) {
                                    drawArc(
                                        color = spec.surfaceVariant,
                                        startAngle = 0f,
                                        sweepAngle = 360f,
                                        useCenter = false,
                                        topLeft = topLeft,
                                        size = arcSize,
                                        style = Stroke(width = strokeWidth)
                                    )
                                } else {
                                    var startAngle = -90f
                                    categoryStats.forEach { (cat, sum, _) ->
                                        val sweep = ((sum / totalSum) * 360.0).toFloat()
                                        drawArc(
                                            color = Color(cat.colorHex),
                                            startAngle = startAngle,
                                            sweepAngle = (sweep - 2f).coerceAtLeast(1f),
                                            useCenter = false,
                                            topLeft = topLeft,
                                            size = arcSize,
                                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                                        )
                                        startAngle += sweep
                                    }
                                }
                            }

                            Surface(
                                color = spec.background.copy(alpha = if (spec.isAnime) 0.78f else 0f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = if (selectedType == TransactionType.EXPENSE) "Всего расход" else "Всего доход",
                                        color = spec.textSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = formatSom(totalSum),
                                        color = spec.textPrimary,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(categoryStats, key = { it.first.name }) { (cat, sum, count) ->
                val ratio = if (totalSum > 0.0) (sum / totalSum).toFloat() else 0f
                val percent = (ratio * 100).toInt()
                val catColor = Color(cat.colorHex)

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 5.dp),
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(catColor)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = cat.title,
                                        color = spec.textPrimary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Операций: $count • $percent%",
                                        color = spec.textSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                            Text(
                                text = formatSom(sum),
                                color = spec.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { ratio },
                            color = catColor,
                            trackColor = spec.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            if (bankStats.isNotEmpty()) {
                item(key = "banks_header") {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Оборот по банкам и кошелькам",
                        color = spec.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
                items(bankStats, key = { it.first }) { (bankName, sum) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        color = spec.surface.copy(alpha = 0.94f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = bankName, color = spec.textPrimary, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                            Text(text = formatSom(sum), color = spec.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawAnalyticsAnimeCenter(
    spec: KeshThemeSpec,
    center: Offset,
    innerRadius: Float
) {
    when (spec.animeType) {
        AnimeVisualType.ITACHI -> {
            drawSharinganArt(center = center, radius = innerRadius, isMangekyo = true)
        }
        AnimeVisualType.GOJO -> {
            drawSixEyesArt(center = center, radius = innerRadius)
            drawCircle(color = Color(0xFF38BDF8), radius = 7.dp.toPx(), center = Offset(center.x - innerRadius * 0.85f, center.y))
            drawCircle(color = Color(0xFFEF4444), radius = 7.dp.toPx(), center = Offset(center.x + innerRadius * 0.85f, center.y))
        }
        AnimeVisualType.ZENITSU -> {
            drawCircle(color = Color(0xFF1A1508), radius = innerRadius, center = center)
            drawLightningBolt(
                start = Offset(center.x + innerRadius * 0.35f, center.y - innerRadius * 0.75f),
                end = Offset(center.x - innerRadius * 0.35f, center.y + innerRadius * 0.75f),
                color = Color(0xFFFACC15).copy(alpha = 0.65f),
                strokeWidth = 5.dp.toPx()
            )
        }
        AnimeVisualType.TANJIRO -> {
            drawCircle(color = Color(0xFF0C1E1A), radius = innerRadius, center = center)
            val arcTopLeft = Offset(center.x - innerRadius * 0.82f, center.y - innerRadius * 0.82f)
            val arcDim = Size(innerRadius * 1.64f, innerRadius * 1.64f)
            drawArc(Color(0xFF14B8A6).copy(alpha = 0.6f), 90f, 180f, false, arcTopLeft, arcDim, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
            drawArc(Color(0xFFF97316).copy(alpha = 0.6f), -90f, 180f, false, arcTopLeft, arcDim, style = Stroke(6.dp.toPx(), cap = StrokeCap.Round))
        }
        AnimeVisualType.HANJI -> {
            drawCircle(color = Color(0xFF131A12), radius = innerRadius, center = center)
            drawWingsOfFreedom(center, innerRadius * 0.85f, Color.White.copy(alpha = 0.32f), Color(0xFF38BDF8).copy(alpha = 0.40f))
        }
        AnimeVisualType.NARUTO -> {
            drawCircle(color = Color(0xFF1F1208), radius = innerRadius, center = center)
            drawUzumakiSpiral(center, innerRadius * 0.85f, Color(0xFFF97316).copy(alpha = 0.55f), 4.dp.toPx())
            for (i in 0 until 8) {
                val rad = (i * 45.0) * PI / 180.0
                drawLine(
                    color = Color(0xFFFACC15).copy(alpha = 0.5f),
                    start = Offset(center.x + (innerRadius * 0.65f * cos(rad)).toFloat(), center.y + (innerRadius * 0.65f * sin(rad)).toFloat()),
                    end = Offset(center.x + (innerRadius * 0.94f * cos(rad)).toFloat(), center.y + (innerRadius * 0.94f * sin(rad)).toFloat()),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }
        AnimeVisualType.NONE -> Unit
    }
}
