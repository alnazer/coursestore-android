package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.Ticket
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SupportFrag
import retrofit2.Call
import retrofit2.Response

class SupportPresenterImpl(private val frag: SupportFrag) : Presenter.SupportPresenter {

    override fun getTickets() {
        val supportsReq = ApiService.apiClient!!.getTickets()
        frag.addNetworkRequest(supportsReq)
        supportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getTickets()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getClassSupport() {
        val classSupportsReq = ApiService.apiClient!!.getClassSupports()
        frag.addNetworkRequest(classSupportsReq)
        classSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }

    override fun getMyClassSupport() {
        val myClassSupportsReq = ApiService.apiClient!!.getMyClassSupports()
        frag.addNetworkRequest(myClassSupportsReq)
        myClassSupportsReq.enqueue(object : CustomCallback<Data<List<Ticket>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMyClassSupport()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Ticket>>>,
                response: Response<Data<List<Ticket>>>
            ) {
                if (response.body() != null) {
                    frag.onSupportsReceived(response.body()!!)
                }
            }

        })
    }
}