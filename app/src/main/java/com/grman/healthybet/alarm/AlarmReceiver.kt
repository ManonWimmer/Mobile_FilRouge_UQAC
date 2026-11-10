package com.grman.healthybet.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, AlarmService::class.java).apply {
            putExtras(intent)
        }
        ContextCompat.startForegroundService(context, service)
    }
}