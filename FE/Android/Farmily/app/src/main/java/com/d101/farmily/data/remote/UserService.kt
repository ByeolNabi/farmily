package com.d101.farmily.data.remote

import com.d101.farmily.data.remote.Response.MessageResponse
import com.d101.farmily.data.remote.Response.TokenResponse
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST

interface UserService {

    @POST("auth/signup")
    suspend fun join(@Body body : User) : Response<Map<String, Int>>

    @POST("auth/login")
    suspend fun login(@Body body: User): Response<TokenResponse>

    @POST("auth/email-req")
    suspend fun getCode(@Body body : Auth) : Response<MessageResponse>

    @POST("auth/email-verify")
    suspend fun verifyCode(@Body body: Auth): Response<Map<String, Boolean>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body body: User): Response<TokenResponse>

    @POST("auth/password-reset")
    suspend fun resetPassword(@Body body: Auth): Response<MessageResponse>

    @PATCH("users/password")
    suspend fun changePassword(
        //@Header("Authorization") token: String, // "Bearer {token}"
        @Body body: Auth
    ): Response<MessageResponse>

    @PATCH("users/withdraw")
    suspend fun withdraw(
        //@Header("Authorization") token: String, // "Bearer {token}"
        @Body body: Auth
    ): Response<MessageResponse>

}