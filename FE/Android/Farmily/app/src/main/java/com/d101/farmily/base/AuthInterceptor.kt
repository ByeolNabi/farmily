package com.d101.farmily.base

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class AuthInterceptor: Interceptor  {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        // 1. SharedPreferences에서 JWT 토큰 하나만 가져오기
        val token = ApplicationClass.sharedPreferencesUtil.getAccessToken()

        // 2. 토큰이 존재할 때만 Authorization 헤더 추가
        if (!token.isNullOrEmpty()) {
            builder.addHeader("Authorization", "Bearer $token")
            //Log.d(TAG, "JWT 토큰 주입 완료")
        }

        val response = chain.proceed(builder.build())

        //return chain.proceed(builder.build())

        if (response.code == 401) {
            ApplicationClass.handleLogout()
        }

        return response
    }

}