package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.PayoutRequestDialog
import retrofit2.Call
import retrofit2.Response

class PayoutRequestPresenterImpl(private val dialog: PayoutRequestDialog) :
    Presenter.PayoutRequestPresenter {

    override fun requestPayout() {
        ApiService.apiClient!!.requestPayout(Any()).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    requestPayout()
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    dialog.onPayoutSaved(response.body()!!)
                }
            }

        })
    }
}