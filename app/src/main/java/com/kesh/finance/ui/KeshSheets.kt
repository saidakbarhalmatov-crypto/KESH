package com.kesh.finance.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.data.Category
import com.kesh.finance.data.OcrParsedReceipt
import com.kesh.finance.data.TransactionType

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddOperationSheet(
    spec: KeshThemeSpec,
    initialAmount: String = "",
    initialType: TransactionType = TransactionType.EXPENSE,
    initialTitle: String = "",
    onDismiss: () -> Unit,
    onSave: (amount: Double, type: TransactionType, category: Category, title: String, bank: String) -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf(initialAmount) }
    var titleText by remember { mutableStateOf(initialTitle) }
    var selectedBank by remember { mutableStateOf("Вручную") }
    var selectedCategory by remember {
        mutableStateOf(
            if (initialType == TransactionType.INCOME) Category.SALARY else Category.FOOD
        )
    }

    val quickBanks = listOf("Вручную", "MBank", "О!Деньги", "Optima24", "Bakai24", "DemirBank", "MegaPay")
    val availableCategories = Category.forType(type)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = spec.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Новая операция",
                color = spec.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TransactionType.entries.forEach { itemType ->
                    val isSelected = type == itemType
                    val label = if (itemType == TransactionType.EXPENSE) "Расход" else "Доход"
                    val activeColor = if (itemType == TransactionType.EXPENSE) Color(0xFFEF4444) else Color(0xFF10B981)
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                type = itemType
                                selectedCategory = Category.forType(itemType).first()
                            },
                        color = if (isSelected) activeColor.copy(alpha = 0.22f) else spec.surfaceVariant
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) activeColor else spec.textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Сумма (сом)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Название или комментарий") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Банк / Кошелёк",
                color = spec.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickBanks.forEach { bank ->
                    FilterChip(
                        selected = selectedBank == bank,
                        onClick = { selectedBank = bank },
                        label = { Text(bank, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = spec.primary.copy(alpha = 0.25f),
                            selectedLabelColor = spec.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "Категория",
                color = spec.textSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableCategories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat.title, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(cat.colorHex).copy(alpha = 0.25f),
                            selectedLabelColor = Color(cat.colorHex)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val parsed = amountText.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
                    if (parsed > 0.0) {
                        onSave(parsed, type, selectedCategory, titleText, selectedBank)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = spec.primary,
                    contentColor = if (spec.isLight) Color.White else Color(0xFF090C14)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Сохранить операцию", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OcrReviewSheet(
    spec: KeshThemeSpec,
    parsed: OcrParsedReceipt,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, type: TransactionType, category: Category, title: String, bank: String) -> Unit
) {
    var amountText by remember { mutableStateOf(parsed.amount?.toString() ?: "") }
    var titleText by remember { mutableStateOf(parsed.merchant) }
    var bankText by remember { mutableStateOf(parsed.bankName) }
    var type by remember { mutableStateOf(parsed.type) }
    var category by remember { mutableStateOf(parsed.category) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = spec.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.DocumentScanner,
                    contentDescription = null,
                    tint = spec.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Распознавание чека (OCR)",
                    color = spec.textPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Распознанная сумма (сом)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Магазин / Получатель") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = bankText,
                onValueChange = { bankText = it },
                label = { Text("Банк / Источник") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Category.forType(type).forEach { cat ->
                    FilterChip(
                        selected = category == cat,
                        onClick = { category = cat },
                        label = { Text(cat.title, fontSize = 12.sp) }
                    )
                }
            }

            if (parsed.rawText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = spec.surfaceVariant,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Текст с чека: ${parsed.rawText.take(180).replace("\n", " ")}",
                        color = spec.textSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = {
                    val amount = amountText.replace(',', '.').trim().toDoubleOrNull() ?: 0.0
                    if (amount > 0.0) {
                        onConfirm(amount, type, category, titleText, bankText)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = spec.primary,
                    contentColor = if (spec.isLight) Color.White else Color(0xFF090C14)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Подтвердить и записать", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerSheet(
    currentThemeId: String,
    spec: KeshThemeSpec,
    onSelectTheme: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val baseThemes = KeshThemes.all.filter { !it.isAnime }
    val animeThemes = KeshThemes.all.filter { it.isAnime }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = spec.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = "11 тем оформления KESH",
                color = spec.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Аниме-коллекция с живыми символами (6)",
                color = spec.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            animeThemes.forEach { item ->
                ThemeRowCard(
                    item = item,
                    isSelected = item.id == currentThemeId,
                    onClick = { onSelectTheme(item.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Базовые темы (5)",
                color = spec.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            baseThemes.forEach { item ->
                ThemeRowCard(
                    item = item,
                    isSelected = item.id == currentThemeId,
                    onClick = { onSelectTheme(item.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeRowCard(
    item: KeshThemeSpec,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) item.primary else item.surfaceVariant,
                shape = RoundedCornerShape(14.dp)
            ),
        color = item.background
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Показываем живой герб темы (Шаринган, Крылья, Спираль, Молнию и т.д.)
                KeshLuxuryEmblem(spec = item, size = 38.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.name,
                        color = item.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = item.subtitle,
                        color = item.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "Выбрано",
                    tint = item.primary
                )
            }
        }
    }
}
