package com.d101.farmily.data.model

import androidx.compose.ui.graphics.Color
import com.d101.farmily.R

data class EnvInfo(
    val type : String,
    val value : Double,
    val state : String,
) {

    val unit: String
        get() = when (type) {
            "온도" -> "°C"
            "습도" -> "%"
            "조도" -> "lx"
            "토양 수분" -> "%"
            else -> "일"
        }

    val iconId: Int
        get() = when (type) {
            "온도" -> R.drawable.temperature
            "습도" -> R.drawable.humidity
            "조도" -> R.drawable.lux
            "토양 수분" -> R.drawable.water
            else -> R.drawable.date
        }

    val color: Color
        get() = when (type) {
            "온도" -> Color(0xFFF52E2E)
            "습도"-> Color(0xFF3B82F6)
            "조도"-> Color(0xFFF1B13E)
            "토양 수분"-> Color(0xFF48ECC6)
            else -> Color(0xFFF97316)
        }

    val backColor: Color
        get() = when (type) {
            "온도" -> Color(0xFFFDF0F0)
            "습도" -> Color(0xFFFAF5FF)
            "조도" -> Color(0xFFFFF9EF)
            "토양 수분" -> Color(0xFFF2FDFA)
            else -> Color(0xFFFFF7ED)
        }

}
