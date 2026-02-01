package com.d101.farmily.data.remote.model

data class User(
    val email : String,
    val name :String,
    val password : String = "",
    val accessToken : String = "",
    val refreshToken : String = ""
) {


}
