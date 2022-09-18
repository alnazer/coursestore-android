package com.online.coursestore.presenterImpl

import com.google.gson.Gson
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.VerifyAccountFrag
import retrofit2.Call
import retrofit2.Response

class VerifyAccountPresenterImpl(private val frag: VerifyAccountFrag) :
    Presenter.VerifyAccountPresenter {

    override fun verifyAccount(accountVerification: AccountVerification) {
        val verifyAccountRequest = ApiService.apiClient!!.verifyAccount(accountVerification)
        frag.addNetworkRequest(verifyAccountRequest)
        ApiService.apiClient!!.verifyAccount(accountVerification).enqueue(object :
            CustomCallback<Data<User>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    verifyAccount(accountVerification)
                }
            }

            override fun onResponse(call: Call<Data<User>>, response: Response<Data<User>>) {
                if (response.body() != null) {
                    frag.onVerificationReceived(response.body()!!)

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }

            override fun onFailure(call: Call<Data<User>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }

    override fun signUp(signUp: EmailSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(emailSignUp = signUp))
    }

    override fun signUp(signUp: MobileSignUp) {
        val signUpRequest = ApiService.apiClient!!.signUpMethod(signUp)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(getCallback(mobileSignUp = signUp))
    }

    private fun getCallback(emailSignUp: EmailSignUp? = null, mobileSignUp: MobileSignUp? = null): CustomCallback<Data<User>>{
        return object : CustomCallback<Data<User>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    if (emailSignUp != null) {
                        signUp(emailSignUp)
                    } else {
                        signUp(mobileSignUp!!)
                    }
                }
            }

            override fun onResponse(call: Call<Data<User>>, response: Response<Data<User>>) {
                if (response.body() != null) {
                    frag.onCodeSent(response.body()!!)

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }
        }
    }

}