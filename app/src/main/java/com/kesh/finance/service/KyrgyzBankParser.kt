package com.kesh.finance.service

import com.kesh.finance.data.Category
import com.kesh.finance.data.TransactionType
import java.util.Locale

enum class KyrgyzBank(
    val displayName: String,
    val packageHints: List<String>,
    val titleKeywords: List<String>,
    val accentHex: Long
) {
    MBANK("MBank", listOf("mbank", "cbk"), listOf("mbank", "мбанк"), 0xFF00A859),
    ODENGI("О!Деньги", listOf("odengi", "nurtelecom", "o.kg"), listOf("о!деньги", "o!dengi", "о! деньги"), 0xFFE6007E),
    OPTIMA24("Optima24", listOf("optima", "atf"), listOf("optima", "оптима"), 0xFFE30613),
    BAKAI24("Bakai24", listOf("bakai"), listOf("bakai", "бакай"), 0xFF005AA7),
    DEMIRBANK("DemirBank", listOf("demir"), listOf("demir", "демир"), 0xFF009640),
    KICB("KICB", listOf("kicb"), listOf("kicb", "кикб"), 0xFF003B71),
    ELKART("Элкарт Мобайл", listOf("elkart", "ipc"), listOf("элкарт", "elkart"), 0xFF1D4ED8),
    MEGAPAY("MegaPay", listOf("megapay", "megacom", "alfatelecom"), listOf("megapay", "мегапей"), 0xFF7C3AED),
    BALANCE_KG("Balance.kg", listOf("balance", "beeline"), listOf("balance", "баланс"), 0xFFFACC15),
    KOMPANION("Компаньон", listOf("kompanion"), listOf("компаньон", "kompanion"), 0xFF16A34A),
    ELDIK("Элдик Банк", listOf("eldik", "rsk"), listOf("элдик", "eldik", "рск"), 0xFF0284C7),
    AIYL_BANK("Айыл Банк", listOf("aiyl"), listOf("айыл", "aiyl"), 0xFF15803D),
    DOSCREDOBANK("Doscredobank", listOf("doscredo", "dcb"), listOf("doscredo", "доскредо", "dcb"), 0xFFEA580C),
    FINCA("FINCA Bank", listOf("finca"), listOf("finca", "финка"), 0xFFDC2626),
    KEREMET("Керемет Банк", listOf("keremet"), listOf("керемет", "keremet"), 0xFF0D9488),
    BANK_OF_ASIA("Банк Азии", listOf("bankasia"), listOf("банк азии", "bank of asia"), 0xFFB45309),
    HALYK("Halyk Bank", listOf("halyk"), listOf("halyk", "халык"), 0xFF047857),
    KSB("KSB Bank", listOf("ksb"), listOf("ksb", "ксб"), 0xFF1E40AF),
    CAPITAL_BANK("Capital Bank", listOf("capitalbank"), listOf("capital bank", "капитал банк"), 0xFF4338CA),
    BAI_TUSHUM("Бай-Тушум", listOf("baitushum", "btb"), listOf("бай-тушум", "бай тушум", "baitushum"), 0xFF059669)
}

data class ParsedBankNotification(
    val bank: KyrgyzBank,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val merchantTitle: String,
    val rawText: String
)

object KyrgyzBankParser {

    private val ignoreKeywords = listOf(
        "код подтверждения", "пароль", "otp", "одноразовый", "вход в",
        "акция", "кэшбэк до", "выиграй", "кредит одобрен", "обновление"
    )

    private val incomeKeywords = listOf(
        "пополнение", "поступило", "зачисление", "зачислено", "входящий перевод",
        "перевод от", "возврат", "кэшбэк", "приход", "получено"
    )

    private val expenseKeywords = listOf(
        "оплата", "списание", "списано", "покупка", "перевод", "платеж", "платёж",
        "снятие", "qr", "вы успешно оплатили", "переведено"
    )

    fun detectBank(packageName: String, title: String): KyrgyzBank? {
        val pkg = packageName.lowercase(Locale.ROOT)
        val ttl = title.lowercase(Locale("ru"))

        KyrgyzBank.entries.firstOrNull { bank ->
            bank.packageHints.any { hint -> pkg.contains(hint) }
        }?.let { return it }

        return KyrgyzBank.entries.firstOrNull { bank ->
            bank.titleKeywords.any { kw -> ttl.contains(kw) }
        }
    }

