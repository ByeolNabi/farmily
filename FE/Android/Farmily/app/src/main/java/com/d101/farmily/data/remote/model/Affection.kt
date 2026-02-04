package com.d101.farmily.data.remote.model

import com.google.gson.annotations.SerializedName

data class Affection(
    @SerializedName("loveTemperature")
    val affection : Float
)
