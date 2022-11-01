package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.SalesRes
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.FinancialSalesFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSalesPresenterImpl(private val frag: FinancialSalesFrag) :
    Presenter.FinancialSalesPresenter {

    override fun getSales() {
        val salesReq = ApiService.apiClient!!.getSales()
        frag.addNetworkRequest(salesReq)
        salesReq.enqueue(object : CustomCallback<Data<SalesRes>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSales()
                }
            }

            override fun onResponse(
                call: Call<Data<SalesRes>>,
                response: Response<Data<SalesRes>>
            ) {
                if (response.body() != null) {
                    frag.onSalesReceived(response.body()!!.data!!)
                }
            }

        })
    }
}