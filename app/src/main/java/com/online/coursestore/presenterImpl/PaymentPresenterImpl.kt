package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.PaymentOptionsDialog
import retrofit2.Call
import retrofit2.Response

class PaymentPresenterImpl(private val frag: PaymentOptionsDialog) : Presenter.PaymentPresenter {

    override fun checkout(checkout: Checkout) {
        val checkoutReq = ApiService.apiClient!!.checkout(checkout)
        frag.addNetworkRequest(checkoutReq)
        checkoutReq.enqueue(object : CustomCallback<Data<CheckoutResponse>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    checkout(checkout)
                }
            }

            override fun onResponse(
                call: Call<Data<CheckoutResponse>>,
                response: Response<Data<CheckoutResponse>>
            ) {
                frag.onCheckout(response.body()!!)
            }

            override fun onFailure(call: Call<Data<CheckoutResponse>>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }

    override fun verifyPayment(verifyPayment: VerifyPayment) {
        val verifyPaymentRequest = ApiService.apiClient!!.verifyPayment(verifyPayment)
        frag.addNetworkRequest(verifyPaymentRequest)
        verifyPaymentRequest.enqueue(object : CustomCallback<Data<VerifyPaymentResponse>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    verifyPayment(verifyPayment)
                }
            }

            override fun onResponse(
                call: Call<Data<VerifyPaymentResponse>>,
                response: Response<Data<VerifyPaymentResponse>>
            ) {
                frag.onVerifyPaymentResponse(response.body()!!)
            }
        })
    }
}