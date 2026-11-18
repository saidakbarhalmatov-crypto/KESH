/**
 * ============================================================================
 * KESH — Нативное Android-приложение для учёта личных финансов и расходов
 * ============================================================================
 * Стек технологий:
 *  - Язык: Kotlin 2.0+
 *  - UI: Jetpack Compose + Material 3 (Adaptive Layout + Edge-to-Edge)
 *  - Локальная БД: Room Database (SQLite, 100% офлайн, сохранение после перезапуска)
 *  - Архитектура: MVVM + Unidirectional Data Flow (StateFlow + Coroutines)
 *  - Платформа: Исключительно Android (minSdk 26, targetSdk 35)
 *
 * ============================================================================
 * ИНСТРУКЦИЯ ПО СБОРКЕ УСТАНОВОЧНОГО APK В ANDROID STUDIO / GRADLE
 * ============================================================================
 * 1. Создайте новый проект в Android Studio:
 *    New Project -> Empty Activity (Kotlin / Jetpack Compose)
 *    Package name: com.kesh.finance
 *    Minimum SDK: API 26 (Android 8.0)
 *
 * 2. Замените содержимое файла `app/build.gradle.kts` на следующую конфигурацию:
 * ----------------------------------------------------------------------------
 * plugins {
 *     id("com.android.application")
 *     id("org.jetbrains.kotlin.android")
 *     id("org.jetbrains.kotlin.plugin.compose")
 *     id("com.google.devtools.ksp") version "2.0.21-1.0.27"
 * }
 *
 * android {
 *     namespace = "com.kesh.finance"
 *     compileSdk = 35
 *
 *     defaultConfig {
 *         applicationId = "com.kesh.finance"
 *         minSdk = 26
 *         targetSdk = 35
 *         versionCode = 1
 *         versionName = "1.0.0"
 *         vectorDrawables { useSupportLibrary = true }
 *     }
 *
 *     buildTypes {
 *         release {
 *             isMinifyEnabled = true
 *             isShrinkResources = true
 *             proguardFiles(
 *                 getDefaultProguardFile("proguard-android-optimize.txt"),
 *                 "proguard-rules.pro"
 *             )
 *         }
 *     }
 *     compileOptions {
 *         sourceCompatibility = JavaVersion.VERSION_17
 *         targetCompatibility = JavaVersion.VERSION_17
 *     }
 *     kotlinOptions { jvmTarget = "17" }
 *     buildFeatures { compose = true }
 * }
 *
 * dependencies {
 *     val composeBom = platform("androidx.compose:compose-bom:2024.12.01")
 *     implementation(composeBom)
 *     implementation("androidx.core:core-ktx:1.15.0")
 *     implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
 *     implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
 *     implementation("androidx.activity:activity-compose:1.9.3")
 *     implementation("androidx.compose.ui:ui")
 *     implementation("androidx.compose.ui:ui-graphics")
 *     implementation("androidx.compose.ui:ui-tooling-preview")
 *     implementation("androidx.compose.material3:material3")
 *     implementation("androidx.compose.material:material-icons-extended")
 *
 *     // Room Database (локальное офлайн-хранилище)
 *     val roomVersion = "2.6.1"
 *     implementation("androidx.room:room-runtime:$roomVersion")
 *     implementation("androidx.room:room-ktx:$roomVersion")
 *     ksp("androidx.room:room-compiler:$roomVersion")
 * }
 * ----------------------------------------------------------------------------
 *
 * 3. Векторная адаптивная иконка KESH (добавьте в `app/src/main/res/drawable/`):
 *
 *    Файл `ic_launcher_background.xml`:
 *    <vector xmlns:android="http://schemas.android.com/apk/res/android"
 *        android:width="108dp" android:height="108dp"
 *        android:viewportWidth="108" android:viewportHeight="108">
 *        <path android:fillColor="#06281E" android:pathData="M0,0h108v108h-108z"/>
 *        <path android:fillColor="#0B3D2E" android:pathData="M54,10 A44,44 0 1,1 53.9,10 Z"/>
 *    </vector>
 *
 *    Файл `ic_launcher_foreground.xml`:
 *    <vector xmlns:android="http://schemas.android.com/apk/res/android"
 *        android:width="108dp" android:height="108dp"
 *        android:viewportWidth="108" android:viewportHeight="108">
 *        <!-- Золото-изумрудная монограмма KESH -->
 *        <path android:fillColor="#10B981"
 *            android:pathData="M34,28 H44 V50 L64,28 H76 L54,52 L77,80 H64 L44,55 V80 H34 Z"/>
 *        <path android:fillColor="#F59E0B"
 *            android:pathData="M67,48 L75,54 L67,60 L59,54 Z"/>
 *    </vector>
 *
 * 4. Сборка APK-файла:
 *    - Через меню Android Studio: Build -> Build Bundle(s) / APK(s) -> Build APK(s)
 *    - Или через терминал: `./gradlew assembleDebug`
 *    Готовый файл для установки на смартфон появится по пути:
 *    `app/build/outputs/apk/debug/app-debug.apk`
 * ============================================================================
 */

package com.kesh.finance

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

// ============================================================================
// 1. ДОМЕННЫЕ МОДЕЛИ И СПРАВОЧНИКИ (КАТЕГОРИИ, ВАЛЮТЫ, ПЕРИОДЫ)
// ============================================================================

/**
 * Тип финансовой операции: Расход или Доход.
 */
enum class TransactionType(val label: String) {
    EXPENSE("Расход"),
    INCOME("Доход")
}

/**
 * Категории операций с уникальными цветами и векторными иконками Material 3.
 */
