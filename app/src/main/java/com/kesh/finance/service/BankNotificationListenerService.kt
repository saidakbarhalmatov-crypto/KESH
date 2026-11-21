package com.kesh.finance.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.kesh.finance.data.KeshPreferences
import com.kesh.finance.data.KeshRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BankNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()
            ?: extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT)?.toString()

        serviceScope.launch {
            val prefs = KeshPreferences(applicationContext)
            val isEnabled = prefs.autoBankEnabled.first()
            if (!isEnabled) return@launch

            val parsed = KyrgyzBankParser.parseNotification(
                packageName = sbn.packageName.orEmpty(),
                title = title,
                text = text,
                subText = subText
            ) ?: return@launch

            val repo = KeshRepository(applicationContext)
            repo.addTransaction(
                amount = parsed.amount,
                type = parsed.type,
                category = parsed.category,
                title = parsed.merchant,
                bankName = parsed.bank.displayName,
                source = "AUTO_BANK",
                note = parsed.rawText,
                timestamp = sbn.postTime
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        fun isNotificationAccessEnabled(context: Context): Boolean {
            val enabledListeners = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            ).orEmpty()
            val componentName = ComponentName(context, BankNotificationListenerService::class.java)
            return enabledListeners.contains(componentName.flattenToString()) ||
                enabledListeners.contains(componentName.flattenToShortString())
        }
    }
}
