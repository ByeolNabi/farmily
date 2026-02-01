package com.d101.farmily.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User
import com.d101.farmily.data.repository.LoginRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginRepository: LoginRepository = LoginRepository()
) : ViewModel() {

    private val _verifyDialog = MutableStateFlow(false)
    val verifyDialog : StateFlow<Boolean> = _verifyDialog.asStateFlow()

    fun closeDialog() {_verifyDialog.value = false}
    private val _wrongCode = MutableStateFlow(false)
    val wrongCode : StateFlow<Boolean> = _wrongCode.asStateFlow()

    private val _joinSuccess = MutableStateFlow(false)
    val joinSuccess : StateFlow<Boolean> = _joinSuccess.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess : StateFlow<Boolean> = _loginSuccess

    private val _canJoin = MutableStateFlow(0)
    val canJoin : StateFlow<Int> = _canJoin.asStateFlow()

    fun getEmailVer(auth : Auth) {

        _verifyDialog.value = true

        viewModelScope.launch {

            loginRepository.getCode(auth)
                .onSuccess {
                    if(it.message == "발송 완료") _verifyDialog.value = true;
                    else _verifyDialog.value = false
                }
                .onFailure {
                    Log.d("Login", "발송 오류 ${it}")
                    _verifyDialog.value = false
                }
        }

    }

    fun sendEmailVer(auth : Auth, user: User) {

        viewModelScope.launch {

            loginRepository.verifyCode(auth)
                .onSuccess {
                    if(it["isVerified"]!!) {
                        _wrongCode.value = false;
                        _verifyDialog.value = false;

                        join(user)
                    } else {
                        _wrongCode.value = true;
                    }
                }
                .onFailure {

                }
        }
    }


    fun join(user : User) {

        viewModelScope.launch {

            loginRepository.join(user)
                .onSuccess {
                    _joinSuccess.value = true
                    _canJoin.value = 1
                    Log.d("Login", "join: success !!")
                }
                .onFailure {
                    Log.d("Login", "join: fail $it ")
                }
        }
    }

    fun login(user : User) {

        viewModelScope.launch {

            loginRepository.login(user)
                .onSuccess {

                    ApplicationClass.sharedPreferencesUtil.addAccessToken(it.accessToken)
                    ApplicationClass.sharedPreferencesUtil.addRefreshToken(it.refreshToken)
                    ApplicationClass.sharedPreferencesUtil.addUserEmail(user.email)
                    _loginSuccess.value = true
                }
                .onFailure {
                    Log.d("Login", "login: fail $it ")
                }
        }
    }

}