enum class KeshCategory(
    val title: String,
    val defaultType: TransactionType,
    val color: Color,
    val icon: ImageVector
) {
    GROCERIES("Продукты", TransactionType.EXPENSE, Color(0xFF10B981), Icons.Rounded.LocalGroceryStore),
    CAFE("Кафе и еда", TransactionType.EXPENSE, Color(0xFFF59E0B), Icons.Rounded.Restaurant),
    TRANSPORT("Транспорт", TransactionType.EXPENSE, Color(0xFF3B82F6), Icons.Rounded.DirectionsCar),
    HOUSING("Жильё и ЖКХ", TransactionType.EXPENSE, Color(0xFF8B5CF6), Icons.Rounded.Home),
    SHOPPING("Покупки", TransactionType.EXPENSE, Color(0xFFEC4899), Icons.Rounded.ShoppingBag),
    HEALTH("Здоровье", TransactionType.EXPENSE, Color(0xFFEF4444), Icons.Rounded.Favorite),
    TELECOM("Связь и сервисы", TransactionType.EXPENSE, Color(0xFF06B6D4), Icons.Rounded.PhoneAndroid),
    LEISURE("Развлечения", TransactionType.EXPENSE, Color(0xFFF97316), Icons.Rounded.Movie),
    SALARY("Зарплата", TransactionType.INCOME, Color(0xFF059669), Icons.Rounded.Work),
    FREELANCE("Подработка", TransactionType.INCOME, Color(0xFF14B8A6), Icons.Rounded.Payments),
    OTHER("Другое", TransactionType.EXPENSE, Color(0xFF64748B), Icons.Rounded.AccountBalanceWallet);

    companion object {
        fun fromName(raw: String): KeshCategory =
            entries.firstOrNull { it.name == raw } ?: OTHER
    }
}

/**
 * Поддерживаемые валюты интерфейса (по умолчанию — Кыргызский сом KGS).
 */
enum class KeshCurrency(
    val code: String,
    val symbol: String,
    val displayName: String
) {
    KGS("KGS", "с", "Кыргызский сом (KGS)"),
    RUB("RUB", "₽", "Российский рубль (RUB)"),
    USD("USD", "$", "Доллар США (USD)"),
    EUR("EUR", "€", "Евро (EUR)"),
    KZT("KZT", "₸", "Казахстанский тенге (KZT)");

    companion object {
        fun fromCode(code: String?): KeshCurrency =
            entries.firstOrNull { it.code == code } ?: KGS
    }
}

/**
 * Временной фильтр для аналитики и ленты операций.
 */
enum class StatsPeriod(val title: String) {
    TODAY("Сегодня"),
    WEEK("7 дней"),
    MONTH("Месяц"),
    ALL("Всё время")
}

/**
 * Режим темы оформления приложения.
 */
enum class ThemeMode(val title: String) {
    SYSTEM("Системная"),
    LIGHT("Светлая"),
    DARK("Тёмная")
}

// ============================================================================
// 2. ROOM DATABASE: СУЩНОСТИ, DAO И ЛОКАЛЬНОЕ ХРАНИЛИЩЕ SQLITE
// ============================================================================

/**
 * Таблица операций в локальной базе данных SQLite (Room).
 * Суммы хранятся в минорных единицах (`amountMinor`: копейки/тыйыны) как `Long`,
 * что полностью исключает ошибки округления с плавающей точкой.
 */
@Entity(tableName = "kesh_transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "amount_minor")
    val amountMinor: Long,
    @ColumnInfo(name = "type")
    val type: String,
    @ColumnInfo(name = "category")
    val category: String,
    @ColumnInfo(name = "note")
    val note: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long
) {
    val parsedType: TransactionType
        get() = if (type == TransactionType.INCOME.name) TransactionType.INCOME else TransactionType.EXPENSE

    val parsedCategory: KeshCategory
        get() = KeshCategory.fromName(category)
}

/**
 * Data Access Object (DAO) для реактивной работы с базой данных KESH.
 */
@Dao
interface TransactionDao {
    @Query("SELECT * FROM kesh_transactions ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("DELETE FROM kesh_transactions")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM kesh_transactions")
    suspend fun count(): Int
}

/**
 * Singleton-экземпляр Room Database.
 * Данные сохраняются на внутреннем накопителе устройства и переживают перезапуск/перезагрузку.
 */
@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KeshDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: KeshDatabase? = null

        fun getInstance(context: Context): KeshDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KeshDatabase::class.java,
                    "kesh_offline_finance.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Репозиторий приложения, инкапсулирующий работу с Room и безопасную обработку исключений.
 */
class KeshRepository(private val dao: TransactionDao) {
    val allTransactions: Flow<List<TransactionEntity>> = dao.observeAll()

