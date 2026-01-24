plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    kotlin("plugin.serialization") version "2.0.21"
}

android {
    namespace = "com.d101.farmily"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.d101.farmily"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            // 중복되는 INDEX.LIST 파일을 빌드에서 제외하거나 하나만 선택합니다.
            excludes += "/META-INF/INDEX.LIST"

            // 만약 나중에 다른 Netty 관련 파일도 에러가 난다면 아래 주석을 참고하세요.
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.navigation:navigation-compose:2.9.0")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    // https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    // https://github.com/square/okhttp/tree/master/okhttp-logging-interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // 자동 암호화 해주는 SH PF
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // HiveMQ MQTT Client (안드로이드 최적화 버전)
    implementation("com.hivemq:hivemq-mqtt-client:1.3.0")
    implementation("io.netty:netty-codec-http:4.1.48.Final")
    implementation("io.netty:netty-handler:4.1.48.Final")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}