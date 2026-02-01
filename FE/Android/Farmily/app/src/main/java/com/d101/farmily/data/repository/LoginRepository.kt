package com.d101.farmily.data.repository

import com.d101.farmily.data.remote.Response.MessageResponse
import com.d101.farmily.data.remote.Response.TokenResponse
import com.d101.farmily.data.remote.RetrofitUtil
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User

class LoginRepository {

    suspend fun join(user: User): Result<Map<String, Int>> = runCatching {
        val response = RetrofitUtil.userService.join(user)
        if (response.isSuccessful) response.body()!!
        else throw Exception("회원가입 실패: ${response.code()}")
    }

    // 2. 로그인
    suspend fun login(user: User): Result<TokenResponse> = runCatching {
        val response = RetrofitUtil.userService.login(user)
        if (response.isSuccessful) response.body()!!
        else throw Exception("로그인 실패: ${response.code()}")
    }

    // 3. 이메일 인증 코드 요청
    suspend fun getCode(auth: Auth): Result<MessageResponse> = runCatching {
        val response = RetrofitUtil.userService.getCode(auth)
        if (response.isSuccessful) response.body() ?: MessageResponse("빈 메세지")
        else throw Exception("코드 요청 실패: ${response.code()}")
    }

    // 4. 이메일 인증 확인
    suspend fun verifyCode(auth: Auth): Result<Map<String, Boolean>> = runCatching {
        val response = RetrofitUtil.userService.verifyCode(auth)
        if (response.isSuccessful) response.body() !!
        else throw Exception("인증 확인 실패: ${response.code()}")
    }

    // 5. 토큰 갱신
    suspend fun refreshToken(user: User): Result<TokenResponse> = runCatching {
        val response = RetrofitUtil.userService.refreshToken(user)
        if (response.isSuccessful) response.body()!!
        else throw Exception("토큰 갱신 실패: ${response.code()}")
    }

    // 6. 비밀번호 재설정 (로그인 전)
    suspend fun resetPassword(auth: Auth): Result<MessageResponse> = runCatching {
        val response = RetrofitUtil.userService.resetPassword(auth)
        if (response.isSuccessful) response.body() ?: MessageResponse("빈 메세지")
        else throw Exception("비밀번호 재설정 실패: ${response.code()}")
    }

    // 7. 비밀번호 변경 (로그인 후)
    suspend fun changePassword(auth: Auth): Result<MessageResponse> = runCatching {
        val response = RetrofitUtil.userService.changePassword(auth)
        if (response.isSuccessful) response.body() ?: MessageResponse("빈 메세지")
        else throw Exception("비밀번호 변경 실패: ${response.code()}")
    }

    // 8. 회원 탈퇴
    suspend fun withdraw(auth: Auth): Result<MessageResponse> = runCatching {
        val response = RetrofitUtil.userService.withdraw(auth)
        if (response.isSuccessful) response.body() ?: MessageResponse("빈 메세지")
        else throw Exception("회원 탈퇴 실패: ${response.code()}")
    }
}