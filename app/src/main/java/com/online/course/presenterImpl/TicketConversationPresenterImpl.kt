package com.online.course.presenterImpl

import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.TicketConversationFrag
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import java.io.IOException
import java.lang.NumberFormatException

class TicketConversationPresenterImpl(private val frag: TicketConversationFrag) :
    Presenter.TicketConversationPresenter {

    override fun closeTicket(ticketId: Int) {
        ApiService.apiClient!!.closeTicket(ticketId).enqueue(object :CustomCallback<BaseResponse>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    closeTicket(ticketId)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onTicketClosed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }

    override fun getFileSize(url: String, sizeCallback: ItemCallback<Long>) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).head().build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.body != null) {
                    try {
                        sizeCallback.onItem(response.headers["Content-Length"]!!.toLong())
                    } catch (ex: NullPointerException) {
                        sizeCallback.onItem(0)
                    } catch (ex: NumberFormatException) {
                        sizeCallback.onItem(0)
                    }
                }
            }
        })
    }
}