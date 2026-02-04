package com.d101.farmily.data.remote

import com.d101.farmily.data.remote.Request.PlantInfoRequest
import com.d101.farmily.data.remote.Response.PlantIdResponse
import com.d101.farmily.data.remote.model.Achievement
import com.d101.farmily.data.remote.model.Affection
import com.d101.farmily.data.remote.model.PlantInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PlantService {

    @GET("plants/{plantId}/points")
    suspend fun getAffectionPoint(@Path("plantId") plantId : Int) : Response<Affection>

    @GET("plants/{plantId}/achievements")
    suspend fun getAchievements(@Path("plantId") plantId : Int) : Response<List<Achievement?>>

    @GET("plant/species")
    suspend fun getPlantSpecies() : Response<List<PlantInfo>>

    @POST("plant")
    suspend fun enrollPlant(@Body body : PlantInfoRequest) : Response<PlantIdResponse>

}