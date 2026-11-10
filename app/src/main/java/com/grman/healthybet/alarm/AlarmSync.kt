package com.grman.healthybet.alarm

import android.content.Context
import com.grman.healthybet.data.alarms.AlarmRepository

object AlarmSync {

    private var generation = 0

    /* Annule tout, puis programme les alarmes actives de [uid] (null = déconnecté) */
    fun sync(context: Context, uid: String?) {
        val app = context.applicationContext
        val current = ++generation

        AlarmScheduler.cancelAll(app)
        if (uid == null) return

        AlarmRepository.getEnabledAlarms(uid) { alarms ->
            if (current != generation) return@getEnabledAlarms
            alarms.forEach { AlarmScheduler.schedule(app, it) }
        }
    }
}