package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.ReserveMeetingDialog
import retrofit2.Call
import retrofit2.Response

class ReserveMeetingDialogPresenterImpl(private val dialog: ReserveMeetingDialog) :
    Presenter.ReserveMeetingDialogPresenter {

    override fun getAvailableMeetingTimes(userId: Int, date: String) {
        val availableMeetingTimesReq = ApiService.apiClient!!.getAvailableMeetingTimes(userId, date)
        dialog.addNetworkRequest(availableMeetingTimesReq)
        availableMeetingTimesReq.enqueue(object : CustomCallback<Data<Count<Timing>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAvailableMeetingTimes(userId, date)
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Timing>>>,
                response: Response<Data<Count<Timing>>>
            ) {
                if (response.body() != null) {
                    dialog.onTimingsReceived(response.body()!!.data!!.items)
                }
            }
        })
    }
}