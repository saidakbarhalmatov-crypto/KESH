package com.kesh.finance.service

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kesh.finance.MainActivity
import java.util.Calendar

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            scheduleDailyReminders(context)
            return
        }

        val slot = intent.getIntExtra("slot", 1)
        val (title, body) = when (slot) {
            1 -> "Обеденный учёт KESH" to "Не забудь записать утренние и обеденные расходы или поступления!"
            2 -> "Перемена • Проверь баланс" to "Если покупал перекус или напитки — занеси в KESH голосом или текстом."
            else -> "Итоги дня в KESH" to "Запиши вечерние расходы и прибыль за сегодня, чтобы лимит был точным."
        }

        showNotification(context, slot, title, body)
        scheduleSingleSlot(context, slot)
    }

    private fun showNotification(context: Context, slot: Int, title: String, body: String) {
        val channelId = "kesh_daily_reminders"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Напоминания о записи расходов",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPending = PendingIntent.getActivity(
            context,
            slot,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(contentPending)
            .build()

        NotificationManagerCompat.from(context).notify(500 + slot, notification)
    }

    companion object {
        // 13:05 (после обеда), 15:05 (после большой перемены), 19:30 (вечером дома)
        private val REMINDER_SLOTS = listOf(
            Triple(1, 13, 5),
            Triple(2, 15, 5),
            Triple(3, 19, 30)
        )

        fun scheduleDailyReminders(context: Context) {
            REMINDER_SLOTS.forEach { (slot, _, _) ->
                scheduleSingleSlot(context, slot)
            }
        }

        private fun scheduleSingleSlot(context: Context, slot: Int) {
            val config = REMINDER_SLOTS.firstOrNull { it.first == slot } ?: return
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, config.second)
                set(Calendar.MINUTE, config.third)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis <= System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val intent = Intent(context, DailyReminderReceiver::class.java).apply {
                putExtra("slot", slot)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + slot,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}