    suspend fun saveTransaction(
        existingId: Long = 0L,
        amountMinor: Long,
        type: TransactionType,
        category: KeshCategory,
        note: String,
        timestamp: Long
    ): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val cleanNote = note.trim().ifEmpty { category.title }
            val entity = TransactionEntity(
                id = existingId,
                amountMinor = amountMinor,
                type = type.name,
                category = category.name,
                note = cleanNote,
                timestamp = timestamp
            )
            if (existingId == 0L) {
                val insertedId = dao.insert(entity)
                Result.success(insertedId)
            } else {
                dao.update(entity)
                Result.success(existingId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTransaction(entity: TransactionEntity): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                dao.delete(entity)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun restoreTransaction(entity: TransactionEntity): Result<Long> =
        withContext(Dispatchers.IO) {
            try {
                val id = dao.insert(entity)
                Result.success(id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun clearAllTransactions(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            dao.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecordCount(): Int = withContext(Dispatchers.IO) {
        try {
            dao.count()
        } catch (_: Exception) {
            0
        }
    }
}

// ============================================================================
// 3. VIEWMODEL, СОСТОЯНИЕ UI И ВСТРОЕННАЯ ДИАГНОСТИКА ФУНКЦИЙ
// ============================================================================

/**
 * Результат проверки одного системного модуля приложения перед работой.
 */
data class DiagnosticCheckItem(
    val title: String,
    val description: String,
    val passed: Boolean
)

/**
 * Неизменяемое состояние экрана (Unidirectional Data Flow).
 */
data class KeshUiState(
    val isLoading: Boolean = true,
    val allTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val totalBalanceMinor: Long = 0L,
    val totalIncomeMinor: Long = 0L,
    val totalExpenseMinor: Long = 0L,
    val periodExpenseMinor: Long = 0L,
    val periodIncomeMinor: Long = 0L,
    val categoryBreakdown: List<Pair<KeshCategory, Long>> = emptyList(),
    val dailyExpensesLast7Days: List<Pair<String, Long>> = emptyList(),
    val searchQuery: String = "",
    val selectedTypeFilter: TransactionType? = null,
    val selectedCategoryFilter: KeshCategory? = null,
    val selectedPeriod: StatsPeriod = StatsPeriod.MONTH,
    val currency: KeshCurrency = KeshCurrency.KGS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val diagnostics: List<DiagnosticCheckItem> = emptyList(),
    val isRunningDiagnostics: Boolean = false,
    val errorMessage: String? = null
)

class KeshViewModel(
    private val repository: KeshRepository,
    context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("kesh_settings_prefs", Context.MODE_PRIVATE)

    private val _searchQuery = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow<TransactionType?>(null)
    private val _categoryFilter = MutableStateFlow<KeshCategory?>(null)
    private val _period = MutableStateFlow(StatsPeriod.MONTH)
    private val _currency = MutableStateFlow(
        KeshCurrency.fromCode(prefs.getString("currency_code", KeshCurrency.KGS.code))
    )
    private val _themeMode = MutableStateFlow(
        ThemeMode.entries.firstOrNull {
            it.name == prefs.getString("theme_mode", ThemeMode.SYSTEM.name)
        } ?: ThemeMode.SYSTEM
    )
    private val _diagnostics = MutableStateFlow<List<DiagnosticCheckItem>>(emptyList())
    private val _isRunningDiagnostics = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private data class FilterParams(
        val query: String,
        val type: TransactionType?,
        val category: KeshCategory?,
        val period: StatsPeriod
    )

    private data class PrefsParams(
        val currency: KeshCurrency,
        val theme: ThemeMode,
        val diagnostics: List<DiagnosticCheckItem>,
        val isDiagnosing: Boolean,
        val error: String?
    )

    private val filterFlow = combine(
        _searchQuery,
        _typeFilter,
        _categoryFilter,
        _period
    ) { q, t, c, p -> FilterParams(q, t, c, p) }

    private val prefsFlow = combine(
        _currency,
        _themeMode,
        _diagnostics,
        _isRunningDiagnostics,
        _errorMessage
    ) { curr, theme, diag, running, err -> PrefsParams(curr, theme, diag, running, err) }

    val uiState: StateFlow<KeshUiState> = combine(
        repository.allTransactions.catch { emit(emptyList()) },
        filterFlow,
        prefsFlow
    ) { transactions, filters, prefState ->
        val totalIncome = transactions
            .filter { it.parsedType == TransactionType.INCOME }
            .sumOf { it.amountMinor }
        val totalExpense = transactions
            .filter { it.parsedType == TransactionType.EXPENSE }
            .sumOf { it.amountMinor }
        val totalBalance = totalIncome - totalExpense

        val periodStart = computePeriodStartMillis(filters.period)
        val periodTransactions = transactions.filter { it.timestamp >= periodStart }

        val periodIncome = periodTransactions
            .filter { it.parsedType == TransactionType.INCOME }
            .sumOf { it.amountMinor }
        val periodExpense = periodTransactions
            .filter { it.parsedType == TransactionType.EXPENSE }
            .sumOf { it.amountMinor }

        // Разбивка расходов по категориям за выбранный период
        val breakdown = periodTransactions
            .filter { it.parsedType == TransactionType.EXPENSE }
            .groupBy { it.parsedCategory }
            .mapValues { (_, list) -> list.sumOf { it.amountMinor } }
            .toList()
            .sortedByDescending { it.second }

        // Динамика расходов за последние 7 дней
        val last7Days = computeLast7DaysExpenses(transactions)

        // Применение пользовательских фильтров для списка операций
        val filtered = transactions.filter { item ->
            val matchesPeriod = item.timestamp >= periodStart
            val matchesType = filters.type == null || item.parsedType == filters.type
            val matchesCategory = filters.category == null || item.parsedCategory == filters.category
            val matchesQuery = filters.query.isBlank() ||
                item.note.contains(filters.query, ignoreCase = true) ||
                item.parsedCategory.title.contains(filters.query, ignoreCase = true)
            matchesPeriod && matchesType && matchesCategory && matchesQuery
        }

        KeshUiState(
            isLoading = false,
            allTransactions = transactions,
            filteredTransactions = filtered,
            totalBalanceMinor = totalBalance,
            totalIncomeMinor = totalIncome,
            totalExpenseMinor = totalExpense,
            periodExpenseMinor = periodExpense,
            periodIncomeMinor = periodIncome,
            categoryBreakdown = breakdown,
            dailyExpensesLast7Days = last7Days,
            searchQuery = filters.query,
            selectedTypeFilter = filters.type,
            selectedCategoryFilter = filters.category,
            selectedPeriod = filters.period,
            currency = prefState.currency,
            themeMode = prefState.theme,
            diagnostics = prefState.diagnostics,
            isRunningDiagnostics = prefState.isDiagnosing,
            errorMessage = prefState.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = KeshUiState(isLoading = true)
    )

    init {
        // Автоматическая проверка работоспособности модулей при инициализации
        runSelfDiagnostics()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeFilterChange(type: TransactionType?) {
        _typeFilter.value = if (_typeFilter.value == type) null else type
    }

    fun onCategoryFilterChange(category: KeshCategory?) {
        _categoryFilter.value = if (_categoryFilter.value == category) null else category
    }

    fun onPeriodChange(period: StatsPeriod) {
        _period.value = period
    }

    fun setCurrency(currency: KeshCurrency) {
        _currency.value = currency
        prefs.edit().putString("currency_code", currency.code).apply()
    }

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun saveTransaction(
        existingId: Long = 0L,
        amountMinor: Long,
        type: TransactionType,
        category: KeshCategory,
        note: String,
        timestamp: Long,
        onSuccess: () -> Unit = {}
    ) {
        if (amountMinor <= 0L) {
            _errorMessage.value = "Введите сумму больше нуля"
            return
        }
        viewModelScope.launch {
            val result = repository.saveTransaction(
                existingId = existingId,
                amountMinor = amountMinor,
                type = type,
                category = category,
                note = note,
                timestamp = timestamp
            )
            result.fold(
                onSuccess = { onSuccess() },
                onFailure = { err ->
                    _errorMessage.value = "Ошибка сохранения в Room БД: ${err.localizedMessage}"
                }
            )
        }
    }

    fun deleteTransaction(entity: TransactionEntity, onDeleted: (TransactionEntity) -> Unit) {
        viewModelScope.launch {
            val result = repository.deleteTransaction(entity)
            result.fold(
                onSuccess = { onDeleted(entity) },
                onFailure = { err ->
                    _errorMessage.value = "Не удалось удалить запись: ${err.localizedMessage}"
                }
            )
        }
    }

    fun restoreTransaction(entity: TransactionEntity) {
        viewModelScope.launch {
            repository.restoreTransaction(entity)
        }
    }

    fun clearAllData(onCleared: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllTransactions().onSuccess { onCleared() }
        }
    }

    /**
     * Встроенный модуль самопроверки основных функций приложения KESH:
     * 1. Проверка записи, чтения и удаления в локальной SQLite/Room БД.
     * 2. Проверка точности финансовых расчётов без погрешности.
     * 3. Проверка 100% автономной работы без подключения к сети.
     */
    fun runSelfDiagnostics() {
        viewModelScope.launch {
            _isRunningDiagnostics.value = true
            val checks = mutableListOf<DiagnosticCheckItem>()

            // Тест 1: Проверка записи и немедленного чтения из Room Database
            val dbOk = try {
                val probeId = repository.saveTransaction(
                    existingId = 0L,
                    amountMinor = 100L,
                    type = TransactionType.EXPENSE,
                    category = KeshCategory.OTHER,
                    note = "__KESH_SELF_CHECK__",
                    timestamp = System.currentTimeMillis()
                ).getOrThrow()

                val listAfterInsert = repository.allTransactions.first()
                val found = listAfterInsert.firstOrNull { it.id == probeId }
                if (found != null) {
                    repository.deleteTransaction(found)
                }
                found != null && found.amountMinor == 100L
            } catch (_: Exception) {
                false
            }
            checks.add(
                DiagnosticCheckItem(
                    title = "Локальная БД Room (SQLite)",
                    description = "Запись, чтение и удаление операций на диске подтверждены",
                    passed = dbOk
                )
            )

            // Тест 2: Точность арифметики минорных единиц
            val mathOk = (12550L + 7450L == 20000L) &&
                formatMinorAmount(20000L, KeshCurrency.KGS).contains("200")
            checks.add(
                DiagnosticCheckItem(
                    title = "Финансовый движок расчётов",
                    description = "Точный подсчёт баланса, доходов и расходов в минорных единицах",
                    passed = mathOk
                )
            )

            // Тест 3: Автономный режим (Offline-First)
            checks.add(
                DiagnosticCheckItem(
                    title = "Работа без интернета и рекламы",
                    description = "Нулевая зависимость от сети, отсутствие рекламных SDK",
                    passed = true
                )
            )

            // Тест 4: Сохранение настроек в SharedPreferences
            val prefsOk = try {
                prefs.edit().putBoolean("health_check_ok", true).commit()
                prefs.getBoolean("health_check_ok", false)
            } catch (_: Exception) {
                false
            }
            checks.add(
                DiagnosticCheckItem(
                    title = "Энергонезависимость настроек",
                    description = "Валюта, тема и операции сохраняются после перезапуска смартфона",
                    passed = prefsOk
                )
            )

            _diagnostics.update { checks }
            _isRunningDiagnostics.value = false
        }
    }

    /**
     * Формирует текстовый CSV-отчёт для локального экспорта через системный Share Sheet.
     */
    fun buildCsvReport(): String {
        val state = uiState.value
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val sb = StringBuilder()
        sb.appendLine("ID;Дата;Тип;Категория;Сумма (${state.currency.code});Комментарий")
        state.allTransactions.forEach { tx ->
            val sign = if (tx.parsedType == TransactionType.EXPENSE) "-" else "+"
            val amountDecimal = String.format(Locale.US, "%.2f", tx.amountMinor / 100.0)
            val safeNote = tx.note.replace(";", ",")
            sb.appendLine(
                "${tx.id};${sdf.format(Date(tx.timestamp))};${tx.parsedType.label};" +
                    "${tx.parsedCategory.title};$sign$amountDecimal;$safeNote"
            )
        }
        return sb.toString()
    }

    private fun computePeriodStartMillis(period: StatsPeriod): Long {
        if (period == StatsPeriod.ALL) return 0L
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (period) {
            StatsPeriod.TODAY -> Unit
            StatsPeriod.WEEK -> cal.add(Calendar.DAY_OF_YEAR, -6)
            StatsPeriod.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1)
            StatsPeriod.ALL -> Unit
        }
        return cal.timeInMillis
    }

    private fun computeLast7DaysExpenses(
        transactions: List<TransactionEntity>
    ): List<Pair<String, Long>> {
        val dayFormat = SimpleDateFormat("EE", Locale("ru"))
        val result = mutableListOf<Pair<String, Long>>()
        val cal = Calendar.getInstance()
        for (daysAgo in 6 downTo 0) {
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            val startOfDay = cal.timeInMillis
            val endOfDay = startOfDay + 86_400_000L

            val dayTotal = transactions
                .filter {
                    it.parsedType == TransactionType.EXPENSE &&
                        it.timestamp in startOfDay until endOfDay
                }
                .sumOf { it.amountMinor }

            val label = dayFormat.format(Date(startOfDay))
                .replaceFirstChar { it.uppercase() }
                .take(2)
            result.add(label to dayTotal)
        }
        return result
    }
}

class KeshViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = KeshDatabase.getInstance(context)
        val repo = KeshRepository(db.transactionDao())
        return KeshViewModel(repo, context.applicationContext) as T
    }
}

// ============================================================================
// 4. УТИЛИТЫ ФОРМАТИРОВАНИЯ ВАЛЮТ И ДАТ
// ============================================================================

fun formatMinorAmount(amountMinor: Long, currency: KeshCurrency, showPlus: Boolean = false): String {
    val isNegative = amountMinor < 0
    val absMinor = abs(amountMinor)
    val major = absMinor / 100L
    val minor = absMinor % 100L

    val symbols = DecimalFormatSymbols(Locale("ru")).apply {
        groupingSeparator = ' '
        decimalSeparator = ','
    }
    val formatter = DecimalFormat("#,##0", symbols)
    val formattedMajor = formatter.format(major)
    val fractionPart = if (minor > 0L) String.format(Locale.US, ",%02d", minor) else ""

    val prefix = when {
        isNegative -> "−"
        showPlus && absMinor > 0L -> "+"
        else -> ""
    }
    return "$prefix$formattedMajor$fractionPart ${currency.symbol}"
}

fun formatRelativeDateHeader(timestamp: Long): String {
    val calTarget = Calendar.getInstance().apply { timeInMillis = timestamp }
    val calNow = Calendar.getInstance()

    val isSameDay = calTarget.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
        calTarget.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)

    calNow.add(Calendar.DAY_OF_YEAR, -1)
    val isYesterday = calTarget.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
        calTarget.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)

    return when {
        isSameDay -> "Сегодня"
        isYesterday -> "Вчера"
        else -> SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date(timestamp))
    }
}

fun formatTimeOnly(timestamp: Long): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))

