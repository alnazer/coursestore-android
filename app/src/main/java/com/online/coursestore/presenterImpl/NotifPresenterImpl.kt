package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Count
import com.online.coursestore.model.Data
import com.online.coursestore.model.Notif
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.NotifsFrag
import retrofit2.Call
import retrofit2.Response

class NotifPresenterImpl(private val frag: NotifsFrag) : Presenter.NotifPresenter {

    override fun getNotifs() {
        val notifsReq = ApiService.apiClient!!.getNotifs()
        frag.addNetworkRequest(notifsReq)
        notifsReq.enqueue(object :CustomCallback<Data<Count<Notif>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getNotifs()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Notif>>>,
                response: Response<Data<Count<Notif>>>
            ) {
                if (response.body() != null) {
                    frag.onNotifsReceived(response.body()!!)
                }
            }
        })
    }
}