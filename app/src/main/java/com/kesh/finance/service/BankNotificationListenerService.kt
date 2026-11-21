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
import kotlinx.coroutines.launch

class BankNotificationListenerService : NotificationListenerService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val notification = sbn?.notification ?: return
        val packageName = sbn.packageName ?: return
        if (packageName == applicationContext.packageName) return

        val prefs = KeshPreferences(applicationContext)
        if (!prefs.autoBankEnabled.value) return

        val extras = notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val text = bigText ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        if (title.isBlank() && text.isBlank()) return

        val parsed = KyrgyzBankParser.parseNotification(packageName, title, text) ?: return

        serviceScope.launch {
            val repo = KeshRepository(applicationContext)
            repo.addAutoBankTransaction(
                amount = parsed.amount,
                type = parsed.type,
                category = parsed.category,
                title = parsed.merchantTitle,
                bankName = parsed.bank.displayName,
                rawNote = parsed.rawText
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        fun isNotificationAccessEnabled(context: Context): Boolean {
            val cn = ComponentName(context, BankNotificationListenerService::class.java)
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            return !flat.isNullOrEmpty() && flat.contains(cn.flattenToString())
        }
    }
}
