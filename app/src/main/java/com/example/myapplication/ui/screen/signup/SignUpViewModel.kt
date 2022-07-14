package com.example.myapplication.ui.screen.signup

import android.content.res.Resources
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.model.UserInfoSignIn
import com.example.myapplication.data.model.UserInfoSignUp
import com.example.myapplication.data.repository.SignUpRepository
import com.example.myapplication.ui.base.BaseViewModel
import com.example.myapplication.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository,
    private val appPrefs: AppPrefs,
    private val resources: Resources
) : BaseViewModel() {
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val rePassword = MutableLiveData<String>()
    val errorsLiveData = MutableLiveData<String>()
    val isLogin = MutableLiveData<Boolean>()

    fun signUp(userInfoSignUp: UserInfoSignUp) {
        viewModelScope.launch {
            signUpRepository.signUp(userInfoSignUp)
                .handleResponse(
                    onSuccess = {
                        appPrefs.storeEmail(userInfoSignUp.email)
                        appPrefs.storeAccessToken(it.data!!.accessToken.toString())
                        appPrefs.storeRefreshToken(it.data.refreshToken.toString())
                        isLogin.value = true
                    },
                    onError = {
                        if (it != null) {
                            isLogin.value = false
                            errorsLiveData.postValue(it.errors?.fullMessage)
                        }
                    }
                )
        }
    }

    fun validateInput(){
        val e = email.value.toString()
        val p = password.value.toString()
        val rp = rePassword.value.toString()

        if (TextUtils.isEmpty(e)){
            errorsLiveData.value = resources.getString(R.string.string_empty_email)
            return
        }
        if (TextUtils.isEmpty(p)){
            errorsLiveData.value = resources.getString(R.string.string_empty_password)
            return
        }
        if (TextUtils.isEmpty(rp)){
            errorsLiveData.value = resources.getString(R.string.string_empty_repassword)
            return
        }

        if (validateEmail(e) &&
            validatePassword(p) &&
            validateRePassword(rp, p)
        ){
            val userInfoSignUp = UserInfoSignUp(e, p, rp)
            signUp(userInfoSignUp)
        }else{
            if (!validateEmail(e)) {
                errorsLiveData.value = resources.getString(R.string.string_invalid_email)
                return
            }
            if (validatePasswordShort(p)){
                errorsLiveData.value = resources.getString(R.string.string_password_short)
                return
            }
            if (validatePasswordLong(p)){
                errorsLiveData.value = resources.getString(R.string.string_password_long)
                return
            }
            if (!validateRePassword(rp, p)){
                errorsLiveData.value = resources.getString(R.string.string_not_match_password)
                return
            }
        }
    }

}