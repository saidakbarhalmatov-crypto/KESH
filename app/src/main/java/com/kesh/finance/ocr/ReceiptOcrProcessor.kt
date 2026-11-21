package com.kesh.finance.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.kesh.finance.data.Category
import com.kesh.finance.data.OcrParsedReceipt
import com.kesh.finance.data.TransactionType
import com.kesh.finance.service.KyrgyzBank
import com.kesh.finance.service.KyrgyzBankParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

object ReceiptOcrProcessor {

    suspend fun recognizeReceiptFromUri(context: Context, uri: Uri): OcrParsedReceipt =
        withContext(Dispatchers.IO) {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            try {
                val image = InputImage.fromFilePath(context, uri)
                val visionText = suspendCancellableCoroutine<String> { cont ->
                    recognizer.process(image)
                        .addOnSuccessListener { result ->
                            if (cont.isActive) cont.resume(result.text)
                        }
                        .addOnFailureListener {
                            if (cont.isActive) cont.resume("")
                        }
                }
                parseReceiptText(visionText)
            } catch (_: Exception) {
                OcrParsedReceipt(
                    amount = null,
                    merchant = "Чек (введите вручную)",
                    bankName = "Чек / Скриншот",
                    category = Category.OTHER_EXPENSE,
                    type = TransactionType.EXPENSE,
                    rawText = ""
                )
            } finally {
                recognizer.close()
            }
        }

    fun parseReceiptText(rawText: String): OcrParsedReceipt {
        val cleanedLines = rawText.lines()
            .map { it.trim() }
            .filter { line ->
                if (line.isBlank()) return@filter false
                // Убираем верхнюю строку состояния телефона (время + уровень сигнала/батареи)
                val looksLikeStatusBar = Regex("""^\d{1,2}:\d{2}\b.*""").matches(line) && line.length <= 18
                !looksLikeStatusBar
            }

        val normalized = cleanedLines.joinToString("\n")
        val lower = normalized.lowercase(Locale("ru"))

        val detectedBank = detectBankFromReceipt(lower)
        val isIncome = listOf("пополнение", "поступило", "зачислено", "входящий перевод")
            .any { lower.contains(it) } && !lower.contains("итого") && !lower.contains("оплата")
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val amount = extractTotalAmount(normalized)
        val merchant = extractMerchantTitle(cleanedLines, detectedBank)
        val category = KyrgyzBankParser.detectCategory(lower, type)

        return OcrParsedReceipt(
            amount = amount,
            merchant = merchant,
            bankName = detectedBank,
            category = category,
            type = type,
            rawText = normalized.ifBlank { rawText }
        )
    }

    private fun detectBankFromReceipt(lower: String): String {
        KyrgyzBank.entries.firstOrNull { bank ->
            bank.titleKeywords.any { kw -> lower.contains(kw) }
        }?.let { return it.displayName }

        return when {
            lower.contains("bakai") || lower.contains("бакай") || lower.contains("qr.bakai.kg") -> "Bakai24"
            lower.contains("mbank") || lower.contains("мбанк") -> "MBank"
            lower.contains("о!деньги") || lower.contains("o!dengi") || lower.contains("мой кошелек") -> "О!Деньги"
            lower.contains("optima") || lower.contains("оптима") -> "Optima24"
            lower.contains("demir") || lower.contains("демир") -> "DemirBank"
            lower.contains("megapay") -> "MegaPay"
            else -> "Чек / Скриншот"
        }
    }

    private fun extractTotalAmount(text: String): Double? {
        val cleaned = text
            // Удаляем даты вида 22.11.2026, 22/11/26 и время 02:58:10
            .replace(Regex("""\b\d{1,2}[./-]\d{1,2}[./-]\d{2,4}\b"""), " ")
            .replace(Regex("""\b\d{1,2}:\d{2}(?::\d{2})?\b"""), " ")
            // Удаляем телефоны и длинные номера счетов / квитанций / ID
            .replace(Regex("""(?:\+?996|0)\s*\d{3}\s*\d{2,3}\s*\d{2,3}"""), " ")
            .replace(Regex("""\b\d{8,}\b"""), " ")
            .replace(Regex("""[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"""), " ")

        // 1. Ищем сумму рядом со знаком сома / KGS / сом
        val currencyRegex = Regex(
            """(\d[\d\s]{0,9}(?:[.,]\d{1,2})?)\s*(?:₵|сом|сомов|kgs|с\.)""",
            RegexOption.IGNORE_CASE
        )
        val currencyMatches = currencyRegex.findAll(cleaned)
            .mapNotNull { it.groupValues[1].replace(" ", "").replace(",", ".").toDoubleOrNull() }
            .filter { it in 0.5..10_000_000.0 }
            .toList()
        if (currencyMatches.isNotEmpty()) {
            return currencyMatches.first()
        }

        // 2. Ищем после слов Итого / Сумма / Оплата
        val labelRegex = Regex(
            """(?:итого|к оплате|всего|сумма|оплата|перевод)\s*:?\s*(\d[\d\s]{0,9}(?:[.,]\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        val labelMatches = labelRegex.findAll(cleaned)
            .mapNotNull { it.groupValues[1].replace(" ", "").replace(",", ".").toDoubleOrNull() }
            .filter { it in 0.5..10_000_000.0 }
            .toList()
        if (labelMatches.isNotEmpty()) {
            return labelMatches.first()
        }

        // 3. Запасной вариант — первое десятичное число с копейками (например 10,00)
        val decimalMatch = Regex("""\b(\d{1,7}[.,]\d{1,2})\b""").find(cleaned)
        if (decimalMatch != null) {
            return decimalMatch.groupValues[1].replace(",", ".").toDoubleOrNull()
        }

        return null
    }

    private fun extractMerchantTitle(lines: List<String>, fallbackBank: String): String {
        val kudaIndex = lines.indexOfFirst {
            val l = it.lowercase(Locale("ru"))
            l == "получатель" || l.startsWith("получатель") || l == "куда" || l.startsWith("куда")
        }
        if (kudaIndex != -1 && kudaIndex + 1 < lines.size) {
            val recipient = lines[kudaIndex + 1].trim()
            if (recipient.length >= 2 && !Regex("""^\d{1,2}:\d{2}$""").matches(recipient)) {
                return recipient
            }
        }

        val qrLine = lines.firstOrNull { it.contains("QR", ignoreCase = true) || it.contains("qr.", ignoreCase = true) }
        if (qrLine != null && qrLine.length in 3..50) {
            return qrLine
        }

        return fallbackBank
    }
}
