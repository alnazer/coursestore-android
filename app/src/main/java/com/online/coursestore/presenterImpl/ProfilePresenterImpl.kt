package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.UserProfile
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.ProfileFrag
import retrofit2.Call
import retrofit2.Response

class ProfilePresenterImpl(private val frag: ProfileFrag) : Presenter.ProfilePresenter {

    override fun getUserProfile(userId: Int) {
        val userProfileReq = ApiService.apiClient!!.getUserProfile(userId)
        frag.addNetworkRequest(userProfileReq)
        userProfileReq.enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getUserProfile(userId)
                }
            }

            override fun onResponse(
                call: Call<Data<Data<UserProfile>>>,
                response: Response<Data<Data<UserProfile>>>
            ) {
                if (response.body() != null) {
                    frag.onUserProfileReceived(response.body()!!.data!!)
                }
            }

        })
    }
}