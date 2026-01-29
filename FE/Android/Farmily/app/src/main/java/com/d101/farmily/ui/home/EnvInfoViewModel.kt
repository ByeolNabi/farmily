package com.d101.farmily.ui.home

import androidx.lifecycle.ViewModel
import com.d101.farmily.data.model.EnvInfo
import com.d101.farmily.data.remote.MqttUtil.Companion.client
import com.d101.farmily.data.remote.Response.MqttResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class EnvInfoViewModel : ViewModel() {

    private val _envInfoList = MutableStateFlow<List<EnvInfo>>(emptyList())
    val envInfoList = _envInfoList.asStateFlow()

    fun startMqtt() {

        client.connect().whenComplete { connAck, throwable ->
            if (throwable != null) {

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

                try {
                    val data: MqttResponse = jsonParser.decodeFromString(payload)


                    _envInfoList.value = data.payload.toEnvInfoList()

                } catch (e: Exception) {
                }


            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {


                } else {

                }
            }
    }
}