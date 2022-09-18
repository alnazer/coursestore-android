package com.online.course.presenterImpl

import com.online.course.manager.net.ApiService
import com.online.course.manager.net.CustomCallback
import com.online.course.manager.net.RetryListener
import com.online.course.model.BaseResponse
import com.online.course.model.Data
import com.online.course.model.Follow
import com.online.course.presenter.Presenter
import com.online.course.ui.widget.PricingPlansDialog
import retrofit2.Call
import retrofit2.Response

class PricingPlansPresenterImpl(private val dialog: PricingPlansDialog) :
    Presenter.PricingPlansPresenter {

    override fun purchaseWithPoints(courseId: Int) {
        ApiService.apiClient!!.purchaseWithPoints(courseId, Follow())
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        purchaseWithPoints(courseId)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    res: Response<BaseResponse>
                ) {
                    if (res.body() != null) {
                        dialog.onPurchase(res.body()!!)
                    }
                }

            })
    }
}