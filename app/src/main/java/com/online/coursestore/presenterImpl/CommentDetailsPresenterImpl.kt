package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.CommentDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CommentDetailsPresenterImpl(private val frag: CommentDetailsFrag) :
    Presenter.CommentDetailsPresenter {

    override fun removeComment(commentId: Int) {
        ApiService.apiClient!!.removeComment(commentId)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        removeComment(commentId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onCommentRemoved(response.body()!!)
                    }
                }
            })
    }
}