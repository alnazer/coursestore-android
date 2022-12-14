package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.PaymentRequest
import com.online.coursestore.model.Response
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.ChargeDialog
import retrofit2.Call

class ChargeAccountPresenterImpl(private val dialog: ChargeDialog) :
    Presenter.ChargeAccountPresenter {
    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Response>>,
                    response: retrofit2.Response<Data<Response>>
                ) {
                    if (response.body() != null) {
                        dialog.onCheckout(response.body()!!)
                    }
                }

            })
    }

}