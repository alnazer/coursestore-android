package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.QuizAnswer
import com.online.coursestore.model.QuizResult
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.QuizFrag
import retrofit2.Call
import retrofit2.Response

class QuizPresenterImpl(private val frag: QuizFrag) : Presenter.QuizPresenter {

    override fun storeResult(quizId: Int, answer: QuizAnswer) {
        ApiService.apiClient!!.storeQuizResult(quizId, answer)
            .enqueue(object : CustomCallback<Data<Data<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        storeResult(quizId, answer)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<QuizResult>>>,
                    response: Response<Data<Data<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onQuizResultSaved(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<QuizResult>>>, t: Throwable) {
                    super.onFailure(call, t)
                    frag.onRequestFailed()
                }
            })
    }
}