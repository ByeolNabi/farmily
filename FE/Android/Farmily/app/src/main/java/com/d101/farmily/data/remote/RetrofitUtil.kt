package com.d101.farmily.data.remote

import com.d101.farmily.base.ApplicationClass

class RetrofitUtil {

    companion object {

        val userService = ApplicationClass.retrofit.create(UserService::class.java)
        val plantService = ApplicationClass.retrofit.create(PlantService::class.java)
    }
}