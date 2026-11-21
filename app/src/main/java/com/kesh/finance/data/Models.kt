package com.kesh.finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransactionType {
    EXPENSE,
    INCOME
}

enum class Category(
    val title: String,
    val isIncome: Boolean,
    val colorHex: Long
) {
    FOOD("Еда и кафе", false, 0xFFF97316),
    GROCERIES("Продукты", false, 0xFF22C55E),
    TRANSPORT("Транспорт и такси", false, 0xFF38BDF8),
    SHOPPING("Покупки", false, 0xFFA855F7),
    BILLS("Связь и ЖКХ", false, 0xFFEAB308),
    HEALTH("Здоровье", false, 0xFFEF4444),
    ENTERTAINMENT("Развлечения", false, 0xFFEC4899),
    TRANSFER("Переводы", false, 0xFF64748B),
    OTHER_EXPENSE("Прочее", false, 0xFF94A3B8),

    SALARY("Зарплата", true, 0xFF10B981),
    FREELANCE("Фриланс / Бизнес", true, 0xFF06B6D4),
    TRANSFER_IN("Пополнение", true, 0xFF3B82F6),
    CASHBACK("Кэшбэк / Бонус", true, 0xFFD4AF37),
    OTHER_INCOME("Другой доход", true, 0xFF84CC16);

    companion object {
        fun forType(type: TransactionType): List<Category> {
            return entries.filter { it.isIncome == (type == TransactionType.INCOME) }
        }

        fun fromNameSafe(name: String, fallbackType: TransactionType): Category {
            return entries.find { it.name == name }
                ?: if (fallbackType == TransactionType.INCOME) OTHER_INCOME else OTHER_EXPENSE
        }
    }
}

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String,
    val category: String,
    val title: String,
    val bankName: String = "Вручную",
    val note: String = "",
    val source: String = "MANUAL",
    val timestamp: Long = System.currentTimeMillis()
) {
    val transactionType: TransactionType
        get() = if (type == TransactionType.INCOME.name) TransactionType.INCOME else TransactionType.EXPENSE

    val categoryEnum: Category
        get() = Category.fromNameSafe(category, transactionType)
}

data class OcrParsedReceipt(
    val amount: Double?,
    val merchant: String,
    val bankName: String,
    val category: Category,
    val type: TransactionType,
    val rawText: String
)
