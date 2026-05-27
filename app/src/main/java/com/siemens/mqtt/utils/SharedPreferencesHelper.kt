package com.siemens.mqtt.utils

import android.content.Context
import android.content.SharedPreferences
import com.siemens.mqtt.models.BrokerConfig

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveBrokerConfig(config: BrokerConfig) {
        sharedPreferences.edit().apply {
            putString(KEY_BROKER_IP, config.brokerIp)
            putInt(KEY_BROKER_PORT, config.port)
            putString(KEY_BROKER_TOPIC, config.topic)
            putString(KEY_BROKER_USERNAME, config.username)
            putString(KEY_BROKER_PASSWORD, config.password)
            putString(KEY_BROKER_CLIENT_ID, config.clientId)
            apply()
        }
    }

    fun getBrokerConfig(): BrokerConfig? {
        val ip = sharedPreferences.getString(KEY_BROKER_IP, null) ?: return null
        return BrokerConfig(
            brokerIp = ip,
            port = sharedPreferences.getInt(KEY_BROKER_PORT, 1883),
            topic = sharedPreferences.getString(KEY_BROKER_TOPIC, "") ?: "",
            username = sharedPreferences.getString(KEY_BROKER_USERNAME, "") ?: "",
            password = sharedPreferences.getString(KEY_BROKER_PASSWORD, "") ?: "",
            clientId = sharedPreferences.getString(KEY_BROKER_CLIENT_ID, "SiemensMQTTController") ?: "SiemensMQTTController"
        )
    }

    fun clearBrokerConfig() {
        sharedPreferences.edit().apply {
            remove(KEY_BROKER_IP)
            remove(KEY_BROKER_PORT)
            remove(KEY_BROKER_TOPIC)
            remove(KEY_BROKER_USERNAME)
            remove(KEY_BROKER_PASSWORD)
            remove(KEY_BROKER_CLIENT_ID)
            apply()
        }
    }

    fun saveCustomButton(index: Int, label: String, payload: String) {
        sharedPreferences.edit().apply {
            putString("custom_button_${index}_label", label)
            putString("custom_button_${index}_payload", payload)
            apply()
        }
    }

    fun getCustomButton(index: Int): Pair<String, String>? {
        val label = sharedPreferences.getString("custom_button_${index}_label", null) ?: return null
        val payload = sharedPreferences.getString("custom_button_${index}_payload", "") ?: ""
        return label to payload
    }

    companion object {
        private const val PREFS_NAME = "SiemensMQTTPrefs"
        private const val KEY_BROKER_IP = "broker_ip"
        private const val KEY_BROKER_PORT = "broker_port"
        private const val KEY_BROKER_TOPIC = "broker_topic"
        private const val KEY_BROKER_USERNAME = "broker_username"
        private const val KEY_BROKER_PASSWORD = "broker_password"
        private const val KEY_BROKER_CLIENT_ID = "broker_client_id"
    }
}