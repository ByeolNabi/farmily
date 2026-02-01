package com.d101.farmily.ui.userInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.repository.UserInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(
    private val userInfoRepository : UserInfoRepository = UserInfoRepository()
) : ViewModel() {

    private val _withdrawal = MutableStateFlow(false)
    var withdrawal : StateFlow<Boolean> = _withdrawal.asStateFlow()

    fun withdraw(auth: Auth) {

        viewModelScope.launch {

            userInfoRepository.withdraw(auth)
                .onSuccess {

                    _withdrawal.value = true
                }
                .onFailure {

                    Log.d("User", "withdraw: failed")
                }
        }
    }

}