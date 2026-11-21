package com.kesh.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FileDownload
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.kesh.finance.service.KyrgyzBank

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    spec: KeshThemeSpec,
    userName: String,
    monthlyBudget: Double,
    geminiApiKey: String,
    autoBankEnabled: Boolean,
    hasNotificationPermission: Boolean,
    onUpdateUserName: (String) -> Unit,
    onUpdateBudget: (Double) -> Unit,
    onUpdateGeminiApiKey: (String) -> Unit,
    onToggleAutoBank: (Boolean) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenThemes: () -> Unit,
    onExportCsv: () -> Unit,
    onClearAll: () -> Unit
) {
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var budgetInput by remember(monthlyBudget) {
        mutableStateOf(
            if (monthlyBudget % 1.0 == 0.0) monthlyBudget.toInt().toString() else monthlyBudget.toString()
        )
    }
    var apiKeyInput by remember(geminiApiKey) { mutableStateOf(geminiApiKey) }
    var showClearDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(spec.background)
    ) {
        KeshAnimeAtmosphere(spec = spec)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 104.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(key = "title") {
                Text(
                    text = "Настройки",
                    color = spec.textPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            item(key = "profile_card") {
                Surface(
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Профиль, ИИ и бюджет",
                            color = spec.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Имя пользователя") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                            label = { Text("Месячный лимит расходов (сом)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            label = { Text("Бесплатный Gemini API ключ") },
                            placeholder = { Text("AIza...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                onUpdateUserName(nameInput.trim().ifBlank { "Саидакбар" })
                                budgetInput.toDoubleOrNull()?.let { if (it > 0.0) onUpdateBudget(it) }
                                onUpdateGeminiApiKey(apiKeyInput.trim())
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Сохранить настройки", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item(key = "theme_card") {
                Surface(
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Оформление и темы",
                                color = spec.textPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Активна: ${spec.name} • ${spec.motto}",
                                color = spec.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        OutlinedButton(onClick = onOpenThemes, shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Rounded.Palette, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Выбрать")
                        }
                    }
                }
            }

            item(key = "autobanks_card") {
                Surface(
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Авто-учёт 20 банков КР",
                                    color = spec.textPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = if (hasNotificationPermission) {
                                        "Доступ к уведомлениям активен • Напоминания 13:05, 15:05, 19:30"
                                    } else {
                                        "Требуется доступ к уведомлениям"
                                    },
                                    color = if (hasNotificationPermission) Color(0xFF22C55E) else Color(0xFFF59E0B),
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = autoBankEnabled,
                                onCheckedChange = onToggleAutoBank
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onOpenNotificationSettings,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Открыть доступ к уведомлениям")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Поддерживаемые банки и кошельки (${KyrgyzBank.entries.size}):",
                            color = spec.textSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            KyrgyzBank.entries.forEach { bank ->
                                val bColor = Color(bank.accentColor)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bColor.copy(alpha = 0.14f))
                                        .border(1.dp, bColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                        .clickable { }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = bank.displayName,
                                        color = spec.textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item(key = "data_card") {
                Surface(
                    color = spec.surface.copy(alpha = 0.94f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Управление данными",
                            color = spec.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        OutlinedButton(
                            onClick = onExportCsv,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Экспортировать историю в CSV")
                        }
                        OutlinedButton(
                            onClick = { showClearDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Icon(Icons.Rounded.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Очистить всю базу")
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить всю базу?") },
            text = { Text("Все сохранённые доходы, расходы и авто-операции будут удалены без возможности восстановления.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearAll()
                    }
                ) {
                    Text("Удалить", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}
