package com.online.coursestore.presenterImpl

import com.google.gson.Gson
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Login
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SignInFrag
import retrofit2.Call
import retrofit2.Response
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data

class SignInPresenterImpl(private val frag: SignInFrag) : ThirdPartyPresenterImpl(frag),
    Presenter.SignInPresenter {

    override fun login(login: Login) {
        val loginRequest = ApiService.apiClient!!.login(login)
        frag.addNetworkRequest(loginRequest)
        loginRequest.enqueue(object : CustomCallback<Data<com.online.coursestore.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    login(login)
                }
            }

            override fun onResponse(
                call: Call<Data<com.online.coursestore.model.Response>>,
                response: Response<Data<com.online.coursestore.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onSuccessfulLogin(response.body()!!)
                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    frag.onErrorOccured(error)
                }
            }

            override fun onFailure(
                call: Call<Data<com.online.coursestore.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onErrorOccured(null)
            }
        })
    }
}