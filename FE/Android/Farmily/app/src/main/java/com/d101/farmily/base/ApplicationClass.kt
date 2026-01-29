package com.d101.farmily.base

import android.app.Application
import android.util.Log
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

        const val SERVER_URL ="https://0.0.0.1/"
        const val MQTT_SERVER_URL = "i14d101.p.ssafy.io"
        const val MQTT_SERVER_PORT = 443
        const val MQTT_SERVER_PATH = "mqtt"
        lateinit var sharedPreferencesUtil: SharedPreferencesUtil
        lateinit var retrofit: Retrofit
        lateinit var mqttClient : Mqtt5AsyncClient
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("reart", "onCreate: application class created")
        sharedPreferencesUtil = SharedPreferencesUtil(context = applicationContext)

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)

            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor(AddCookiesInterceptor())
            .addInterceptor(ReceivedCookiesInterceptor()).build()

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