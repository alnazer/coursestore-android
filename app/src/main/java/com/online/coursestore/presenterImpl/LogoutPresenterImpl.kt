package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Follow
import com.online.coursestore.presenter.Presenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogoutPresenterImpl: Presenter.LogoutPresenter {
    override fun logout() {
        ApiService.apiClient!!.logout(Follow()).enqueue(object : Callback<BaseResponse> {
            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {

            }
        })
    }
}