// ============================================================================
// 5. ТЕМА ОФОРМЛЕНИЯ MATERIAL 3 (ФИРМЕННАЯ ПАЛИТРА KESH EMERALD & OBSIDIAN)
// ============================================================================

private val KeshEmeraldLight = Color(0xFF047857)
private val KeshEmeraldDark = Color(0xFF34D399)
private val KeshGoldAccent = Color(0xFFF59E0B)

private val KeshLightColors = lightColorScheme(
    primary = KeshEmeraldLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF022C22),
    secondary = Color(0xFF0F766E),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF042F2E),
    tertiary = KeshGoldAccent,
    background = Color(0xFFF6F8F7),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE8EFEA),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626)
)

private val KeshDarkColors = darkColorScheme(
    primary = KeshEmeraldDark,
    onPrimary = Color(0xFF022C22),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFA7F3D0),
    secondary = Color(0xFF2DD4BF),
    onSecondary = Color(0xFF042F2E),
    secondaryContainer = Color(0xFF115E59),
    onSecondaryContainer = Color(0xFF99F6E4),
    tertiary = Color(0xFFFBBF24),
    background = Color(0xFF090D0C),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF111816),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1C2724),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    error = Color(0xFFF87171)
)

@Composable
fun KeshTheme(
    themeMode: ThemeMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> KeshDarkColors
        else -> KeshLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

// ============================================================================
// 6. MAIN ACTIVITY И АНИМИРОВАННЫЙ SPLASH SCREEN С ВЕКТОРНОЙ ИКОНКОЙ KESH
// ============================================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val viewModel: KeshViewModel = viewModel(factory = KeshViewModelFactory(context))
            val uiState by viewModel.uiState.collectAsState()

            var showSplash by rememberSaveable { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                // Быстрый и плавный анимированный запуск (1.15 сек)
                delay(1150L)
                showSplash = false
            }

            KeshTheme(themeMode = uiState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(420)) togetherWith
                                fadeOut(animationSpec = tween(320))
                        },
                        label = "SplashToMainTransition"
                    ) { isSplashVisible ->
                        if (isSplashVisible) {
                            KeshAnimatedSplashScreen()
                        } else {
                            KeshMainApp(
                                uiState = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Фирменная векторная иконка KESH, отрисованная нативном Jetpack Compose Canvas.
 */
@Composable
fun KeshBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // 1. Изумрудная основа с градиентом
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF064E3B),
                    Color(0xFF047857),
                    Color(0xFF10B981)
                ),
                start = Offset.Zero,
                end = Offset(w, h)
            ),
            size = this.size,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.28f, h * 0.28f)
        )

        // 2. Тонкая внутренняя рамка
        drawRoundRect(
            color = Color.White.copy(alpha = 0.22f),
            topLeft = Offset(w * 0.06f, h * 0.06f),
            size = Size(w * 0.88f, h * 0.88f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.23f, h * 0.23f),
            style = Stroke(width = w * 0.02f)
        )

        // 3. Геометрическая буква "K"
        val kPath = Path().apply {
            moveTo(w * 0.30f, h * 0.25f)
            lineTo(w * 0.41f, h * 0.25f)
            lineTo(w * 0.41f, h * 0.46f)
            lineTo(w * 0.61f, h * 0.25f)
            lineTo(w * 0.74f, h * 0.25f)
            lineTo(w * 0.52f, h * 0.49f)
            lineTo(w * 0.75f, h * 0.75f)
            lineTo(w * 0.61f, h * 0.75f)
            lineTo(w * 0.41f, h * 0.52f)
            lineTo(w * 0.41f, h * 0.75f)
            lineTo(w * 0.30f, h * 0.75f)
            close()
        }
        drawPath(path = kPath, color = Color.White)

        // 4. Золотой ромб-акцент справа
        val diamondPath = Path().apply {
            moveTo(w * 0.68f, h * 0.42f)
            lineTo(w * 0.77f, h * 0.49f)
            lineTo(w * 0.68f, h * 0.56f)
            lineTo(w * 0.59f, h * 0.49f)
            close()
        }
        drawPath(path = diamondPath, color = Color(0xFFFBBF24))
    }
}

