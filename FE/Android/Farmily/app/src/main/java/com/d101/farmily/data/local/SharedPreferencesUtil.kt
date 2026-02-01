package com.d101.farmily.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPreferencesUtil (context: Context) {

    val SHARED_PREFERENCES_NAME = "farmily_preference"
    val COOKIES_KEY_NAME = "cookies"

    val ACCESS_TOKEN = "accessToken"
    val REFRESH_TOKEN = "refreshToken"
    val PLANT_NAME = "plantName"
    val PLANT_TYPE = "plantType"

    val USER_EMAIL = "userEmail"

//    var preferences : SharedPreferences =
//        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // 2. 기존 preferences 선언을 Encrypted 버전으로 교체
    var preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        SHARED_PREFERENCES_NAME, // 기존에 쓰시던 파일 이름 그대로 사용 가능
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getAccessToken() : String? {

        return preferences.getString(ACCESS_TOKEN, "")
    }

    fun addAccessToken(token : String) {

        preferences.edit {
            putString(ACCESS_TOKEN, token)
        }
    }

    fun deleteAccessToken() {

        preferences.edit().remove(ACCESS_TOKEN).apply()
    }

    fun getRefreshToken() : String? {

        return preferences.getString(REFRESH_TOKEN, "")
    }

    fun addRefreshToken(token : String) {

        preferences.edit {
            putString(REFRESH_TOKEN, token)
        }
    }

    fun deleteRefreshToken() {

        preferences.edit().remove(REFRESH_TOKEN).apply()
    }

    fun addUserCookie(cookies: HashSet<String>) {
        val editor = preferences.edit()
        editor.putStringSet(COOKIES_KEY_NAME, cookies)
        editor.apply()
    }

    fun getUserCookie(): MutableSet<String>? {
        return preferences.getStringSet(COOKIES_KEY_NAME, HashSet())
    }

    fun deleteUserCookie() {
        preferences.edit().remove(COOKIES_KEY_NAME).apply()
    }

    /*****
     * User
     */
    fun userExist() : Boolean {
        val id = preferences.getString("id", "")
        return (id != "")
    }

    fun plantExist() : Boolean {
        val plantName = preferences.getString(PLANT_NAME, "")

        return (plantName != "")
    }

    fun addPlantName(name : String) {

        preferences.edit {
            putString(PLANT_NAME, name)
        }
    }

    fun getPlantName() : String? {

        return preferences.getString(PLANT_NAME, "")
    }

    fun deletePlantName() {
        preferences.edit { remove(PLANT_NAME) }
    }

    fun addPlantType(type : String) {

        preferences.edit {
            putString(PLANT_TYPE, type)
        }
    }

    fun getPlantType() : String? {

        return preferences.getString(PLANT_TYPE, "")
    }

    fun deletePlantType() {
        preferences.edit { remove(PLANT_TYPE) }
    }

    fun addUserEmail(type : String) {

        preferences.edit {
            putString(USER_EMAIL, type)
        }
    }

    fun getUserEmail() : String? {

        return preferences.getString(USER_EMAIL, "")
    }

    fun deleteUserEmail() {
        preferences.edit { remove(USER_EMAIL) }
    }

    //add

    //delete

    //get

    ////////////////
}