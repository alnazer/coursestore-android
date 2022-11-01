package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Follow
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.ClassDetailsMoreDialog
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