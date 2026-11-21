package com.kesh.finance.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.Category
import com.kesh.finance.data.TransactionEntity
import com.kesh.finance.data.TransactionType

@Composable
fun AnalyticsScreen(
    spec: KeshThemeSpec,
    userName: String,
    isAutoBankActive: Boolean,
    transactions: List<TransactionEntity>,
    onOpenThemes: () -> Unit
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    val filtered = transactions.filter { it.transactionType == selectedType }
    val totalSum = filtered.sumOf { it.amount }

    val categoryStats: List<Triple<Category, Double, Int>> = filtered
        .groupBy { it.categoryEnum }
        .map { (cat, list) -> Triple(cat, list.sumOf { it.amount }, list.size) }
        .sortedByDescending { it.second }

    val bankStats: List<Pair<String, Double>> = transactions
        .groupBy { it.bankName }
        .map { (bank, list) -> bank to list.sumOf { it.amount } }
        .sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(spec.background),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        item {
            KeshTopHeader(
                userName = userName,
                spec = spec,
                isAutoBankActive = isAutoBankActive,
                onOpenThemes = onOpenThemes
            )
        }

        item {
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

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = spec.surface,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(190.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(180.dp)) {
                            val strokeWidth = 26.dp.toPx()
                            val diameter = size.minDimension - strokeWidth
                            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
                            val arcSize = Size(diameter, diameter)

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

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        items(categoryStats) { (cat, sum, count) ->
            val ratio = if (totalSum > 0.0) (sum / totalSum).toFloat() else 0f
            val percent = (ratio * 100).toInt()
            val catColor = Color(cat.colorHex)

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                color = spec.surface,
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
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Оборот по банкам и кошелькам",
                    color = spec.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            items(bankStats) { (bankName, sum) ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    color = spec.surface,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = bankName,
                            color = spec.textPrimary,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = formatSom(sum),
                            color = spec.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}
