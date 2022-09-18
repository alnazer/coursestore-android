package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Follow
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.ProfileAboutFrag
import retrofit2.Call
import retrofit2.Response

class ProfileAboutPresenterImpl(private val frag: ProfileAboutFrag) :
    Presenter.ProfileAboutPresenter {

    override fun follow(userId: Int, follow: Follow) {
        ApiService.apiClient!!.followUser(userId, follow)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        follow(userId, follow)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onFollowed(follow)
                    }
                }
            })
    }
}