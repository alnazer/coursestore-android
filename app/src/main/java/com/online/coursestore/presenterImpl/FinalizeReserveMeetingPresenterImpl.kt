package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.ReserveTimeMeeting
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.FinalizeReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class FinalizeReserveMeetingPresenterImpl(private val dialog: FinalizeReserveMeetingDialog) :
    Presenter.FinalizeReserveMeetingPresenter {

    override fun reserveMeeting(reserveMeeting: ReserveTimeMeeting) {
        ApiService.apiClient!!.reserveMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        reserveMeeting(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingReserved(response.body()!!)
                    } else {
                        dialog.onFailed()
                    }
                }

                override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                    super.onFailure(call, t)
                    dialog.onFailed()
                }
            })
    }
}