package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.Meetings
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.MeetingsTabFrag
import retrofit2.Call
import retrofit2.Response

class MeetingsPresenterImpl(private val frag: MeetingsTabFrag) : Presenter.MeetingsPresenter {

    override fun getMeetings() {
        val meetingsReq = ApiService.apiClient!!.getMeetings()
        frag.addNetworkRequest(meetingsReq)
        meetingsReq.enqueue(object : CustomCallback<Data<Meetings>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getMeetings()
                }
            }

            override fun onResponse(
                call: Call<Data<Meetings>>,
                response: Response<Data<Meetings>>
            ) {
                if (response.body() != null) {
                    frag.onMeetingsReceived(response.body()!!.data!!)
                }
            }
        })
    }
}