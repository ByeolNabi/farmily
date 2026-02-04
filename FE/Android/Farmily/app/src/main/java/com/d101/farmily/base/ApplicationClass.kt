package com.d101.farmily.base

import android.app.Application
import android.content.Intent
import android.util.Log
import com.d101.farmily.LoginActivity
import com.d101.farmily.data.local.SharedPreferencesUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {

    companion object{
        lateinit var instance: ApplicationClass
        const val SERVER_URL ="https://i14d101.p.ssafy.io/api/"
        const val MQTT_SERVER_URL = "i14d101.p.ssafy.io"
        const val MQTT_SERVER_PORT = 443
        const val MQTT_SERVER_PATH = "mqtt"
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
        lateinit var mqttClient : Mqtt5AsyncClient

        fun handleLogout() {
            // 토큰 등 저장된 정보 싹 비우기
            ApplicationClass.sharedPreferencesUtil.deleteAccessToken()

            // 여기에 로그인 화면으로 이동하는 로직을 넣으세요.
            val intent = Intent(instance, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            instance.startActivity(intent)
            Log.e("Auth", "401 에러 감지: 세션 만료로 인한 로그아웃")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d("reart", "onCreate: application class created")
        sharedPreferencesUtil = SharedPreferencesUtil(context = applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)

            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            //.addInterceptor(AddCookiesInterceptor())
            //.addInterceptor(ReceivedCookiesInterceptor())
            .addInterceptor(AuthInterceptor())
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()


        mqttClient = Mqtt5Client.builder()
            .identifier("android-client-farmily")
            .serverHost(MQTT_SERVER_URL)
            .serverPort(MQTT_SERVER_PORT)
            .sslConfig().applySslConfig()
            .webSocketConfig()
            .serverPath(MQTT_SERVER_PATH)
            .applyWebSocketConfig()
            .buildAsync()

    }

    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

}