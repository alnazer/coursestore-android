package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Course
import com.online.course.model.Follow
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.ClassDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class CourseDetailsMorePresenterImpl(private val dialog: ClassDetailsMoreDialog) :
    Presenter.CourseDetailsMorePresenter {

    override fun addToFavorite(itemId: Int) {
        ApiService.apiClient!!.addRemoveFromFavorite(itemId, Follow())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        addToFavorite(itemId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onItemAddedToFavorites(response.body()!!)
                    }
                }

            })

    }

}