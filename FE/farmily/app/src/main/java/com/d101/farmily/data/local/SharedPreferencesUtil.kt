package com.d101.farmily.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SharedPreferencesUtil (context: Context) {

    val SHARED_PREFERENCES_NAME = "farmily_preference"
    val COOKIES_KEY_NAME = "cookies"

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

    //add

    //delete

    //get

    ////////////////
}