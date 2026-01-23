package com.d101.farmily.base

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val TAG = "ReceivedCooki_싸피"

class ReceivedCookiesInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain):Response{
        val originalResponse: Response = chain.proceed(chain.request())

        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {

            val cookies = HashSet<String>()
            for (header in originalResponse.headers("Set-Cookie")) {
                cookies.add(header)
            }
            
            // cookie 내부 데이터에 저장
            ApplicationClass.sharedPreferencesUtil.addUserCookie(cookies)
        }
        return originalResponse
    }
}
