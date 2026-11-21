package com.kesh.finance.ocr

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.cyrillic.CyrillicTextRecognizerOptions
import com.kesh.finance.data.Category
import com.kesh.finance.data.OcrParsedReceipt
import com.kesh.finance.data.TransactionType
import com.kesh.finance.service.KyrgyzBank
import com.kesh.finance.service.KyrgyzBankParser
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

object ReceiptOcrProcessor {

    suspend fun recognizeReceiptFromUri(context: Context, uri: Uri): OcrParsedReceipt {
        return try {
            val image = InputImage.fromFilePath(context, uri)
            val recognizer = TextRecognition.getClient(
                CyrillicTextRecognizerOptions.Builder().build()
            )
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
        } catch (e: Exception) {
            OcrParsedReceipt(
                amount = null,
                merchant = "Чек (не удалось прочитать)",
                bankName = "Чек / Скриншот",
                category = Category.OTHER_EXPENSE,
                type = TransactionType.EXPENSE,
                rawText = ""
            )
        }
    }

    fun parseReceiptText(rawText: String): OcrParsedReceipt {
        val lower = rawText.lowercase(Locale("ru"))

        val detectedBank = KyrgyzBank.entries.firstOrNull { bank ->
            bank.titleKeywords.any { kw -> lower.contains(kw) }
        }?.displayName ?: "Чек / Скриншот"

        val isIncome = listOf("пополнение", "поступило", "зачислено", "входящий перевод")
            .any { lower.contains(it) } && !lower.contains("итого")
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val amount = extractTotalAmount(rawText)
        val merchant = extractMerchantTitle(rawText, detectedBank)
        val category = KyrgyzBankParser.detectCategory(lower, type)

        return OcrParsedReceipt(
            amount = amount,
            merchant = merchant,
            bankName = detectedBank,
            category = category,
            type = type,
            rawText = rawText
        )
    }

    private fun extractTotalAmount(text: String): Double? {
        val totalRegex = Regex(
            """(?:итого|к оплате|всего|сумма|оплата|перевод)\s*:?\s*(\d[\d\s]{0,12}(?:[.,]\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        val totalMatches = totalRegex.findAll(text)
            .mapNotNull { it.groupValues[1].replace(" ", "").replace(",", ".").toDoubleOrNull() }
            .filter { it in 1.0..10_000_000.0 }
            .toList()
        if (totalMatches.isNotEmpty()) {
            return totalMatches.maxOrNull()
        }

        val currencyRegex = Regex(
            """(\d[\d\s]{0,12}(?:[.,]\d{1,2})?)\s*(?:сом|kgs|с\.)""",
            RegexOption.IGNORE_CASE
        )
        val currencyMatches = currencyRegex.findAll(text)
            .mapNotNull { it.groupValues[1].replace(" ", "").replace(",", ".").toDoubleOrNull() }
            .filter { it in 1.0..10_000_000.0 }
            .toList()
        if (currencyMatches.isNotEmpty()) {
            return currencyMatches.maxOrNull()
        }

        val fallbackNumbers = Regex("""\b(\d{2,7}(?:[.,]\d{2})?)\b""").findAll(text)
            .mapNotNull { it.groupValues[1].replace(",", ".").toDoubleOrNull() }
            .filter { it in 10.0..5_000_000.0 }
            .toList()

        return fallbackNumbers.maxOrNull()
    }

    private fun extractMerchantTitle(rawText: String, fallbackBank: String): String {
        val lines = rawText.lines()
            .map { it.trim() }
            .filter { it.length in 3..32 && !it.all { ch -> ch.isDigit() || ch == '.' || ch == ',' } }

        val knownStore = lines.firstOrNull { line ->
            val l = line.lowercase(Locale("ru"))
            listOf("globus", "глобус", "народный", "фрунзе", "frunze", "яндекс", "glovo", "азс", "кафе", "кофе")
                .any { l.contains(it) }
        }
        if (knownStore != null) return knownStore

        return lines.firstOrNull { line ->
            val l = line.lowercase(Locale("ru"))
            !l.contains("чек") && !l.contains("квитанц") && !l.contains("успешно") && !l.contains("дата")
        } ?: fallbackBank
    }
}
