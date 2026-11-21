package com.kesh.finance.ui

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.TransactionEntity
import com.kesh.finance.data.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OverviewScreen(
    spec: KeshThemeSpec,
    userName: String,
    monthlyBudget: Double,
    isAutoBankActive: Boolean,
    transactions: List<TransactionEntity>,
    onOpenThemes: () -> Unit,
    onOpenAdd: () -> Unit,
    onScanReceipt: () -> Unit,
    onVoiceInput: () -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    var searchQuery by remember { mutableStateOf("") }

    val startOfMonth = remember {
        Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis
    }

    val totals = remember(transactions, startOfMonth) {
        var inc = 0.0
        var exp = 0.0
        var mExp = 0.0
        for (tx in transactions) {
            if (tx.transactionType == TransactionType.INCOME) {
                inc += tx.amount
            } else {
                exp += tx.amount
                if (tx.timestamp >= startOfMonth) {
                    mExp += tx.amount
                }
            }
        }
        Triple(inc, exp, mExp)
    }

    val totalIncome = totals.first
    val totalExpense = totals.second
    val monthExpense = totals.third
    val balance = totalIncome - totalExpense
    val budgetRatio = (monthExpense / monthlyBudget.coerceAtLeast(1.0)).toFloat().coerceIn(0f, 1f)

    val filteredList = remember(transactions, selectedFilter, searchQuery) {
        transactions.filter { item ->
            val matchesTab = when (selectedFilter) {
                "EXPENSE" -> item.transactionType == TransactionType.EXPENSE
                "INCOME" -> item.transactionType == TransactionType.INCOME
                "AUTO" -> item.source == "AUTO_BANK"
                else -> true
            }
            val matchesQuery = searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.bankName.contains(searchQuery, ignoreCase = true) ||
                item.categoryEnum.title.contains(searchQuery, ignoreCase = true)
            matchesTab && matchesQuery
        }
    }

    val filters = remember(transactions.size) {
        listOf(
            "ALL" to "Все (${transactions.size})",
            "EXPENSE" to "Расходы",
            "INCOME" to "Доходы",
            "AUTO" to "Авто-банк"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(spec.background)
    ) {
        // Аниме-атмосфера на заднем плане (вороны, молнии, Муген, волны, крылья, спираль)
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

            item(key = "balance_card") {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(
                            width = if (spec.isAnime) 1.dp else 0.dp,
                            color = if (spec.isAnime) spec.primary.copy(alpha = 0.4f) else Color.Transparent,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = spec.surface.copy(alpha = 0.94f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        spec.primary.copy(alpha = 0.26f),
                                        spec.surface.copy(alpha = 0.92f),
                                        spec.secondary.copy(alpha = 0.16f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Общий баланс",
                                    color = spec.textSecondary,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = spec.motto,
                                    color = spec.primary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatSom(balance),
                                color = spec.textPrimary,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                StatPill(
                                    label = "Доходы",
                                    value = "+${formatSom(totalIncome)}",
                                    color = Color(0xFF10B981),
                                    icon = Icons.Rounded.ArrowDownward,
                                    modifier = Modifier.weight(1f)
                                )
                                StatPill(
                                    label = "Расходы",
                                    value = "-${formatSom(totalExpense)}",
                                    color = Color(0xFFEF4444),
                                    icon = Icons.Rounded.ArrowUpward,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Лимит месяца: ${formatSom(monthExpense)}",
                                    color = spec.textSecondary,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "из ${formatSom(monthlyBudget)}",
                                    color = spec.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { budgetRatio },
                                color = if (budgetRatio > 0.85f) Color(0xFFEF4444) else spec.primary,
                                trackColor = spec.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }

            item(key = "quick_actions") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "Добавить",
                        icon = Icons.Rounded.Add,
                        spec = spec,
                        isAccent = true,
                        onClick = onOpenAdd,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        title = "Чек OCR",
                        icon = Icons.Rounded.DocumentScanner,
                        spec = spec,
                        isAccent = false,
                        onClick = onScanReceipt,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        title = "Голос",
                        icon = Icons.Rounded.Mic,
                        spec = spec,
                        isAccent = false,
                        onClick = onVoiceInput,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item(key = "search_filters") {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Поиск по операциям и банкам...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filters, key = { it.first }) { (key, label) ->
                            FilterChip(
                                selected = selectedFilter == key,
                                onClick = { selectedFilter = key },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = spec.primary.copy(alpha = 0.22f),
                                    selectedLabelColor = spec.primary
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (filteredList.isEmpty()) {
                item(key = "empty_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Операций пока нет. Нажмите «Добавить», отсканируйте чек или скажите голосом.",
                            color = spec.textSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredList, key = { it.id }) { tx ->
                    TransactionRowCard(
                        tx = tx,
                        spec = spec,
                        onDelete = { onDeleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(text = label, color = color.copy(alpha = 0.85f), fontSize = 11.sp)
                Text(
                    text = value,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    icon: ImageVector,
    spec: KeshThemeSpec,
    isAccent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = if (isAccent) spec.primary else spec.surface.copy(alpha = 0.94f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isAccent) {
                    if (spec.isLight) Color.White else Color(0xFF090C14)
                } else {
                    spec.primary
                },
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = if (isAccent) {
                    if (spec.isLight) Color.White else Color(0xFF090C14)
                } else {
                    spec.textPrimary
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TransactionRowCard(
    tx: TransactionEntity,
    spec: KeshThemeSpec,
    onDelete: () -> Unit
) {
    val dateStr = remember(tx.timestamp) {
        SimpleDateFormat("dd MMM, HH:mm", Locale("ru")).format(Date(tx.timestamp))
    }
    val isIncome = tx.transactionType == TransactionType.INCOME
    val amountColor = if (isIncome) Color(0xFF10B981) else spec.textPrimary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp),
        color = spec.surface.copy(alpha = 0.94f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CategoryBadge(category = tx.categoryEnum)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = tx.title,
                        color = spec.textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (tx.source == "AUTO_BANK") {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = Color(0xFF22C55E).copy(alpha = 0.18f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "AUTO",
                                color = Color(0xFF22C55E),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${tx.bankName} • $dateStr",
                    color = spec.textSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isIncome) "+" else "-") + formatSom(tx.amount),
                  
