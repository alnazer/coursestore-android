package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Message
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.NewMessageDialog
import retrofit2.Call
import retrofit2.Response

class NewMessagePresenterImpl(private val dialog: NewMessageDialog) :
    Presenter.NewMessagePresenter {


    override fun addMessage(userId: Int, message: Message) {
        ApiService.apiClient!!.addNewMessage(userId, message)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        addMessage(userId, message)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMessageAdded(response.body()!!)
                    }
                }

            })
    }
}