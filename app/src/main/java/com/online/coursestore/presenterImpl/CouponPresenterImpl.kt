package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Coupon
import com.online.coursestore.model.CouponValidation
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.CouponDialog
import retrofit2.Call
import retrofit2.Response

class CouponPresenterImpl(private val dialog: CouponDialog) : Presenter.CouponPresenter {

    override fun validateCoupon(coupon: Coupon) {
        ApiService.apiClient!!.validateCoupon(coupon)
            .enqueue(object : CustomCallback<Data<CouponValidation>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        validateCoupon(coupon)
                    }
                }

                override fun onResponse(
                    call: Call<Data<CouponValidation>>,
                    response: Response<Data<CouponValidation>>
                ) {
                    if (response.body() != null) {
                        dialog.onCouponValidated(response.body()!!)
                    }
                }

            })
    }


}