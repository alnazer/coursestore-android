package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.PayoutRes
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.FinancialPayoutFrag
import retrofit2.Call
import retrofit2.Response

class FinancialPayoutPresenterImpl(private val frag: FinancialPayoutFrag) :
    Presenter.FinancialPayoutPresenter {

    override fun getPayouts() {
        val payoutsReq = ApiService.apiClient!!.getPayouts()
        frag.addNetworkRequest(payoutsReq)
        payoutsReq.enqueue(object :CustomCallback<Data<PayoutRes>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getPayouts()
                }
            }

            override fun onResponse(
                call: Call<Data<PayoutRes>>,
                response: Response<Data<PayoutRes>>
            ) {
                if (response.body() != null) {
                    frag.onPayoutsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}