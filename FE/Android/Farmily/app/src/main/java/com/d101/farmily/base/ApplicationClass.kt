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

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            // 로그캣에 okhttp.OkHttpClient로 검색하면 http 통신 내용을 보여줍니다.
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
            .sslConfig().applySslConfig()           // Secure 적용
            .webSocketConfig()
            .serverPath(MQTT_SERVER_PATH)
            .applyWebSocketConfig()
            .buildAsync()

    }

    val gson : Gson = GsonBuilder()
        .setLenient()
        .create()

}