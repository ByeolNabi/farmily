package com.d101.farmily.data.repository

import com.d101.farmily.data.remote.Response.MessageResponse
import com.d101.farmily.data.remote.RetrofitUtil
import com.d101.farmily.data.remote.model.Auth

class UserInfoRepository {

    suspend fun withdraw(auth : Auth) : Result<MessageResponse> = runCatching {

        val response = RetrofitUtil.userService.withdraw(auth)
        if (response.isSuccessful) response.body() ?: MessageResponse("빈 메세지")
        else throw Exception("코드 요청 실패: ${response.code()}")
    }
}