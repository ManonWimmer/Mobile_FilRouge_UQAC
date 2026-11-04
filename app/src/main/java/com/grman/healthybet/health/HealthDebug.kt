package com.grman.healthybet.data.health

import android.util.Log

object HealthDebug {
    const val ENABLED = true          // passe à false pour couper tous les logs
    const val TAG = "HEALTH_DEBUG"
    const val POLL_MS = 5_000L       // fréquence du debug automatique à l'écran Réveils

    fun debug(msg: String) {
        if (ENABLED)
            Log.d(TAG, msg)
    }

    fun error(msg: String, t: Throwable? = null) {
        if (ENABLED)
            Log.e(TAG, msg, t)
    }
}