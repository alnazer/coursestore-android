package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Review
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.CourseReviewDialog
import retrofit2.Call
import retrofit2.Response

class CourseReviewPresenterImpl(private val dialog: CourseReviewDialog) :
    Presenter.CourseReviewPresenter {

    override fun addReview(review: Review) {
        ApiService.apiClient!!.addCourseReview(review)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        addReview(review)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onReviewSaved(response.body()!!, review)
                    }
                }

            })
    }

}