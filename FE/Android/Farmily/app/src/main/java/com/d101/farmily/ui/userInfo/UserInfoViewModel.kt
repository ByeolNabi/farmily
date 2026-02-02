package com.d101.farmily.ui.userInfo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d101.farmily.base.ApplicationClass
import com.d101.farmily.data.remote.model.Auth
import com.d101.farmily.data.remote.model.User
import com.d101.farmily.data.repository.LoginRepository
import com.d101.farmily.data.repository.UserInfoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel(
    private val userInfoRepository : UserInfoRepository = UserInfoRepository(),
    private val loginRepository: LoginRepository = LoginRepository(),
) : ViewModel() {

    val userEmail = ApplicationClass.sharedPreferencesUtil.getUserEmail()!!
    private val _withdrawal = MutableStateFlow(false)
    var withdrawal : StateFlow<Boolean> = _withdrawal.asStateFlow()

    private val _currentPasswordInvalid = MutableStateFlow(false)
    val currentPasswordInvalid : StateFlow<Boolean> = _currentPasswordInvalid.asStateFlow()

    private val _showPasswordChangeDialog = MutableStateFlow(false)
    val showPasswordChangeDialog : StateFlow<Boolean> = _showPasswordChangeDialog.asStateFlow()

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

    fun openChangePasswordDialog() {

        _showPasswordChangeDialog.value = true
    }
    fun closeChangePasswordDialog() {

        _showPasswordChangeDialog.value = false
    }

    fun changePassword(cur : String, new : String) {

        viewModelScope.launch {

            loginRepository.login(User(userEmail, "", cur))
                .onSuccess {

                    Log.d("CHPW", "changePassword: 비밀번호 일치로 인증 성공")
                    _currentPasswordInvalid.value = false
                    userInfoRepository.changePassword(Auth("","", new))
                        .onSuccess {

                            closeChangePasswordDialog()

                            Log.d("CHPW", "changePassword: success")
                        }
                        .onFailure {

                            Log.d("CHPW", "changePassword: failed...")
                        }
                }
                .onFailure {

                    _currentPasswordInvalid.value = true
                    Log.d("CHPW", "changePassword: 비밀번호 불일치로 인증 실패")
                }
        }
    }

}