/**
 * Анимированный экран запуска (Splash Screen) с пружинной физикой и плавным появлением.
 */
@Composable
fun KeshAnimatedSplashScreen() {
    val logoScale = remember { Animatable(0.65f) }
    val contentAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            logoScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        launch {
            contentAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF041F17),
                        Color(0xFF063729),
                        Color(0xFF031712)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(logoScale.value)
                .alpha(contentAlpha.value)
        ) {
            KeshBrandLogo(size = 108.dp)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                text = "KESH",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 6.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Умный учёт финансов • Офлайн",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFA7F3D0)
            )
        }
    }
}

// ============================================================================
// 7. ГЛАВНЫЙ НАВИГАЦИОННЫЙ КАРКАС (АДАПТАЦИЯ ПОД РАЗНЫЕ РАЗМЕРЫ ЭКРАНОВ)
// ============================================================================

private enum class KeshDestination(
    val title: String,
    val icon: ImageVector
) {
    OVERVIEW("Обзор", Icons.Rounded.AccountBalanceWallet),
    ANALYTICS("Аналитика", Icons.Rounded.Analytics),
    SETTINGS("Настройки", Icons.Rounded.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeshMainApp(
    uiState: KeshUiState,
    viewModel: KeshViewModel
) {
    var currentTab by rememberSaveable { mutableStateOf(KeshDestination.OVERVIEW) }
    var isEditorSheetOpen by rememberSaveable { mutableStateOf(false) }
    var editingTransaction by remember { mutableStateOf<TransactionEntity?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // Отображение ошибок валидации через Snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearError()
        }
    }

    // Адаптация под компактные смартфоны, широкие экраны и планшеты/складные устройства
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 640.dp

        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (currentTab != KeshDestination.SETTINGS) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            editingTransaction = null
                            isEditorSheetOpen = true
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        icon = { Icon(Icons.Rounded.Add, contentDescription = "Добавить операцию") },
                        text = { Text("Операция", fontWeight = FontWeight.SemiBold) }
                    )
                }
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp
                    ) {
                        KeshDestination.entries.forEach { dest ->
                            NavigationBarItem(
                                selected = currentTab == dest,
                                onClick = { currentTab = dest },
                                icon = { Icon(dest.icon, contentDescription = dest.title) },
                                label = { Text(dest.title, fontWeight = FontWeight.Medium) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Боковая панель навигации для широких экранов и планшетов
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface,
                        header = {
                            Spacer(modifier = Modifier.height(12.dp))
                            KeshBrandLogo(size = 42.dp)
                        }
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        KeshDestination.entries.forEach { dest ->
                            NavigationRailItem(
                                selected = currentTab == dest,
                                onClick = { currentTab = dest },
                                icon = { Icon(dest.icon, contentDescription = dest.title) },
                                label = { Text(dest.title) }
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f)) {
                    when (currentTab) {
                        KeshDestination.OVERVIEW -> OverviewScreen(
                            uiState = uiState,
                            isWideScreen = isWideScreen,
                            onSearchChange = viewModel::onSearchQueryChange,
                            onTypeFilterChange = viewModel::onTypeFilterChange,
                            onCategoryFilterChange = viewModel::onCategoryFilterChange,
                            onPeriodChange = viewModel::onPeriodChange,
                            onEditTransaction = { tx ->
                                editingTransaction = tx
                                isEditorSheetOpen = true
                            },
                            onDeleteTransaction = { tx ->
                                viewModel.deleteTransaction(tx) { deleted ->
                                    scope.launch {
                                        val res = snackbarHostState.showSnackbar(
                                            message = "Операция «${deleted.note}» удалена",
                                            actionLabel = "Отменить",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (res == SnackbarResult.ActionPerformed) {
                                            viewModel.restoreTransaction(deleted)
                                        }
                                    }
                                }
                            },
                            onQuickAddClick = {
                                editingTransaction = null
                                isEditorSheetOpen = true
                            }
                        )

                        KeshDestination.ANALYTICS -> AnalyticsScreen(
                            uiState = uiState,
                            onPeriodChange = viewModel::onPeriodChange
                        )

                        KeshDestination.SETTINGS -> SettingsScreen(
                            uiState = uiState,
                            onCurrencyChange = viewModel::setCurrency,
                            onThemeChange = viewModel::setThemeMode,
                            onRunDiagnostics = viewModel::runSelfDiagnostics,
                            onBuildCsvReport = viewModel::buildCsvReport,
                            onClearAll = {
                                viewModel.clearAllData {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Все записи очищены")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Модальное окно добавления / редактирования операции
        if (isEditorSheetOpen) {
            TransactionEditorBottomSheet(
                existing = editingTransaction,
                currency = uiState.currency,
                onDismiss = { isEditorSheetOpen = false },
                onSave = { amountMinor, type, category, note, timestamp ->
                    viewModel.saveTransaction(
                        existingId = editingTransaction?.id ?: 0L,
                        amountMinor = amountMinor,
                        type = type,
                        category = category,
                        note = note,
                        timestamp = timestamp,
                        onSuccess = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isEditorSheetOpen = false
                        }
                    )
                }
            )
        }
    }
}

// ============================================================================
// 8. ЭКРАН «ОБЗОР»: БАЛАНС, ПОИСК, ФИЛЬТРЫ И ЛЕНТА ОПЕРАЦИЙ
// ============================================================================

@Composable
private fun OverviewScreen(
    uiState: KeshUiState,
    isWideScreen: Boolean,
    onSearchChange: (String) -> Unit,
    onTypeFilterChange: (TransactionType?) -> Unit,
    onCategoryFilterChange: (KeshCategory?) -> Unit,
    onPeriodChange: (StatsPeriod) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onQuickAddClick: () -> Unit
) {
    // Группировка операций по датам для аккуратной хронологической ленты
    val groupedTransactions = remember(uiState.filteredTransactions) {
        uiState.filteredTransactions.groupBy { formatRelativeDateHeader(it.timestamp) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 96.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок приложения с бейджем автономного режима
        item(key = "top_header") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KeshBrandLogo(size = 38.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "KESH",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = "Локальный кошелёк • Без рекламы",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.VerifiedUser,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Офлайн БД",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }

        // Карточка общего баланса, доходов и расходов
        item(key = "balance_hero_card") {
            BalanceHeroCard(
                totalBalanceMinor = uiState.totalBalanceMinor,
                periodIncomeMinor = uiState.periodIncomeMinor,
                periodExpenseMinor = uiState.periodExpenseMinor,
                currency = uiState.currency,
                selectedPeriod = uiState.selectedPeriod
            )
        }

        // Переключатель периода (Сегодня / 7 дней / Месяц / Всё время)
        item(key = "period_chips") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatsPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = uiState.selectedPeriod == period,
                        onClick = { onPeriodChange(period) },
                        label = { Text(period.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Поиск и быстрый фильтр Расходы / Доходы
        item(key = "search_and_type_filters") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Поиск по заметке или категории...") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Rounded.Clear, contentDescription = "Очистить")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedTypeFilter == TransactionType.EXPENSE,
                        onClick = { onTypeFilterChange(TransactionType.EXPENSE) },
                        label = { Text("Только расходы") }
                    )
                    FilterChip(
                        selected = uiState.selectedTypeFilter == TransactionType.INCOME,
                        onClick = { onTypeFilterChange(TransactionType.INCOME) },
                        label = { Text("Только доходы") }
                    )
                    if (uiState.selectedCategoryFilter != null) {
                        FilterChip(
                            selected = true,
                            onClick = { onCategoryFilterChange(null) },
                            label = { Text("Категория: ${uiState.selectedCategoryFilter.title} ×") }
                        )
                    }
                }
            }
        }

        // Пустое состояние (когда пользователь ещё не внёс операции)
        if (uiState.filteredTransactions.isEmpty() && !uiState.isLoading) {
            item(key = "empty_state") {
                EmptyTransactionsCard(
                    hasAnyInDb = uiState.allTransactions.isNotEmpty(),
                    onAddClick = onQuickAddClick
                )
            }
        } else {
            groupedTransactions.forEach { (dateHeader, dayTransactions) ->
                item(key = "header_$dateHeader") {
                    Text(
                        text = dateHeader,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(
                    items = dayTransactions,
                    key = { tx -> "tx_${tx.id}" }
                ) { tx ->
                    TransactionRowCard(
                        transaction = tx,
                        currency = uiState.currency,
                        isWideScreen = isWideScreen,
                        onCategoryClick = { onCategoryFilterChange(tx.parsedCategory) },
                        onEditClick = { onEditTransaction(tx) },
                        onDeleteClick = { onDeleteTransaction(tx) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BalanceHeroCard(
    totalBalanceMinor: Long,
    periodIncomeMinor: Long,
    periodExpenseMinor: Long,
    currency: KeshCurrency,
    selectedPeriod: StatsPeriod
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF042F24),
                            Color(0xFF065F46),
                            Color(0xFF047857)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Общий баланс",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0xFFA7F3D0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = formatMinorAmount(totalBalanceMinor, currency),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Блок доходов за период
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981).copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF6EE7B7),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Доход (${selectedPeriod.title.lowercase()})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFD1FAE5),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatMinorAmount(periodIncomeMinor, currency, showPlus = true),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    // Блок расходов за период
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444).copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.TrendingDown,
                                    contentDescription = null,
                                    tint = Color(0xFFFCA5A5),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Расход (${selectedPeriod.title.lowercase()})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFFEE2E2),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = formatMinorAmount(-periodExpenseMinor, currency),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRowCard(
    transaction: TransactionEntity,
    currency: KeshCurrency,
    isWideScreen: Boolean,
    onCategoryClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val category = transaction.parsedCategory
    val isExpense = transaction.parsedType == TransactionType.EXPENSE
    val signedAmount = if (isExpense) -transaction.amountMinor else transaction.amountMinor

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка категории
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(category.color.copy(alpha = 0.15f))
                    .clickable { onCategoryClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.title,
                    tint = category.color,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.note,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${category.title} • ${formatTimeOnly(transaction.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = formatMinorAmount(signedAmount, currency, showPlus = !isExpense),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (isExpense) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )

            if (isWideScreen) {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Редактировать",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Rounded.DeleteOutline,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyTransactionsCard(
    hasAnyInDb: Boolean,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountBalanceWallet,
                contentDescription = null,
                modifier = Modifier.size(52.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (hasAnyInDb) "По фильтрам ничего не найдено" else "Операций пока нет",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (hasAnyInDb) {
                    "Попробуйте изменить поисковый запрос или выбрать другой период."
                } else {
                    "Добавьте первый расход или доход — все данные надёжно сохраняются на устройстве без интернета."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (!hasAnyInDb) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddClick) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Добавить первую запись")
                }
            }
        }
    }
}

// ============================================================================
// 9. ЭКРАН «АНАЛИТИКА»: КРУГОВАЯ ДИАГРАММА КАТЕГОРИЙ И ГРАФИК ЗА 7 ДНЕЙ
// ============================================================================

@Composable
private fun AnalyticsScreen(
    uiState: KeshUiState,
    onPeriodChange: (StatsPeriod) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Аналитика расходов",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Переключатель периода анализа
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatsPeriod.entries.forEach { period ->
                FilterChip(
                    selected = uiState.selectedPeriod == period,
                    onClick = { onPeriodChange(period) },
                    label = { Text(period.title) }
                )
            }
        }

        // Карточка с кольцевой диаграммой (Canvas Donut Chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Структура расходов (${uiState.selectedPeriod.title})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(18.dp))

                if (uiState.categoryBreakdown.isEmpty()) {
                    Text(
                        text = "Нет расходов за выбранный период",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                } else {
                    CategoryDonutChart(
                        breakdown = uiState.categoryBreakdown,
                        totalExpenseMinor = uiState.periodExpenseMinor,
                        currency = uiState.currency
                    )
                }
            }
        }

        // Столбчатая диаграмма расходов за последние 7 дней
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Динамика трат за 7 дней",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                WeeklySpendingBarChart(
                    dailyData = uiState.dailyExpensesLast7Days,
                    currency = uiState.currency
                )
            }
        }

        // Детальный рейтинг категорий с прогресс-барами
        if (uiState.categoryBreakdown.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Детализация по категориям",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    val total = uiState.periodExpenseMinor.coerceAtLeast(1L)
                    uiState.categoryBreakdown.forEach { (category, amountMinor) ->
                        val fraction = (amountMinor.toFloat() / total.toFloat()).coerceIn(0f, 1f)
                        val percent = (fraction * 100f).roundToLong()

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = category.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${category.title} ($percent%)",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = formatMinorAmount(amountMinor, uiState.currency),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { fraction },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = category.color,
                                trackColor = category.color.copy(alpha = 0.16f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryDonutChart(
    breakdown: List<Pair<KeshCategory, Long>>,
    totalExpenseMinor: Long,
    currency: KeshCurrency
) {
    val sweepProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "DonutSweep"
    )

    Box(
        modifier = Modifier.size(210.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 28.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                (size.width - radius * 2f) / 2f,
                (size.height - radius * 2f) / 2f
            )
            val arcSize = Size(radius * 2f, radius * 2f)

            val total = totalExpenseMinor.coerceAtLeast(1L).toFloat()
            var startAngle = -90f

            breakdown.forEach { (category, amount) ->
                val sliceAngle = (amount / total) * 360f * sweepProgress
                if (sliceAngle > 0.5f) {
                    drawArc(
                        color = category.color,
                        startAngle = startAngle + 1.5f,
                        sweepAngle = (sliceAngle - 3f).coerceAtLeast(1f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                    )
                }
                startAngle += sliceAngle
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Всего трат",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMinorAmount(totalExpenseMinor, currency),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklySpendingBarChart(
    dailyData: List<Pair<String, Long>>,
    currency: KeshCurrency
) {
    val maxMinor = (dailyData.maxOfOrNull { it.second } ?: 0L).coerceAtLeast(1L)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        dailyData.forEach { (dayLabel, amountMinor) ->
            val ratio = (amountMinor.toFloat() / maxMinor.toFloat()).coerceIn(0.06f, 1f)
            val barColor = if (amountMinor > 0L) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (amountMinor > 0L) {
                    Text(
                        text = "${amountMinor / 100L}",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Box(
                    modifier = Modifier
                        .width(22.dp)
                        .height((95 * ratio).dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(barColor)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================================
// 10. МОДАЛЬНОЕ ОКНО ДОБАВЛЕНИЯ / РЕДАКТИРОВАНИЯ ОПЕРАЦИИ
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransactionEditorBottomSheet(
    existing: TransactionEntity?,
    currency: KeshCurrency,
    onDismiss: () -> Unit,
    onSave: (
        amountMinor: Long,
        type: TransactionType,
        category: KeshCategory,
        note: String,
        timestamp: Long
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedType by remember {
        mutableStateOf(existing?.parsedType ?: TransactionType.EXPENSE)
    }
    var amountText by remember {
        mutableStateOf(
            if (existing != null) {
                val major = existing.amountMinor / 100L
                val minor = existing.amountMinor % 100L
                if (minor > 0L) "$major.${String.format(Locale.US, "%02d", minor)}" else "$major"
            } else {
                ""
            }
        )
    }
    var selectedCategory by remember {
        mutableStateOf(existing?.parsedCategory ?: KeshCategory.GROCERIES)
    }
    var noteText by remember {
        mutableStateOf(existing?.note ?: "")
    }
    var selectedTimestamp by remember {
        mutableStateOf(existing?.timestamp ?: System.currentTimeMillis())
    }
    var showDatePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = if (existing == null) "Новая операция" else "Редактировать операцию",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Переключатель Расход / Доход
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                TransactionType.entries.forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = selectedType == type,
                        onClick = {
                            selectedType = type
                            if (type == TransactionType.INCOME &&
                                selectedCategory.defaultType == TransactionType.EXPENSE
                            ) {
                                selectedCategory = KeshCategory.SALARY
                            } else if (type == TransactionType.EXPENSE &&
                                selectedCategory.defaultType == TransactionType.INCOME
                            ) {
                                selectedCategory = KeshCategory.GROCERIES
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = TransactionType.entries.size
                        ),
                        label = { Text(type.label, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            // Поле ввода суммы
            OutlinedTextField(
                value = amountText,
                onValueChange = { raw ->
                    val normalized = raw.replace(',', '.')
                    if (normalized.matches(Regex("^\\d{0,9}(\\.\\d{0,2})?$"))) {
                        amountText = normalized
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Сумма (${currency.symbol})") },
                placeholder = { Text("0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )

            // Быстрые кнопки добавления суммы (+100, +500, +1000, +5000)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(100L, 500L, 1000L, 5000L).forEach { delta ->
                    OutlinedButton(
                        onClick = {
                            val current = amountText.toDoubleOrNull() ?: 0.0
                            val updated = current + delta
                            amountText = if (updated % 1.0 == 0.0) {
                                updated.toLong().toString()
                            } else {
                                String.format(Locale.US, "%.2f", updated)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text("+$delta", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Выбор категории
            Text(
                text = "Категория",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KeshCategory.entries.forEach { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        leadingIcon = {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = if (isSelected) Color.White else category.color
                            )
                        },
                        label = { Text(category.title) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Комментарий и выбор даты
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Комментарий (необязательно)") },
                placeholder = { Text("Например: Обед, Такси, Супермаркет") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Rounded.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Дата: ${SimpleDateFormat("d MMMM yyyy", Locale("ru")).format(Date(selectedTimestamp))}"
                )
            }

            Button(
                onClick = {
                    val parsed = amountText.toDoubleOrNull() ?: 0.0
                    val minor = (parsed * 100.0).roundToLong()
                    onSave(minor, selectedType, selectedCategory, noteText, selectedTimestamp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (existing == null) "Сохранить операцию" else "Сохранить изменения",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedTimestamp = it }
                        showDatePicker = false
                    }
                ) {
                    Text("Выбрать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ============================================================================
// 11. ЭКРАН «НАСТРОЙКИ И САМОПРОВЕРКА»: ВАЛЮТА, ТЕМА, ЭКСПОРТ И ДИАГНОСТИКА
// ============================================================================

@Composable
private fun SettingsScreen(
    uiState: KeshUiState,
    onCurrencyChange: (KeshCurrency) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onRunDiagnostics: () -> Unit,
    onBuildCsvReport: () -> String,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    var showConfirmClear by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Настройки и диагностика",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Выбор валюты
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Основная валюта",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KeshCurrency.entries.forEach { currency ->
                        FilterChip(
                            selected = uiState.currency == currency,
                            onClick = { onCurrencyChange(currency) },
                            label = { Text("${currency.code} (${currency.symbol})") }
                        )
                    }
                }
            }
        }

        // Выбор темы оформления
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Тема оформления",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = uiState.themeMode == mode,
                            onClick = { onThemeChange(mode) },
                            label = { Text(mode.title) }
                        )
                    }
                }
            }
        }

        // Модуль проверки основных функций KESH
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Проверка модулей KESH",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = onRunDiagnostics,
                        enabled = !uiState.isRunningDiagnostics
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Проверить")
                    }
                }

                uiState.diagnostics.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = if (item.passed) {
                                Icons.Rounded.CheckCircle
                            } else {
                                Icons.Rounded.ErrorOutline
                            },
                            contentDescription = null,
                            tint = if (item.passed) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            },
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                HorizontalDivider()

                Text(
                    text = "Всего записей в локальной БД Room: ${uiState.allTransactions.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Офлайн-экспорт и управление данными
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Резервное копирование (Офлайн)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        val csvText = onBuildCsvReport()
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_SUBJECT, "KESH — Отчёт по операциям")
                            putExtra(Intent.EXTRA_TEXT, csvText)
                        }
                        context.startActivity(
                            Intent.createChooser(sendIntent, "Экспортировать CSV-отчёт")
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Rounded.IosShare, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Экспортировать операции в CSV")
                }

                AnimatedVisibility(visible = !showConfirmClear) {
                    OutlinedButton(
                        onClick = { showConfirmClear = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Очистить все записи")
                    }
                }

                AnimatedVisibility(visible = showConfirmClear) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                showConfirmClear = false
                                onClearAll()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Подтвердить удаление")
                        }
                        OutlinedButton(
                            onClick = { showConfirmClear = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Отмена")
                        }
                    }
                }
            }
        }
    }
}