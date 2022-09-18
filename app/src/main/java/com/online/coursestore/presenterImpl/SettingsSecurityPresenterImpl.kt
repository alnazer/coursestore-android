package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.ChangePassword
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SettingsSecurityFrag
import retrofit2.Call
import retrofit2.Response

class SettingsSecurityPresenterImpl(private val frag: SettingsSecurityFrag) : Presenter.SettingsSecurityPresenter {

    override fun changePassword(changePassword: ChangePassword) {
        ApiService.apiClient!!.changePassword(changePassword)
            .enqueue(object : CustomCallback<Data<com.online.coursestore.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changePassword(changePassword)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.coursestore.model.Response>>,
                    response: Response<Data<com.online.coursestore.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onPasswordChanges(response.body()!!)
                    }
                }

            })
    }

}