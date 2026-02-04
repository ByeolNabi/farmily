package com.d101.farmily.ui.plantInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.data.remote.Request.PlantInfoRequest
import com.d101.farmily.data.remote.model.PlantInfo
import com.d101.farmily.data.repository.PlantRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlantInfoViewModel(
    private val plantRepository: PlantRepository = PlantRepository()
) : ViewModel() {

    private val _plantSpecies = MutableStateFlow(listOf(PlantInfo(-1,"no")))
    val plantSpecies : StateFlow<List<PlantInfo>> = _plantSpecies.asStateFlow()

    private val _enrollSuccess = MutableSharedFlow<Boolean>()
    val enrollSuccess = _enrollSuccess.asSharedFlow()

    fun getPlantSpecies() {

        viewModelScope.launch {

            plantRepository.getPlantSpecies()
                .onSuccess {

                    Log.d("PlantInfo", "getPlantSpecies: success ${it}")
                    _plantSpecies.value = it
                }
                .onFailure {

                    Log.d("PlantInfo", "getPlantSpecies: failed $it")
                }
        }
    }

    fun enrollPlant(plantInfoRequest: PlantInfoRequest) {

        viewModelScope.launch {

            plantRepository.enrollPlant(plantInfoRequest)
                .onSuccess {

                    Log.d("PlantInfo", "enrollPlant: success ${it}")
                    ApplicationClass.sharedPreferencesUtil.addPlantId(it.plantId)

                    _enrollSuccess.emit(true)
                }
                .onFailure {

                    Log.d("PlantInfo", "enrollPlant: failed ${it}")

                    _enrollSuccess.emit(false)
                }
        }
    }

}