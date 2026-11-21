package com.kesh.finance.service

import com.kesh.finance.data.Category
import com.kesh.finance.data.TransactionType
import java.util.Locale

enum class KyrgyzBank(
    val displayName: String,
    val packageKeywords: List<String>,
    val titleKeywords: List<String>,
    val accentColor: Long
) {
    MBANK("MBank", listOf("mbank"), listOf("mbank", "мбанк"), 0xFF00A859),
    ODENGI("О!Деньги", listOf("odengi", "o!dengi", "nurtelecom"), listOf("о!деньги", "o!dengi", "мой кошелек"), 0xFFE91E63),
    OPTIMA("Optima24", listOf("optima"), listOf("optima", "оптима"), 0xFF1565C0),
    BAKAI("Bakai24", listOf("bakai"), listOf("bakai", "бакай"), 0xFFE65100),
    DEMIR("DemirBank", listOf("demir"), listOf("demir", "демир"), 0xFF00838F),
    KICB("KICB", listOf("kicb"), listOf("kicb", "кисб"), 0xFF283593),
    ELKART("Элкарт Мобайл", listOf("elkart"), listOf("элкарт", "elkart"), 0xFF0277BD),
    MEGAPAY("MegaPay", listOf("megapay", "megacom"), listOf("megapay", "мегапэй"), 0xFF6A1B9A),
    BALANCE("Balance.kg", listOf("balance"), listOf("balance", "баланс"), 0xFF2E7D32),
    KOMPANION("Компаньон", listOf("kompanion", "companion"), listOf("компаньон", "kompanion"), 0xFF388E3C),
    ELDIK("Элдик Банк", listOf("eldik", "aiyl", "ryok"), listOf("элдик", "айыл банк", "eldik"), 0xFF00695C),
    AIYL("Айыл Банк", listOf("aiyl"), listOf("айыл банк", "aiyl bank"), 0xFF2E7D32),
    DOSCREDO("Doscredobank", listOf("doscredo"), listOf("doscredo", "доскредо"), 0xFF0097A7),
    FINCA("FINCA Bank", listOf("finca"), listOf("finca", "финка"), 0xFF558B2F),
    KEREMET("Керемет Банк", listOf("keremet"), listOf("керемет", "keremet"), 0xFF1976D2),
    BANK_ASIA("Банк Азии", listOf("bankasia", "asia"), listOf("банк азии"), 0xFFC62828),
    HALYK("Halyk Bank", listOf("halyk"), listOf("halyk", "халык"), 0xFF00796B),
    KSB("KSB Bank", listOf("ksb"), listOf("ksb", "ксб"), 0xFF3949AB),
    CAPITAL("Capital Bank", listOf("capital"), listOf("capital bank", "капитал"), 0xFF4527A0),
    BAI_TUSHUM("Бай-Тушум", listOf("baitushum", "tushum"), listOf("бай-тушум", "бай тушум"), 0xFF6D4C41)
}

data class ParsedBankNotification(
    val bank: KyrgyzBank,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val merchant: String,
    val rawText: String
)

object KyrgyzBankParser {

    private val ignoreKeywords = listOf(
        "код подтверждения", "otp", "одноразовый пароль", "вход в приложение",
        "обновите приложение", "акция", "скидка", "промокод"
    )

    private val incomeKeywords = listOf(
        "пополнение", "поступило", "зачислено", "входящий перевод",
        "перевод от", "кэшбэк", "кешбэк", "возврат", "зарплата", "прибыль"
    )

    private val expenseKeywords = listOf(
        "списание", "оплата", "платеж", "платёж", "покупка",
        "перевод", "снятие", "комиссия", "успешный платеж", "успешный платёж"
    )

    fun detectBank(packageName: String, title: String, text: String = ""): KyrgyzBank? {
        val pkg = packageName.lowercase(Locale.US)
        val combinedTitle = "$title $text".lowercase(Locale("ru"))

        KyrgyzBank.entries.firstOrNull { bank ->
            bank.packageKeywords.any { kw -> pkg.contains(kw) }
        }?.let { return it }

        return KyrgyzBank.entries.firstOrNull { bank ->
            bank.titleKeywords.any { kw -> combinedTitle.contains(kw) }
        }
    }

    fun parseNotification(
        packageName: String,
        title: String?,
        text: String?,
        subText: String? = null
    ): ParsedBankNotification? {
        val safeTitle = title.orEmpty().trim()
        val safeText = text.orEmpty().trim()
        val safeSub = subText.orEmpty().trim()
        val fullText = listOf(safeTitle, safeText, safeSub)
            .filter { it.isNotBlank() && !it.equals("null", ignoreCase = true) }
            .joinToString(" • ")

        if (fullText.isBlank()) return null

        val lower = fullText.lowercase(Locale("ru"))
        if (ignoreKeywords.any { lower.contains(it) }) return null

        val bank = detectBank(packageName, safeTitle, "$safeText $safeSub") ?: return null

        val hasActionWord = incomeKeywords.any { lower.contains(it) } ||
            expenseKeywords.any { lower.contains(it) } ||
            lower.contains("kgs") || lower.contains("сом") || lower.contains("kg")
        if (!hasActionWord) return null

        val amount = extractTransactionAmount(fullText) ?: return null
        if (amount <= 0.0) return null

        val isIncome = incomeKeywords.any { lower.contains(it) } &&
            !lower.contains("оплата") &&
            !lower.contains("успешный платеж") &&
            !lower.contains("успешный платёж") &&
            !lower.contains("покупка")
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val merchant = extractMerchant(fullText, bank.displayName)
        val category = detectCategory(lower, type)

        return ParsedBankNotification(
            bank = bank,
            amount = amount,
            type = type,
            category = category,
            merchant = merchant,
            rawText = fullText
        )
    }

