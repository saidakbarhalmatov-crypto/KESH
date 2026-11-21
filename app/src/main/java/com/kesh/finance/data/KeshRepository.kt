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
        bankName: String = "Вручную",
        note: String = "",
        source: String = "MANUAL",
        timestamp: Long = System.currentTimeMillis()
    ) {
        if (amount <= 0.0) return
        dao.insert(
            TransactionEntity(
                amount = amount,
                type = type.name,
                category = category.name,
                title = title.trim().ifEmpty { category.title },
                bankName = bankName.trim().ifEmpty { "Вручную" },
                note = note.trim(),
                source = source,
                timestamp = timestamp
            )
        )
    }

    suspend fun addAutoBankTransaction(
        amount: Double,
        type: TransactionType,
        category: Category,
        title: String,
        bankName: String,
        rawNote: String
    ): Boolean {
        if (amount <= 0.0) return false
        val now = System.currentTimeMillis()
        val duplicates = dao.countRecentDuplicate(
            amount = amount,
            bankName = bankName,
            sinceTimestamp = now - 60_000L
        )
        if (duplicates > 0) return false

        dao.insert(
            TransactionEntity(
                amount = amount,
                type = type.name,
                category = category.name,
                title = title.trim().ifEmpty { bankName },
                bankName = bankName,
                note = rawNote.take(220),
                source = "AUTO_BANK",
                timestamp = now
            )
        )
        return true
    }

    suspend fun deleteTransaction(item: TransactionEntity) {
        dao.delete(item)
    }

    suspend fun clearAll() {
        dao.deleteAll()
    }

    fun exportCsv(context: Context, items: List<TransactionEntity>): File {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val file = File(context.cacheDir, "KESH_export_${System.currentTimeMillis()}.csv")
        val sb = StringBuilder()
        sb.append('\uFEFF')
        sb.append("Дата;Тип;Категория;Описание;Банк;Сумма (сом);Источник\n")
        for (item in items) {
            val dateStr = formatter.format(Date(item.timestamp))
            val typeStr = if (item.transactionType == TransactionType.INCOME) "Доход" else "Расход"
            val cleanTitle = item.title.replace(";", ",")
            val cleanBank = item.bankName.replace(";", ",")
            sb.append("$dateStr;$typeStr;${item.categoryEnum.title};$cleanTitle;$cleanBank;${item.amount};${item.source}\n")
        }
        file.writeText(sb.toString(), Charsets.UTF_8)
        return file
    }

    fun parseVoiceInput(spokenText: String): Triple<Double, TransactionType, String>? {
        val lower = spokenText.lowercase(Locale("ru"))
        val numRegex = Regex("""(\d+(?:[.,]\d+)?)""")
        val match = numRegex.find(lower) ?: return null
        val amount = match.groupValues[1].replace(',', '.').toDoubleOrNull() ?: return null
        if (amount <= 0.0) return null

        val incomeWords = listOf(
            "доход", "зарплата", "пополнил", "пополнение", "перевели",
            "пришло", "заработал", "кэшбэк", "бонус", "стипендия"
        )
        val isIncome = incomeWords.any { lower.contains(it) }
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val cleanedTitle = spokenText
            .replace(match.value, "")
            .replace(Regex("""\b(сом|сомов|сома|потратил|купил|на|за|доход|расход)\b""", RegexOption.IGNORE_CASE), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString() }

        return Triple(amount, type, cleanedTitle.ifEmpty { if (isIncome) "Голосовой доход" else "Голосовой расход" })
    }
}
