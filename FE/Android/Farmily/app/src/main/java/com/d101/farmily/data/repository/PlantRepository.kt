package com.d101.farmily.data.repository

import com.d101.farmily.data.remote.Request.PlantInfoRequest
import com.d101.farmily.data.remote.Response.PlantIdResponse
import com.d101.farmily.data.remote.RetrofitUtil
import com.d101.farmily.data.remote.model.Achievement
import com.d101.farmily.data.remote.model.Affection
import com.d101.farmily.data.remote.model.PlantInfo

class PlantRepository {

    suspend fun getAffection(plantId : Int) : Result<Affection> = runCatching {

        val response = RetrofitUtil.plantService.getAffectionPoint(plantId)
        if (response.isSuccessful) response.body() ?: Affection(-1f)
        else throw Exception("코드 요청 실패: ${response.code()}")
    }

    suspend fun getAchievements(plantId : Int) : Result<List<Achievement?>> = runCatching {

        val response = RetrofitUtil.plantService.getAchievements(plantId)
        if (response.isSuccessful) response.body()!!
        else throw Exception("코드 요청 실패: ${response.code()}")
    }

    suspend fun getPlantSpecies() : Result<List<PlantInfo>> = runCatching {

        val response = RetrofitUtil.plantService.getPlantSpecies()
        if(response.isSuccessful) response.body()!!
        else throw Exception("코드 요청 실패 ${response.code()}")
    }

    suspend fun enrollPlant(plantInfoRequest: PlantInfoRequest) : Result<PlantIdResponse> = runCatching {

        val response = RetrofitUtil.plantService.enrollPlant(plantInfoRequest)
        if(response.isSuccessful) response.body()!!
        else throw Exception("코드 요청 실패 ${response.code()}")
    }

}