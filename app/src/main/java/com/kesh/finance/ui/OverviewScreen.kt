package com.kesh.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.TransactionEntity
import com.kesh.finance.data.TransactionType
import java.util.Calendar

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
                                Text(text = "Общий баланс", color = spec.textSecondary, fontSize = 13.sp)
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
                    QuickActionTile("Добавить", Icons.Rounded.Add, spec, true, onOpenAdd, Modifier.weight(1f))
                    QuickActionTile("Чек OCR", Icons.Rounded.DocumentScanner, spec, false, onScanReceipt, Modifier.weight(1f))
                    QuickActionTile("Голос", Icons.Rounded.Mic, spec, false, onVoiceInput, Modifier.weight(1f))
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
