package com.kesh.finance.ai

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.kesh.finance.data.Category
import com.kesh.finance.data.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class AiParsedTransaction(
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val title: String,
    val bankName: String,
    val replyComment: String
)

object AiFinanceAssistant {

    private const val MODEL_NAME = "gemini-2.5-flash"

    suspend fun parseTextCommand(
        apiKey: String,
        userPrompt: String
    ): Result<AiParsedTransaction> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Укажите бесплатный Gemini API ключ в Настройках"))
        }

        val systemInstruction = """
            Ты финансовый ИИ-помощник приложения KESH (валюта: кыргызский сом, KGS).
            Разбери фразу пользователя (например: "родители скинули мне денег 800 сомов" или "я купил колу за 87 сомов")
            и верни СТРОГО валидный JSON без markdown-обёртки со следующими полями:
            {
              "amount": число (например 800.0 или 87.0),
              "type": "INCOME" или "EXPENSE",
              "category": одно из [FOOD, GROCERIES, TRANSPORT, SHOPPING, BILLS, HEALTH, ENTERTAINMENT, TRANSFER, SALARY, CASHBACK, TRANSFER_IN, OTHER_EXPENSE, OTHER_INCOME],
              "title": короткое понятное название (например "От родителей" или "Кола"),
              "bankName": банк/кошелёк если упомянут (MBank, Bakai24, О!Деньги и т.д.) или "Наличные",
              "replyComment": короткий дружелюбный ответ на русском в 1 предложение
            }
        """.trimIndent()

        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", "$systemInstruction\n\nЗапрос пользователя: $userPrompt"))
                    })
                })
            })
        }

        executeGeminiRequest(apiKey, body)
    }

    suspend fun parseReceiptImage(
        context: Context,
        apiKey: String,
        imageUri: Uri
    ): Result<AiParsedTransaction> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalStateException("Укажите бесплатный Gemini API ключ в Настройках"))
        }

        val base64Image = encodeUriToBase64(context, imageUri)
            ?: return@withContext Result.failure(IllegalStateException("Не удалось прочитать изображение"))

        val prompt = """
            Ты финансовый ИИ-помощник приложения KESH (Кыргызстан, валюта сом / KGS / ₵).
            Посмотри на фото чека или скриншот банковского перевода. Игнорируй часы в верхней строке телефона и даты (например 2026).
            Найди итоговую сумму операции, определи это доход (INCOME) или расход (EXPENSE), название магазина/получателя и банк.
            Верни СТРОГО валидный JSON без markdown-обёртки:
            {
              "amount": число,
              "type": "INCOME" или "EXPENSE",
              "category": одно из [FOOD, GROCERIES, TRANSPORT, SHOPPING, BILLS, HEALTH, ENTERTAINMENT, TRANSFER, SALARY, CASHBACK, TRANSFER_IN, OTHER_EXPENSE, OTHER_INCOME],
              "title": название магазина или получателя,
              "bankName": банк (MBank, Bakai24, О!Деньги, Optima24 и т.д.) или "Чек",
              "replyComment": краткое пояснение на русском что найдено на фото
            }
        """.trimIndent()

        val body = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", prompt))
                        put(JSONObject().apply {
                            put("inline_data", JSONObject().apply {
                                put("mime_type", "image/jpeg")
                                put("data", base64Image)
                            })
                        })
                    })
                })
            })
        }

        executeGeminiRequest(apiKey, body)
    }

    private fun executeGeminiRequest(apiKey: String, payload: JSONObject): Result<AiParsedTransaction> {
        return try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=${apiKey.trim()}"
            val conn = (URL(endpoint).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connectTimeout = 20000
                readTimeout = 25000
                doOutput = true
            }

            conn.outputStream.use { os ->
                os.write(payload.toString().toByteArray(Charsets.UTF_8))
            }

            val code = conn.responseCode
            val stream = if (code in 200..299) conn.inputStream else conn.errorStream
            val responseText = stream?.bufferedReader()?.use { it.readText() }.orEmpty()

            if (code !in 200..299) {
                return Result.failure(IllegalStateException("Ошибка API ($code): проверьте ключ Gemini"))
            }

            val root = JSONObject(responseText)
            val rawAnswer = root.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsed = JSONObject(rawAnswer)
            val amount = parsed.optDouble("amount", 0.0)
            if (amount <= 0.0) {
                return Result.failure(IllegalStateException("ИИ не смог определить сумму"))
            }

            val typeStr = parsed.optString("type", "EXPENSE").uppercase()
            val type = if (typeStr == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE

            val catStr = parsed.optString("category", "OTHER_EXPENSE").uppercase()
            val category = Category.entries.firstOrNull { it.name == catStr }
                ?: if (type == TransactionType.INCOME) Category.OTHER_INCOME else Category.OTHER_EXPENSE

            val title = parsed.optString("title", category.title).ifBlank { category.title }
            val bankName = parsed.optString("bankName", "Наличные").ifBlank { "Наличные" }
            val comment = parsed.optString("replyComment", "Готово!")

            Result.success(
                AiParsedTransaction(
                    amount = amount,
                    type = type,
                    category = category,
                    title = title,
                    bankName = bankName,
                    replyComment = comment
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun encodeUriToBase64(context: Context, uri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val original = input.use { BitmapFactory.decodeStream(it) } ?: return null
            val maxSide = 1280
            val ratio = minOf(maxSide.toFloat() / original.width, maxSide.toFloat() / original.height, 1f)
            val scaled = if (ratio < 1f) {
                Bitmap.createScaledBitmap(
                    original,
                    (original.width * ratio).toInt(),
                    (original.height * ratio).toInt(),
                    true
                )
            } else {
                original
            }
            val baos = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos)
            Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
        } catch (_: Exception) {
            null
        }
    }
}
