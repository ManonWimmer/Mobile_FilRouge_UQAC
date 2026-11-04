package com.grman.healthybet.alarm
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.grman.healthybet.MainActivity
import com.grman.healthybet.data.alarms.AlarmData
import java.util.Calendar

object AlarmScheduler {


    private const val PREFS = "scheduled_alarms"
    private const val KEY_IDS = "ids"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun trackedIds(context: Context): Set<String> =
        prefs(context).getStringSet(KEY_IDS, emptySet()).orEmpty()

    private fun track(context: Context, id: String) {
        val ids = trackedIds(context).toMutableSet().apply { add(id) }
        prefs(context).edit().putStringSet(KEY_IDS, ids).apply()
    }

    private fun untrack(context: Context, id: String) {
        val ids = trackedIds(context).toMutableSet().apply { remove(id) }
        prefs(context).edit().putStringSet(KEY_IDS, ids).apply()
    }

    /* Retourne l'heure de déclenchement (ms) ou null si la permission d'alarme exacte manque */
    fun schedule(context: Context, alarm: AlarmData): Long? {
        val am = context.getSystemService(AlarmManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) return null

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarmId", alarm.id)
            putExtra("stepGoal", alarm.stepGoal)
            putExtra("durationMinutes", alarm.durationMinutes)
        }
        val requestCode = alarm.id.hashCode()
        val operation = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val showIntent = PendingIntent.getActivity(
            context, requestCode, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, showIntent), operation)
        track(context, alarm.id)
        return cal.timeInMillis
    }

    fun cancel(context: Context, alarmId: String) {
        val am = context.getSystemService(AlarmManager::class.java)
        val operation = PendingIntent.getBroadcast(
            context, alarmId.hashCode(), Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        untrack(context, alarmId)
        am.cancel(operation)
    }

    /** Annule toutes les alarmes programmées par cette app sur le téléphone. */
    fun cancelAll(context: Context) {
        trackedIds(context).toList().forEach { cancel(context, it) }
    }
}