package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Follow
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.PricingPlansDialog
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