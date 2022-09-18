package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Comments
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import retrofit2.Call
import retrofit2.Response

class CommentsPresenterImpl() : Presenter.CommentsPresenter {

    override fun getComments(callback: ItemCallback<Comments>) {
        ApiService.apiClient!!.getComments().enqueue(object : CustomCallback<Data<Comments>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getComments(callback)
                }
            }

            override fun onResponse(
                call: Call<Data<Comments>>,
                response: Response<Data<Comments>>
            ) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!.data!!)
                }
            }
        })
    }
}