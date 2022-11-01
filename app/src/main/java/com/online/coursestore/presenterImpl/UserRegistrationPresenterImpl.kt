package com.online.coursestore.presenterImpl

import com.google.gson.Gson
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Data
import com.online.coursestore.model.User
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.UserRegistrationDialog
import retrofit2.Call
import retrofit2.Response

class UserRegistrationPresenterImpl(private val dialog: UserRegistrationDialog) :
    Presenter.UserRegistrationPresenter {

    override fun register(user: User) {
        val registerUserRequest = ApiService.apiClient!!.registerUser(user)
        dialog.addNetworkRequest(registerUserRequest)
        registerUserRequest.enqueue(object : CustomCallback<Data<com.online.coursestore.model.Response>> {

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    register(user)
                }
            }

            override fun onResponse(call: Call<Data<com.online.coursestore.model.Response>>,
                                    response: Response<Data<com.online.coursestore.model.Response>>) {
                if (response.body() != null) {
                    dialog.onRegistrationSaved(response.body()!!, user)

                } else {
                    val error = Gson().fromJson<BaseResponse>(
                        response.errorBody()?.string(),
                        BaseResponse::class.java
                    )

                    dialog.onErrorOccured(error)
                }
            }
        })
    }
}