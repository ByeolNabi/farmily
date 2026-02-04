package com.d101.farmily.data.remote

import com.d101.farmily.data.remote.model.Achievement
import com.d101.farmily.data.remote.model.Affection
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PlantService {

    @GET("plants/{plantId}/points")
    suspend fun getAffectionPoint(@Path("plantId") plantId : Int) : Response<Affection>

    @GET("plants/{plantId}/achievements")
    suspend fun getAchievements(@Path("plantId") plantId : Int) : Response<List<Achievement?>>

}