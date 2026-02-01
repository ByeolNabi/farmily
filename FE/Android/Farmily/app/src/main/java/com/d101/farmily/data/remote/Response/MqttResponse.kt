package com.d101.farmily.data.remote.Response

import com.d101.farmily.data.remote.model.EnvInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MqttResponse(
    val header: Header,
    val payload: PlantPayload
)

@Serializable
data class Header(
    @SerialName("msg_id") val msgId: String,
    val type: String,
    @SerialName("device_id") val deviceId: String,
    val timestamp: String
)

@Serializable
data class PlantPayload(
    val temperature: Double,
    val humidity: Double,
    val illuminance: Double,
    @SerialName("soil_moisture") val soilMoisture: Double
) {

    fun toEnvInfoList(): List<EnvInfo> {
        return listOf(
            EnvInfo(type = "온도", value = temperature, state = "적정"),
            EnvInfo(type = "습도", value = humidity, state = "적정"),
            EnvInfo(type = "조도", value = illuminance, state = "적정"),
            EnvInfo(type = "토양 수분", value = soilMoisture, state = "적정")
        )
    }
}