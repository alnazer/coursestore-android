package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.ReserveMeeting
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.MeetingJoinDetailsDialog
import retrofit2.Call
import retrofit2.Response

class MeetingJoinDetailsPresenterImpl(private val dialog: MeetingJoinDetailsDialog) :
    Presenter.MeetingJoinDetailsPresenter {

    override fun createJoin(reserveMeeting: ReserveMeeting) {
        ApiService.apiClient!!.createJoinForMeeting(reserveMeeting)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        createJoin(reserveMeeting)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingJoinAdded(response.body()!!)
                    }
                }

            })
    }
}