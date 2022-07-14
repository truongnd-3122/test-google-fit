package com.example.myapplication.ui.screen.signin

import android.content.res.Resources
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.data.local.pref.AppPrefs
import com.example.myapplication.data.model.UserInfoSignIn
import com.example.myapplication.data.repository.SignInRepository
import com.example.myapplication.ui.base.BaseViewModel
import com.example.myapplication.utils.validateEmail
import com.example.myapplication.utils.validatePassword
import com.example.myapplication.utils.validatePasswordLong
import com.example.myapplication.utils.validatePasswordShort
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val appPrefs: AppPrefs,
    private val resources: Resources
) : BaseViewModel() {

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val errorsLiveData = MutableLiveData<String>()
    val isLogin = MutableLiveData<Boolean>()
    val isTokenUser = MutableLiveData<Boolean>(false)

    fun isTokenExists() {
        isTokenUser.value = !TextUtils.isEmpty(appPrefs.getAccessToken().toString())
        Log.d("zzz", appPrefs.getAccessToken().toString())
    }

    fun signIn(userInfoSignIn: UserInfoSignIn) {
        viewModelScope.launch {
            signInRepository.signIn(userInfoSignIn).handleResponse(onSuccess = {
                appPrefs.storeEmail(userInfoSignIn.email)
                appPrefs.storeAccessToken(it.data!!.accessToken.toString())
                appPrefs.storeRefreshToken(it.data.refreshToken.toString())
                isLogin.value = true
            }, onError = {
                if (it != null) {
                    isLogin.value = false
                    errorsLiveData.postValue(it.errors?.fullMessage)
                }
            })
        }
    }

    fun validateInput(){
        val e = email.value.toString()
        val p = password.value.toString()

        if (TextUtils.isEmpty(email.value)){
            errorsLiveData.value = resources.getString(R.string.string_empty_email)
            return
        }
        if (TextUtils.isEmpty(password.value)){
            errorsLiveData.value = resources.getString(R.string.string_empty_password)
            return
        }

        if (validateEmail(e) && validatePassword(p)){
            val userInfoSignIn = UserInfoSignIn(e, p)
            signIn(userInfoSignIn)
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
        }

    }
}