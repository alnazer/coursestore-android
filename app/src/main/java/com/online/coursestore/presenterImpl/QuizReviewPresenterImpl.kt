package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.QuizAnswerItem
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.QuizReviewFrag
import retrofit2.Call
import retrofit2.Response

class QuizReviewPresenterImpl(private val frag: QuizReviewFrag) : Presenter.QuizReviewPresenter {

    override fun storeReviewResult(resultId: Int, review: List<QuizAnswerItem>) {
        ApiService.apiClient!!.storeReviewResult(resultId, review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeReviewResult(resultId, review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onResultStored(response.body()!!)
                    }
                }

            })
    }
}