    private fun extractTransactionAmount(text: String): Double? {
        val cleaned = text
            .replace(Regex("""(?i)(остаток|баланс|доступно)\s*:?\s*\d[\d\s.,]*"""), " ")
            .replace(Regex("""\b\d{1,2}:\d{2}(?::\d{2})?\b"""), " ")

        val currencyRegex = Regex(
            """(\d[\d\s]{0,10}(?:[.,]\d{1,2})?)\s*(?:сом|сомов|kgs|kg|с\.)""",
            RegexOption.IGNORE_CASE
        )
        currencyRegex.find(cleaned)?.let { match ->
            val rawNum = match.groupValues[1].replace(" ", "").replace(",", ".")
            rawNum.toDoubleOrNull()?.let { if (it > 0.0) return it }
        }

        val labelRegex = Regex(
            """(?:сумма|оплата|платеж|платёж|пополнение|перевод|списание|покупка)\s*:?\s*(\d[\d\s]{0,10}(?:[.,]\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        labelRegex.find(cleaned)?.let { match ->
            val rawNum = match.groupValues[1].replace(" ", "").replace(",", ".")
            rawNum.toDoubleOrNull()?.let { if (it > 0.0) return it }
        }

        val anyNumber = Regex("""\b(\d{1,8}(?:[.,]\d{1,2})?)\b""").find(cleaned)
        return anyNumber?.groupValues?.get(1)?.replace(",", ".")?.toDoubleOrNull()
    }

    private fun extractMerchant(fullText: String, fallbackBank: String): String {
        val patterns = listOf(
            Regex("""(?:куда|получатель|мерчант|точка|в)\s*:?\s*([А-Яа-яЁёA-Za-z0-9._ -]{2,32})""", RegexOption.IGNORE_CASE),
            Regex("""(?:пополнение|оплата|платеж|платёж|покупка|перевод)\s*:?\s*\d[\d\s.,]*\s*(?:kgs|сом|kg)?\s*([А-Яа-яЁёA-Za-z0-9._ -]{2,32})""", RegexOption.IGNORE_CASE)
        )
        for (p in patterns) {
            val match = p.find(fullText) ?: continue
            val candidate = match.groupValues[1].trim().trimEnd('.', ',', '•', ' ')
            if (candidate.length >= 2 && !candidate.all { it.isDigit() || it == '.' || it == ',' }) {
                return candidate
            }
        }
        return fallbackBank
    }

    fun detectCategory(lowerText: String, type: TransactionType): Category {
        if (type == TransactionType.INCOME) {
            return when {
                lowerText.contains("зарплат") || lowerText.contains("аванс") -> Category.SALARY
                lowerText.contains("кэшбэк") || lowerText.contains("кешбэк") || lowerText.contains("бонус") -> Category.CASHBACK
                lowerText.contains("перевод") || lowerText.contains("пополнение") -> Category.TRANSFER_IN
                else -> Category.OTHER_INCOME
            }
        }

        return when {
            listOf("глобус", "народный", "фрунзе", "супермаркет", "маркет", "продукт", "cash-in")
                .any { lowerText.contains(it) } -> Category.GROCERIES
            listOf("кафе", "кофе", "coffee", "ресторан", "столовая", "бургер", "шаурма", "еда", "food")
                .any { lowerText.contains(it) } -> Category.FOOD
            listOf("такси", "яндекс", "маршрут", "тулпар", "азс", "бензин", "транспорт", "qr")
                .any { lowerText.contains(it) } -> Category.TRANSPORT
            listOf("wildberries", "ozon", "магазин", "одежда", "техника", "shop")
                .any { lowerText.contains(it) } -> Category.SHOPPING
            listOf("свет", "электричеств", "коммунал", "интернет", "связь", "мегаком", "о!", "билайн")
                .any { lowerText.contains(it) } -> Category.BILLS
            listOf("аптека", "больниц", "клиник", "врач", "health")
                .any { lowerText.contains(it) } -> Category.HEALTH
            listOf("кино", "игры", "steam", "playstation", "подписк", "youtube", "spotify")
                .any { lowerText.contains(it) } -> Category.ENTERTAINMENT
            listOf("перевод", "с2с", "p2p")
                .any { lowerText.contains(it) } -> Category.TRANSFER
            else -> Category.OTHER_EXPENSE
        }
    }
}
