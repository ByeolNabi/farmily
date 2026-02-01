package com.d101.farmily.data.remote.model

data class Auth(
    val email : String = "",
    val code : String ="",
    val newPassword : String ="",
)
