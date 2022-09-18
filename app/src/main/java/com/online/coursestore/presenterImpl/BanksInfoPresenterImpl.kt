package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Count
import com.online.coursestore.model.Data
import com.online.coursestore.model.SystemBankAccount
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.BanksInfoFrag
import retrofit2.Call
import retrofit2.Response

class BanksInfoPresenterImpl(private val frag: BanksInfoFrag) : Presenter.BanksInfoPresenter {

    override fun getBanksInfo() {
        val bankInfosReq = ApiService.apiClient!!.getBankInfos()
        frag.addNetworkRequest(bankInfosReq)
        bankInfosReq.enqueue(object : CustomCallback<Data<Count<SystemBankAccount>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBanksInfo()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<SystemBankAccount>>>,
                    response: Response<Data<Count<SystemBankAccount>>>
                ) {
                    if (response.body() != null) {
                        frag.onInfosReceived(response.body()!!.data!!.items)
                    }
                }

            })
    }
}