    fun parseNotification(
        packageName: String,
        title: String,
        text: String
    ): ParsedBankNotification? {
        val bank = detectBank(packageName, title) ?: return null
        val fullText = "$title. $text".trim()
        val lower = fullText.lowercase(Locale("ru"))

        if (ignoreKeywords.any { lower.contains(it) }) return null

        val hasActionKeyword = incomeKeywords.any { lower.contains(it) } ||
            expenseKeywords.any { lower.contains(it) } ||
            lower.contains("сом") || lower.contains("kgs")
        if (!hasActionKeyword) return null

        val amount = extractTransactionAmount(fullText) ?: return null
        if (amount <= 0.0 || amount > 50_000_000.0) return null

        val isIncome = incomeKeywords.any { lower.contains(it) } &&
            !lower.contains("оплата") && !lower.contains("списание")
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val merchant = extractMerchant(text, bank.displayName)
        val category = detectCategory(lower, type)

        return ParsedBankNotification(
            bank = bank,
            amount = amount,
            type = type,
            category = category,
            merchantTitle = merchant,
            rawText = fullText
        )
    }

    private fun extractTransactionAmount(text: String): Double? {
        val cleaned = text
            .replace(Regex("""(?i)(остаток|баланс|доступно)\s*:?\s*[\d\s.,]+"""), " ")
            .replace(Regex("""\*\d{4}"""), " ")

        val currencyRegex = Regex(
            """([+-]?\s*\d[\d\s]{0,12}(?:[.,]\d{1,2})?)\s*(?:сом|сомов|kgs|с\.)""",
            RegexOption.IGNORE_CASE
        )
        currencyRegex.find(cleaned)?.let { match ->
            val rawNum = match.groupValues[1]
                .replace("+", "")
                .replace("-", "")
                .replace(" ", "")
                .replace(",", ".")
            rawNum.toDoubleOrNull()?.let { return it }
        }

        val labelRegex = Regex(
            """(?:сумма|оплата|списание|пополнение|перевод)\s*:?\s*(\d[\d\s]{0,12}(?:[.,]\d{1,2})?)""",
            RegexOption.IGNORE_CASE
        )
        labelRegex.find(cleaned)?.let { match ->
            val rawNum = match.groupValues[1]
                .replace(" ", "")
                .replace(",", ".")
            rawNum.toDoubleOrNull()?.let { return it }
        }

        return null
    }

    private fun extractMerchant(text: String, fallbackBank: String): String {
        val patterns = listOf(
            Regex("""(?:в пользу|получатель|точка|мерчант|в)\s+["«]?([A-Za-zА-Яа-яЁё0-9 ._-]{2,28})["»]?""", RegexOption.IGNORE_CASE),
            Regex("""QR\s*[-:]?\s*([A-Za-zА-Яа-яЁё0-9 ._-]{2,28})""", RegexOption.IGNORE_CASE)
        )
        for (regex in patterns) {
            regex.find(text)?.let { m ->
                val candidate = m.groupValues[1].trim()
                if (candidate.length >= 2 && !candidate.all { it.isDigit() }) {
                    return candidate
                }
            }
        }
        return "$fallbackBank (авто)"
    }

    fun detectCategory(lowerText: String, type: TransactionType): Category {
        if (type == TransactionType.INCOME) {
            return when {
                lowerText.contains("зарплат") || lowerText.contains("аванс") -> Category.SALARY
                lowerText.contains("кэшбэк") || lowerText.contains("бонус") -> Category.CASHBACK
                lowerText.contains("перевод") || lowerText.contains("пополн") -> Category.TRANSFER_IN
                else -> Category.OTHER_INCOME
            }
        }

        return when {
            listOf("глобус", "globus", "народн", "фрунзе", "frunze", "азия", "маркет", "продукт", "супермаркет")
                .any { lowerText.contains(it) } -> Category.GROCERIES
            listOf("кафе", "кофе", "coffee", "glovo", "яндекс еда", "шаурм", "фастфуд", "kfc", "ресторан", "столов", "бублик", "сиерра")
                .any { lowerText.contains(it) } -> Category.FOOD
            listOf("такси", "яндекс go", "yandex", "автобус", "маршрут", "тулпар", "tulpar", "азс", "газпром", "бензин", "парков")
                .any { lowerText.contains(it) } -> Category.TRANSPORT
            listOf("wildberries", "ozon", "одежд", "магазин", "цум", "гум", "дордой", "техно", "sulpak", "мой телефон")
                .any { lowerText.contains(it) } -> Category.SHOPPING
            listOf("северэлектро", "бишкектепло", "о!", "megacom", "мегаком", "beeline", "билайн", "акнет", "интернет", "жкх", "связь")
                .any { lowerText.contains(it) } -> Category.BILLS
            listOf("аптек", "неман", "лекар", "клиник", "бонецк", "анализ", "врач", "стомат")
                .any { lowerText.contains(it) } -> Category.HEALTH
            listOf("кино", "билет", "игр", "steam", "playstation", "подписк", "youtube", "spotify")
                .any { lowerText.contains(it) } -> Category.ENTERTAINMENT
            lowerText.contains("перевод") -> Category.TRANSFER
            else -> Category.OTHER_EXPENSE
        }
    }
}
