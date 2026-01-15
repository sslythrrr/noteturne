package com.sslythrrr.noteturne

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import androidx.core.content.edit

class PinManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PIN_HASH = "pin_hash"
        private const val KEY_PIN_SET = "pin_set"
    }

    fun isPinSet(): Boolean {
        return prefs.getBoolean(KEY_PIN_SET, false)
    }

    fun setPin(pin: String) {
        val hash = hashPin(pin)
        prefs.edit {
            putString(KEY_PIN_HASH, hash)
                .putBoolean(KEY_PIN_SET, true)
        }
    }

    fun verifyPin(pin: String): Boolean {
        val storedHash = prefs.getString(KEY_PIN_HASH, null) ?: return false
        val inputHash = hashPin(pin)
        return storedHash == inputHash
    }

    private fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
}