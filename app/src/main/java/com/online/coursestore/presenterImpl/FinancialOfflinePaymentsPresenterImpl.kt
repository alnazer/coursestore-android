package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.OfflinePayment
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.FinancialOfflinePaymentsFrag
import retrofit2.Call
import retrofit2.Response

class FinancialOfflinePaymentsPresenterImpl(private val frag: FinancialOfflinePaymentsFrag) :
    Presenter.FinancialOfflinePaymentsPresenter {

    override fun getOfflinePayments() {
        val offlinePaymentsReq = ApiService.apiClient!!.getOfflinePayments()
        frag.addNetworkRequest(offlinePaymentsReq)
        offlinePaymentsReq.enqueue(object : CustomCallback<Data<List<OfflinePayment>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getOfflinePayments()
                }
            }

            override fun onResponse(
                call: Call<Data<List<OfflinePayment>>>,
                response: Response<Data<List<OfflinePayment>>>
            ) {
                if (response.body() != null) {
                    frag.onPaymentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}