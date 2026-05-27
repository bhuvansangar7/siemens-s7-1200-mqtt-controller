package com.siemens.mqtt.utils

import android.content.Context
import android.util.Log
import com.siemens.mqtt.models.BrokerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCancellableCoroutine

class MqttManager(
    private val context: Context,
    private val onConnectionStatusChanged: (Boolean) -> Unit
) {

    private var mqttClient: MqttClient? = null
    private var brokerConfig: BrokerConfig? = null
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 10
    private val reconnectDelayMs = 3000L

    init {
        Log.d(TAG, "MqttManager initialized")
    }

    suspend fun connect(config: BrokerConfig) = withContext(Dispatchers.IO) {
        try {
            brokerConfig = config
            val brokerUrl = "tcp://${config.brokerIp}:${config.port}"
            Log.d(TAG, "Connecting to broker: $brokerUrl")

            mqttClient = MqttClient(brokerUrl, config.clientId, null)
            mqttClient?.setCallback(object : MqttCallback {
                override fun connectionLost(cause: Throwable?) {
                    Log.w(TAG, "Connection lost", cause)
                    onConnectionStatusChanged(false)
                    attemptReconnect()
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    Log.d(TAG, "Message arrived on $topic: ${message?.payload?.let { String(it) }}")
                }

                override fun deliveryComplete(token: IMqttToken?) {
                    Log.d(TAG, "Message delivery complete")
                }
            })

            val options = MqttConnectOptions()
            options.isCleanSession = true
            options.automaticReconnect = true
            options.isAutomaticReconnect = true
            options.connectionTimeout = 10
            options.keepAliveInterval = 60

            if (config.username.isNotEmpty() && config.password.isNotEmpty()) {
                options.userName = config.username
                options.password = config.password.toCharArray()
            }

            suspendCancellableCoroutine { continuation ->
                try {
                    mqttClient?.connect(options, null, object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(TAG, "Connected successfully")
                            reconnectAttempts = 0
                            onConnectionStatusChanged(true)
                            continuation.resume(true)
                        }

                        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                            Log.e(TAG, "Connection failed", exception)
                            onConnectionStatusChanged(false)
                            attemptReconnect()
                            continuation.resume(false)
                        }
                    })
                } catch (e: MqttException) {
                    Log.e(TAG, "MQTT Exception during connect", e)
                    onConnectionStatusChanged(false)
                    continuation.resume(false)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection error", e)
            onConnectionStatusChanged(false)
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            mqttClient?.disconnect()
            Log.d(TAG, "Disconnected")
            onConnectionStatusChanged(false)
        } catch (e: MqttException) {
            Log.e(TAG, "Disconnect error", e)
        }
    }

    suspend fun publishMessage(topic: String, payload: String) = withContext(Dispatchers.IO) {
        try {
            if (mqttClient?.isConnected == true) {
                val message = MqttMessage(payload.toByteArray())
                message.qos = 1
                message.isRetained = false
                mqttClient?.publish(topic, message)
                Log.d(TAG, "Message published: $topic = $payload")
            } else {
                Log.w(TAG, "Client not connected")
            }
        } catch (e: MqttException) {
            Log.e(TAG, "Publish error", e)
        }
    }

    private fun attemptReconnect() {
        if (reconnectAttempts < maxReconnectAttempts && brokerConfig != null) {
            reconnectAttempts++
            Log.d(TAG, "Attempting reconnect $reconnectAttempts/$maxReconnectAttempts")
            Thread {
                Thread.sleep(reconnectDelayMs)
                // Reconnect logic will be handled by automatic reconnect
            }.start()
        }
    }

    companion object {
        private const val TAG = "MqttManager"
    }
}