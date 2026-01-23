package com.d101.farmily.data.model

import androidx.compose.ui.graphics.Color
import com.d101.farmily.R

data class StatInfo(
    val type: String,
    val value: String,
) {
    val unit: String
        get() = when (type) {
            "쓰다듬기" -> "회"
            "사진찍기" -> "회"
            "물 주기" -> "회"
            "대화하기" -> "일"
            else -> "일"
        }

    val color: Color
        get() = when (type) {
            "쓰다듬기" -> Color(0xFF22C55E)
            "사진찍기"-> Color(0xFFA855F7)
            "물 주기"-> Color(0xFF3B82F6)
            "대화하기"-> Color(0xFFEC4899)
            else -> Color(0xFFF97316)
        }

    val backColor: Color
        get() = when (type) {
            "쓰다듬기" -> Color(0xFFF0FDF4)
            "사진찍기" -> Color(0xFFFAF5FF)
            "물 주기" -> Color(0xFFEFF6FF)
            "대화하기" -> Color(0xFFFDF2F8)
            else -> Color(0xFFFFF7ED)
        }


    val iconId: Int
        get() = when (type) {
            "쓰다듬기" -> R.drawable.stroking
            "사진찍기" -> R.drawable.photo
            "물 주기" -> R.drawable.watering
            "대화하기" -> R.drawable.chatting
            else -> R.drawable.date
        }
}

