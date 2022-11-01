package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.MeetingDetailsMoreDialog
import retrofit2.Call
import retrofit2.Response

class MeetingDetailsMorePresenterImpl(private val dialog: MeetingDetailsMoreDialog) :
    Presenter.MeetingDetailsMorePresenter {

    override fun finishMeeting(meetingId: Int) {
        ApiService.apiClient!!.finishMeeting(meetingId, Any())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        finishMeeting(meetingId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        dialog.onMeetingFinished(response.body()!!)
                    }
                }
            })
    }
}