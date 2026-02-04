package com.d101.farmily.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import com.d101.farmily.base.ApplicationClass.Companion.mqttClient
import com.d101.farmily.data.remote.MqttUtil.Companion.client
import com.d101.farmily.data.remote.Response.MqttResponse
import com.d101.farmily.data.remote.model.EnvInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class EnvInfoViewModel : ViewModel() {

    private val _envInfoList = MutableStateFlow<List<EnvInfo>>(emptyList())
    val envInfoList = _envInfoList.asStateFlow()

    fun startMqtt() {

//        try {
//            if (mqttClient.state.isConnectedOrReconnect) {
//                Log.d("MQTT", "기존 연결 발견! 재연결을 위해 끊습니다.")
//                mqttClient.disconnect()
//            }
//        } catch (e: Exception) {
//            Log.e("MQTT", "연결 확인 중 에러: ${e.message}")
//        }

        client.connect().whenComplete { connAck, throwable ->
            if (throwable != null) {

                Log.d("MQTT", "startMqtt: failed to connect $throwable")
            } else {

                subscribeToTopic()
            }
        }
    }

    private fun subscribeToTopic() {
        val jsonParser = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        client.subscribeWith()
            .topicFilter("farmily/raspi/sensor/all")
            .callback { publish ->
                val payload = String(publish.payloadAsBytes)
                Log.d("MQTT", "Raw Payload: $payload")
                try {
                    val data: MqttResponse = jsonParser.decodeFromString(payload)

                    Log.d("MQTT", "subscribeToTopic: ${data.payload}")
                    _envInfoList.value = data.payload.toEnvInfoList()

                } catch (e: Exception) {

                    Log.d("MQTT", "subscribeToTopic: ${e}")
                }


            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {

                    Log.d("MQTT", "subscribeToTopic: failed ${throwable} + ${subAck}")
                } else {
                    Log.d("MQTT", "subscribeToTopic: 구독 성공했습니다.  ${subAck}")
                }
            }
    }


    override fun onCleared() {
        super.onCleared()
        Log.d("EnvInfoViewModel", "ViewModel Cleared! MQTT 연결을 해제합니다.")

        // MQTT 연결 해제 로직 호출
        stopMqtt()
    }

    private fun stopMqtt() {
        try {
            // mqttClient가 null이 아니고 연결되어 있다면 disconnect
            if (mqttClient != null && mqttClient.state.isConnected) {
                mqttClient.toBlocking().disconnect()
                Log.d("EnvInfoViewModel", "MQTT Disconnected Successfully")
            }
        } catch (e: Exception) {
            Log.e("EnvInfoViewModel", "MQTT 해제 중 에러 발생: ${e.message}")
        }
    }
}