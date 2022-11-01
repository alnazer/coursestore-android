package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.PaymentRequest
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.ChargeAccountPaymentFrag
import retrofit2.Call
import retrofit2.Response

class ChargeAccountPaymentPresenterImpl(private val frag: ChargeAccountPaymentFrag) :
    Presenter.ChargeAccountPaymentPresenter {

    override fun requestPayment(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.requestPayment(paymentRequest)
    }

    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
    }

    override fun requestPaymentFromCharge(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.payWithCredit(paymentRequest).enqueue(object : CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    requestPaymentFromCharge(paymentRequest)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onPaymentWithCharge(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }

        })
    }

}