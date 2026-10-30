package com.grman.healthybet.alarm
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.grman.healthybet.MainActivity
import java.util.Calendar

object AlarmScheduler {

    /* Retourne l'heure de déclenchement (ms) ou null si la permission d'alarme exacte manque */
    fun schedule(
        context: Context,
        hour: Int,
        minute: Int,
        objective: String,
        id: Int = 1
    ): Long? {
        val am = context.getSystemService(AlarmManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            return null
        }

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("id", id)
            putExtra("objective", objective)
        }
        val operation = PendingIntent.getBroadcast(
            context, id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Intent ouvert quand on tape sur l'icône d'alarme
        val showIntent = PendingIntent.getActivity(
            context, id, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        // setAlarmClock = meme doze
        am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, showIntent), operation)
        return cal.timeInMillis
    }

    fun cancel(context: Context, id: Int = 1) {
        val am = context.getSystemService(AlarmManager::class.java)
        val operation = PendingIntent.getBroadcast(
            context, id, Intent(context, AlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.cancel(operation)
    }
}