package com.d101.farmily.data.remote.model

data class Achievement(
    val id : Int,
    val name : String,
    val description : String,
    val iconUrl : String,
    val actionType : String,
    val requiredCount : Int,
    val createdAt : String
) {

}
