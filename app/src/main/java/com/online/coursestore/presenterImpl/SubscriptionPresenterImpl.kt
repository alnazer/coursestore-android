package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.Subscription
import com.online.coursestore.model.SubscriptionItem
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SubscriptionFrag
import retrofit2.Call
import retrofit2.Response

class SubscriptionPresenterImpl(private val frag: SubscriptionFrag) :
    Presenter.SubscriptionPresenter {

    override fun getSubscriptions() {
        val subscriptionsReq = ApiService.apiClient!!.getSubscriptions()
        frag.addNetworkRequest(subscriptionsReq)
        subscriptionsReq.enqueue(object : CustomCallback<Data<Subscription>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSubscriptions()
                }
            }

            override fun onResponse(
                call: Call<Data<Subscription>>,
                response: Response<Data<Subscription>>
            ) {
                if (response.body() != null) {
                    frag.onSubscriptionsReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun checkoutSubscription(subscriptionItem: SubscriptionItem) {
        val checkoutSubscriptionReq = ApiService.apiClient!!.checkoutSubscription(subscriptionItem)
        frag.addNetworkRequest(checkoutSubscriptionReq)
        checkoutSubscriptionReq.enqueue(object :
            CustomCallback<Data<com.online.coursestore.model.Response>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    checkoutSubscription(subscriptionItem)
                }
            }

            override fun onResponse(
                call: Call<Data<com.online.coursestore.model.Response>>,
                response: Response<Data<com.online.coursestore.model.Response>>
            ) {
                if (response.body() != null) {
                    frag.onCheckout(response.body()!!)
                }
            }

            override fun onFailure(
                call: Call<Data<com.online.coursestore.model.Response>>,
                t: Throwable
            ) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        }

        )
    }
}