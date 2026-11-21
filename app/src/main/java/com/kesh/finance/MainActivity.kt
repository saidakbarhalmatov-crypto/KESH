package com.kesh.finance

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.kesh.finance.data.KeshPreferences
import com.kesh.finance.data.KeshRepository
import com.kesh.finance.data.OcrParsedReceipt
import com.kesh.finance.data.TransactionType
import com.kesh.finance.ocr.ReceiptOcrProcessor
import com.kesh.finance.service.BankNotificationListenerService
import com.kesh.finance.service.DailyReminderReceiver
import com.kesh.finance.ui.AddOperationSheet
import com.kesh.finance.ui.AiAssistantSheet
import com.kesh.finance.ui.AnalyticsScreen
import com.kesh.finance.ui.KeshAppTheme
import com.kesh.finance.ui.KeshThemes
import com.kesh.finance.ui.OcrReviewSheet
import com.kesh.finance.ui.OverviewScreen
import com.kesh.finance.ui.SettingsScreen
import com.kesh.finance.ui.ThemePickerSheet
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val incomingUriState = mutableStateOf<Uri?>(null)
    private val openQuickAddState = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        DailyReminderReceiver.scheduleDailyReminders(applicationContext)
        handleIncomingIntent(intent)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val prefs = remember { KeshPreferences(context) }
            val repo = remember { KeshRepository(context) }

            val themeId by prefs.selectedThemeId.collectAsState()
            val userName by prefs.userName.collectAsState()
            val monthlyBudget by prefs.monthlyBudget.collectAsState()
            val geminiApiKey by prefs.geminiApiKey.collectAsState()
            val autoBankEnabled by prefs.autoBankEnabled.collectAsState()
            val transactions by repo.transactions.collectAsState(initial = emptyList())

            val spec = remember(themeId) { KeshThemes.byId(themeId) }

            var selectedTab by remember { mutableIntStateOf(0) }
            var showThemePicker by remember { mutableStateOf(false) }
            var showAddSheet by remember { mutableStateOf(false) }
            var showAiSheet by remember { mutableStateOf(false) }
            var prefillAmount by remember { mutableStateOf("") }
            var prefillType by remember { mutableStateOf(TransactionType.EXPENSE) }
            var prefillTitle by remember { mutableStateOf("") }
            var ocrResult by remember { mutableStateOf<OcrParsedReceipt?>(null) }

            var hasNotificationPermission by remember {
                mutableStateOf(BankNotificationListenerService.isNotificationAccessEnabled(context))
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_RESUME) {
                        hasNotificationPermission =
                            BankNotificationListenerService.isNotificationAccessEnabled(context)
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
            }

            val incomingUri by incomingUriState
            LaunchedEffect(incomingUri) {
                val uri = incomingUri ?: return@LaunchedEffect
                incomingUriState.value = null
                Toast.makeText(context, "Сканирую чек...", Toast.LENGTH_SHORT).show()
                ocrResult = ReceiptOcrProcessor.recognizeReceiptFromUri(context, uri)
            }

            val quickAddTrigger by openQuickAddState
            LaunchedEffect(quickAddTrigger) {
                if (quickAddTrigger) {
                    openQuickAddState.value = false
                    prefillAmount = ""
                    prefillType = TransactionType.EXPENSE
                    prefillTitle = ""
                    showAddSheet = true
                }
            }

            val pickReceiptLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    scope.launch {
                        Toast.makeText(context, "Сканирую чек...", Toast.LENGTH_SHORT).show()
                        ocrResult = ReceiptOcrProcessor.recognizeReceiptFromUri(context, uri)
                    }
                }
            }

            KeshAppTheme(spec = spec) {
                Scaffold(
                    containerColor = spec.background,
                    bottomBar = {
                        NavigationBar(containerColor = spec.surface) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Rounded.AccountBalanceWallet, contentDescription = "Обзор") },
                                label = { Text("Обзор") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Rounded.PieChart, contentDescription = "Аналитика") },
                                label = { Text("Аналитика") }
                            )
                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = { Icon(Icons.Rounded.Settings, contentDescription = "Настройки") },
                                label = { Text("Настройки") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        when (selectedTab) {
                            0 -> OverviewScreen(
                                spec = spec,
                                userName = userName,
                                monthlyBudget = monthlyBudget,
                                isAutoBankActive = autoBankEnabled && hasNotificationPermission,
                                transactions = transactions,
                                onOpenThemes = { showThemePicker = true },
                                onOpenAdd = {
                                    prefillAmount = ""
                                    prefillType = TransactionType.EXPENSE
                                    prefillTitle = ""
                                    showAddSheet = true
                                },
                                onScanReceipt = { pickReceiptLauncher.launch("image/*") },
                                onOpenAiAssistant = { showAiSheet = true },
                                onDeleteTransaction = { tx ->
                                    scope.launch { repo.deleteTransaction(tx) }
                                }
                            )

                            1 -> AnalyticsScreen(
                                spec = spec,
                                userName = userName,
                                isAutoBankActive = autoBankEnabled && hasNotificationPermission,
                                transactions = transactions,
                                onOpenThemes = { showThemePicker = true }
                            )

                            2 -> SettingsScreen(
                                spec = spec,
                                userName = userName,
                                monthlyBudget = monthlyBudget,
                                geminiApiKey = geminiApiKey,
                                autoBankEnabled = autoBankEnabled,
                                hasNotificationPermission = hasNotificationPermission,
                                onUpdateUserName = { prefs.setUserName(it) },
                                onUpdateBudget = { prefs.setMonthlyBudget(it) },
                                onUpdateGeminiApiKey = { prefs.setGeminiApiKey(it) },
                                onToggleAutoBank = { prefs.setAutoBankEnabled(it) },
                                onOpenNotificationSettings = {
                                    context.startActivity(
                                        Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        }
                                    )
                                },
                                onOpenThemes = { showThemePicker = true },
                                onExportCsv = {
                                    val file = repo.exportCsv(context, transactions)
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/csv"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Экспорт KESH CSV"))
                                },
                                onClearAll = {
                                    scope.launch {
                                        repo.clearAll()
                                        Toast.makeText(context, "База очищена", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }

                    if (showAiSheet) {
                        AiAssistantSheet(
                            spec = spec,
                            apiKey = geminiApiKey,
                            repo = repo,
                            onDismiss = { showAiSheet = false },
                            onTransactionSaved = { parsed ->
                                scope.launch {
                                    repo.addTransaction(
                                        amount = parsed.amount,
                                        type = parsed.type,
                                        category = parsed.category,
                                        title = parsed.title,
                                        bankName = parsed.bankName,
                                        source = "AI_ASSISTANT"
                                    )
                                    showAiSheet = false
                                    Toast.makeText(
                                        context,
                                        "Записано: ${parsed.title} (${parsed.amount} сом)",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    if (showAddSheet) {
                        AddOperationSheet(
                            spec = spec,
                            initialAmount = prefillAmount,
                            initialType = prefillType,
                            initialTitle = prefillTitle,
                            onDismiss = { showAddSheet = false },
                            onSave = { amt, type, cat, title, bank ->
                                scope.launch {
                                    repo.addTransaction(
                                        amount = amt,
                                        type = type,
                                        category = cat,
                                        title = title,
                                        bankName = bank
                                    )
                                    showAddSheet = false
                                }
                            }
                        )
                    }

                    ocrResult?.let { parsed ->
                        OcrReviewSheet(
                            spec = spec,
                            parsed = parsed,
                            onDismiss = { ocrResult = null },
                            onConfirm = { amt, type, cat, title, bank ->
                                scope.launch {
                                    repo.addTransaction(
                                        amount = amt,
                                        type = type,
                                        category = cat,
                                        title = title,
                                        bankName = bank,
                                        source = "OCR_RECEIPT"
                                    )
                                    ocrResult = null
                                }
                            }
                        )
                    }

                    if (showThemePicker) {
                        ThemePickerSheet(
                            currentThemeId = themeId,
                            spec = spec,
                            onSelectTheme = { prefs.setTheme(it) },
                            onDismiss = { showThemePicker = false }
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == Intent.ACTION_VIEW && intent.data?.scheme == "kesh") {
            openQuickAddState.value = true
        } else if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }
            if (uri != null) {
                incomingUriState.value = uri
            }
        }
    }
}
