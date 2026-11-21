package com.kesh.finance.data

import android.content.Context
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class KeshRepository(context: Context) {

    private val dao = KeshDatabase.getInstance(context).transactionDao()

    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    suspend fun addTransaction(
        amount: Double,
        type: TransactionType,
        category: Category,
        title: String,
        bankName: String = "Наличные",
        source: String = "MANUAL",
        note: String = "",
        timestamp: Long = System.currentTimeMillis()
    ) {
        if (amount <= 0.0) return
        val entity = TransactionEntity(
            amount = amount,
            type = type.name,
            category = category.name,
            title = title.ifBlank { category.title },
            bankName = bankName,
            source = source,
            note = note,
            timestamp = timestamp
        )
        dao.insert(entity)
    }

    suspend fun deleteTransaction(entity: TransactionEntity) {
        dao.delete(entity)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }

    fun parseVoiceInput(spokenText: String): Triple<Double, TransactionType, String>? {
        val cleaned = spokenText.trim()
        if (cleaned.isBlank()) return null

        val normalizedNumbers = cleaned.replace(Regex("""(\d)\s+(\d)"""), "$1$2")
        val amountMatch = Regex("""(\d+(?:[.,]\d{1,2})?)""").find(normalizedNumbers) ?: return null
        val amount = amountMatch.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
        if (amount <= 0.0) return null

        val lower = cleaned.lowercase(Locale("ru"))
        val incomeTriggers = listOf(
            "прибыль", "доход", "плюс", "зарплата", "аванс",
            "получил", "получила", "заработал", "заработала",
            "вернули", "кэшбэк", "кешбэк", "пополнение", "зачисление"
        )
        val isIncome = incomeTriggers.any { lower.contains(it) }
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val title = cleaned
            .replace(Regex("""\d[\d\s]*(?:[.,]\d{1,2})?"""), " ")
            .replace(
                Regex(
                    """(?i)\b(прибыль|доход|расход|трата|плюс|минус|потратил|потратила|купил|купила|оплатил|оплатила|получил|получила|заработал|заработала|сомов|сом|kgs)\b"""
                ),
                " "
            )
            .replace(Regex("""[,.:;!?-]+"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString() }
            .ifBlank { if (isIncome) "Доход (голос)" else "Расход (голос)" }

        return Triple(amount, type, title)
    }

    fun exportCsv(context: Context, items: List<TransactionEntity>): File {
        val exportDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val file = File(exportDir, "KESH_report_${System.currentTimeMillis()}.csv")
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val sb = StringBuilder()
        sb.append('\uFEFF')
        sb.appendLine("Дата;Тип;Категория;Название;Сумма (сом);Банк;Источник")
        for (item in items) {
            val dateStr = sdf.format(Date(item.timestamp))
            val typeStr = if (item.transactionType == TransactionType.INCOME) "Доход" else "Расход"
            val amountStr = String.format(Locale.US, "%.2f", item.amount)
            val safeTitle = item.title.replace(";", ",")
            sb.appendLine("$dateStr;$typeStr;${item.categoryEnum.title};$safeTitle;$amountStr;${item.bankName};${item.source}")
        }
        file.writeText(sb.toString(), Charsets.UTF_8)
        return file
    }
}
