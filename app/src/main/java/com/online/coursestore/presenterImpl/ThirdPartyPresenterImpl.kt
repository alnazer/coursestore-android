package com.online.coursestore.presenterImpl

import com.google.gson.Gson
import com.online.coursestore.manager.App
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Data
import com.online.coursestore.model.Response
import com.online.coursestore.model.ThirdPartyLogin
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.abstract.UserAuthFrag
import retrofit2.Call

open class ThirdPartyPresenterImpl(
    private val frag: UserAuthFrag
) : Presenter.ThirdPartyPresenter {

    override fun facebookSignInUp(thirdPartyLogin: ThirdPartyLogin) {
        val signUpRequest = ApiService.apiClient!!.registerWithFacebook(thirdPartyLogin)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(object : CustomCallback<Data<Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    facebookSignInUp(thirdPartyLogin)
                }
            }

            override fun onResponse(
                call: Call<Data<Response>>,
                response: retrofit2.Response<Data<Response>>
            ) {
                onThridPartyRes(
                    response,
                    App.Companion.RegistrationProvider.FACEBOOK.value(),
                    thirdPartyLogin
                )
            }

            override fun onFailure(call: Call<Data<Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }

    override fun googleSignInUp(thirdPartyLogin: ThirdPartyLogin) {
        val signUpRequest = ApiService.apiClient!!.registerWithGoogle(thirdPartyLogin)
        frag.addNetworkRequest(signUpRequest)
        signUpRequest.enqueue(object : CustomCallback<Data<Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    googleSignInUp(thirdPartyLogin)
                }
            }

            override fun onResponse(
                call: Call<Data<Response>>,
                response: retrofit2.Response<Data<Response>>
            ) {
                onThridPartyRes(
                    response,
                    App.Companion.RegistrationProvider.GOOGLE.value(),
                    thirdPartyLogin
                )
            }

            override fun onFailure(call: Call<Data<Response>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }

    private fun onThridPartyRes(
        response: retrofit2.Response<Data<Response>>,
        provider: Int,
        thirdPartyLogin: ThirdPartyLogin
    ) {
        if (response.body() != null) {
            frag.onThirdPartyLogin(response.body()!!, provider, thirdPartyLogin)

        } else {
            val error = Gson().fromJson<BaseResponse>(
                response.errorBody()?.string(),
                BaseResponse::class.java
            )

            frag.onErrorOccured(error)
        }
    }
}