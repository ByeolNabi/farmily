package com.d101.farmily.data.model

import androidx.compose.ui.graphics.Color
import com.d101.farmily.R

data class StatInfo(
    val type: String,
    val value: String,
) {
    val unit: String
        get() = when (type) {
            "stroking" -> "회"
            "photo" -> "회"
            "watering" -> "회"
            "chatting" -> "일"
            else -> "회"
        }

    val color: Color
        get() = when (type) {
            "stroking" -> Color(0xFF22C55E)
            "photo"-> Color(0xFFA855F7)
            "watering"-> Color(0xFF3B82F6)
            "chatting"-> Color(0xFFEC4899)
            else -> Color(0xFFF97316)
        }

    val backColor: Color
        get() = when (type) {
            "stroking" -> Color(0xFFF0FDF4)
            "photo" -> Color(0xFFFAF5FF)
            "watering" -> Color(0xFFEFF6FF)
            "chatting" -> Color(0xFFFDF2F8)
            else -> Color(0xFFFFF7ED)
        }


    val iconId: Int
        get() = when (type) {
            "stroking" -> R.drawable.stroking
            "photo" -> R.drawable.photo
            "watering" -> R.drawable.watering
            "chatting" -> R.drawable.chatting
            else -> R.drawable.date
        }
}

