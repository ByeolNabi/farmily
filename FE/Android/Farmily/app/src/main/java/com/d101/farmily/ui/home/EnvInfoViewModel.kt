package com.d101.farmily.ui.home

import android.util.Log
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
        //Log.d("MQTT", "startMqtt: 실행")

        client.connect().whenComplete { connAck, throwable ->
            if (throwable != null) {
                //Log.d("MQTT", "연결 실패:")
                //Log.e("MQTT", "연결 실패: ${throwable.message}")
            } else {
                //Log.d("MQTT", "연결 성공! 상태: ${connAck.reasonCode}")

                // 연결 성공 직후 토픽 구독 시작
                subscribeToTopic()
            }
        }
    }

    private fun subscribeToTopic() {
        val jsonParser = Json {
            ignoreUnknownKeys = true // 서버에서 추가 데이터가 와도 에러 안 나게 방어
            coerceInputValues = true // null 방지
        }

        client.subscribeWith()
            .topicFilter("farmily/raspi/sensor/all") //
            .callback { publish ->
                val payload = String(publish.payloadAsBytes)
                //Log.d("MQTT", "데이터 수신: $payload")

                try {
                    // 👈 여기서 Retrofit처럼 자동 매핑이 일어납니다!
                    val data: MqttResponse = jsonParser.decodeFromString(payload)

                    // 이제 data.payload.temperature 처럼 바로 꺼내 쓰면 끝
                    _envInfoList.value = data.payload.toEnvInfoList()
                    //Log.d("MQTT", "${_envInfoList.value}")

                } catch (e: Exception) {
                    //Log.e("MQTT", "파싱 에러: ${e.message}")
                }


            }
            .send()
            .whenComplete { subAck, throwable ->
                if (throwable != null) {
                    Log.e("MQTT", "구독 실패: ${throwable.message}")
                } else {
                    Log.d("MQTT", "구독 시작됨: ${subAck.reasonCodes}")
                }
            }
    }
}