package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.ForgetPassword
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.ForgetPasswordFrag
import retrofit2.Call
import retrofit2.Response

class ForgotPasswordPresenterImpl(private val frag: ForgetPasswordFrag): Presenter.ForgotPasswordPresenter {

    override fun sendChangePasswordLink(forgetPassword: ForgetPassword) {
        val sendChangePasswordLinkReq = ApiService.apiClient!!.sendChangePasswordLink(forgetPassword)
        frag.addNetworkRequest(sendChangePasswordLinkReq)
        sendChangePasswordLinkReq.enqueue(object : CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    sendChangePasswordLink(forgetPassword)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onPasswordChanged(response.body()!!)
                }
            }

        })
    }
}