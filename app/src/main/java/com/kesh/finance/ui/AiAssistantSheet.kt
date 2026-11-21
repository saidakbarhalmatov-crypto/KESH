package com.kesh.finance.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kesh.finance.ai.AiFinanceAssistant
import com.kesh.finance.ai.AiParsedTransaction
import com.kesh.finance.data.KeshRepository
import com.kesh.finance.data.TransactionType
import com.kesh.finance.service.KyrgyzBankParser
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantSheet(
    spec: KeshThemeSpec,
    apiKey: String,
    repo: KeshRepository,
    onDismiss: () -> Unit,
    onTransactionSaved: (AiParsedTransaction) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var inputPrompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var previewResult by remember { mutableStateOf<AiParsedTransaction?>(null) }

    fun analyzeText(query: String) {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return
        isLoading = true
        statusMessage = null
        previewResult = null

        scope.launch {
            if (apiKey.isNotBlank()) {
                val aiRes = AiFinanceAssistant.parseTextCommand(apiKey, trimmed)
                aiRes.onSuccess { parsed ->
                    isLoading = false
                    previewResult = parsed
                    statusMessage = parsed.replyComment
                }.onFailure { err ->
                    val fallback = repo.parseVoiceInput(trimmed)
                    isLoading = false
                    if (fallback != null) {
                        val (amt, type, title) = fallback
                        val cat = KyrgyzBankParser.detectCategory(title.lowercase(Locale("ru")), type)
                        previewResult = AiParsedTransaction(
                            amount = amt,
                            type = type,
                            category = cat,
                            title = title,
                            bankName = "Локальный разбор",
                            replyComment = "Разобрано локально (${err.message})"
                        )
                        statusMessage = "Разобрано локально: проверьте и нажмите «Записать»"
                    } else {
                        statusMessage = err.message ?: "Ошибка ИИ"
                    }
                }
            } else {
                val fallback = repo.parseVoiceInput(trimmed)
                isLoading = false
                if (fallback != null) {
                    val (amt, type, title) = fallback
                    val cat = KyrgyzBankParser.detectCategory(title.lowercase(Locale("ru")), type)
                    previewResult = AiParsedTransaction(
                        amount = amt,
                        type = type,
                        category = cat,
                        title = title,
                        bankName = "Без API-ключа",
                        replyComment = "Для ИИ-анализа фото и сложных фраз добавьте бесплатный Gemini ключ в Настройках"
                    )
                    statusMessage = "Распознано локально. Для полного ИИ добавьте бесплатный ключ в Настройках."
                } else {
                    statusMessage = "Добавьте бесплатный Gemini API ключ в Настройках, чтобы ИИ понимал любые фразы и фото."
                }
            }
        }
    }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull().orEmpty()
            if (spoken.isNotBlank()) {
                inputPrompt = spoken
                analyzeText(spoken)
            }
        }
    }

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isLoading = true
            statusMessage = "ИИ изучает фото чека..."
            previewResult = null
            scope.launch {
                val res = AiFinanceAssistant.parseReceiptImage(context, apiKey, uri)
                isLoading = false
                res.onSuccess { parsed ->
                    previewResult = parsed
                    statusMessage = parsed.replyComment
                }.onFailure { err ->
                    statusMessage = err.message ?: "Не удалось распознать фото"
                }
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = spec.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThemeEmblemBadge(spec = spec, modifier = Modifier.size(34.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ИИ-Помощник KESH",
                        color = spec.textPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Голос, текст или фото чека • Авто-определение дохода и расхода",
                        color = spec.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = {
                    Text("Например: родители скинули 800 сом или купил колу за 87")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                try {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(
                                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                        )
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите доход или расход...")
                                    }
                                    voiceLauncher.launch(intent)
                                } catch (_: Exception) {
                                    statusMessage = "Голосовой ввод недоступен на устройстве"
                                }
                            }
                        ) {
                            Icon(Icons.Rounded.Mic, contentDescription = "Голос", tint = spec.primary)
                        }
                        IconButton(onClick = { analyzeText(inputPrompt) }) {
                            Icon(Icons.Rounded.Send, contentDescription = "Отправить", tint = spec.primary)
                        }
                    }
                }
            )

            OutlinedButton(
                onClick = { imageLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.ImageSearch, contentDescription = null, tint = spec.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сканировать фото чека или скриншот через ИИ", color = spec.textPrimary)
            }

            if (isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = spec.primary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("ИИ анализирует...", color = spec.textSecondary, fontSize = 13.sp)
                }
            }

            statusMessage?.let { msg ->
                Text(
                    text = msg,
                    color = spec.textSecondary,
                    fontSize = 12.sp
                )
            }

            previewResult?.let { item ->
                val isIncome = item.type == TransactionType.INCOME
                val accent = if (isIncome) Color(0xFF10B981) else Color(0xFFEF4444)
                val sign = if (isIncome) "+" else "-"

                Surface(
                    color = spec.surfaceVariant,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isIncome) "ПРИБЫЛЬ / ДОХОД" else "РАСХОД (ВЫЧТЕТСЯ ИЗ БАЛАНСА)",
                                color = accent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$sign${item.amount} сом",
                                color = accent,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Text(
                            text = "${item.title} • ${item.category.title} (${item.bankName})",
                            color = spec.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { onTransactionSaved(item) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = spec.primary)
                        ) {
                            Icon(Icons.Rounded.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Подтвердить и записать в KESH", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
