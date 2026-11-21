package com.kesh.finance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
    autoBankEnabled: Boolean,
    hasNotificationPermission: Boolean,
    onUpdateUserName: (String) -> Unit,
    onUpdateBudget: (Double) -> Unit,
    onToggleAutoBank: (Boolean) -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenThemes: () -> Unit,
    onExportCsv: () -> Unit,
    onClearAll: () -> Unit
) {
    var nameInput by remember(userName) { mutableStateOf(userName) }
    var budgetInput by remember(monthlyBudget) { mutableStateOf(monthlyBudget.toInt().toString()) }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить все записи?") },
            text = { Text("Все доходы и расходы в базе KESH будут удалены без возможности восстановления.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearDialog = false
                    }
                ) {
                    Text("Удалить всё", color = Color(0xFFEF4444))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

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
                isAutoBankActive = autoBankEnabled && hasNotificationPermission,
                onOpenThemes = onOpenThemes
            )
        }

        // Профиль и бюджет
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = spec.surface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Профиль и лимит месяца",
                        color = spec.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Имя в шапке") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = budgetInput,
                        onValueChange = { budgetInput = it },
                        label = { Text("Месячный бюджет (сом)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            onUpdateUserName(nameInput)
                            budgetInput.toDoubleOrNull()?.let { onUpdateBudget(it) }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = spec.primary,
                            contentColor = if (spec.isLight) Color.White else Color(0xFF090C14)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Сохранить профиль", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Темы оформления
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = spec.surface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Оформление (11 тем)",
                            color = spec.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Активна: ${spec.name} • ${spec.subtitle}",
                            color = spec.textSecondary,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    OutlinedButton(onClick = onOpenThemes, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Rounded.Palette, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Выбрать")
                    }
                }
            }
        }

        // Авто-учёт 20 банков КР
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = spec.surface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                    "Доступ к уведомлениям активен"
                                } else {
                                    "Требуется разрешение на чтение уведомлений"
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
                    Button(
                        onClick = onOpenNotificationSettings,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = spec.surfaceVariant,
                            contentColor = spec.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Открыть доступ к уведомлениям")
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Поддерживаемые банки и кошельки (20):",
                        color = spec.textSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        KyrgyzBank.entries.forEach { bank ->
                            Surface(
                                color = Color(bank.accentHex).copy(alpha = 0.16f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = bank.displayName,
                                    color = spec.textPrimary,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Экспорт и очистка
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                color = spec.surface,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Управление данными",
                        color = spec.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = onExportCsv,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.FileDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Экспортировать историю в CSV")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showClearDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Rounded.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Очистить всю базу", color = Color(0xFFEF4444))
                    }
                }
            }
        }
    }
}
