package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.UserChangeSettings
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SettingsGeneralFrag
import retrofit2.Call
import retrofit2.Response

class SettingsGeneralPresenterImpl(private val frag: SettingsGeneralFrag) :
    Presenter.SettingsGeneralPresenter {

    override fun changeProfileSettings(changeSettings: UserChangeSettings) {
        ApiService.apiClient!!.changeProfileSettings(changeSettings)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changeProfileSettings(changeSettings)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsChanged(response.body()!!, changeSettings)
                    }
                }

            })
    }
}