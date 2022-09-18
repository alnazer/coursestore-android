package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.SaasPackage
import com.online.coursestore.model.SaasPackageItem
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SaasPackageFrag
import retrofit2.Call
import retrofit2.Response

class SaasPackagePresenterImpl(private val frag: SaasPackageFrag) : Presenter.SaasPackagePresenter {

    override fun getSaasPackages() {
        ApiService.apiClient!!.getSaasPackages()
            .enqueue(object : CustomCallback<Data<SaasPackage>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getSaasPackages()
                    }
                }

                override fun onResponse(
                    call: Call<Data<SaasPackage>>,
                    res: Response<Data<SaasPackage>>
                ) {
                    if (res.body() != null) {
                        frag.onSaasPackageReceived(res.body()!!.data!!)
                    }
                }

            })
    }

    override fun checkoutSubscription(saasPackageItem: SaasPackageItem) {
        ApiService.apiClient!!.checkoutSaasPackage(saasPackageItem)
            .enqueue(object : CustomCallback<Data<com.online.coursestore.model.Response>> {
                override fun onStateChanged(): RetryListener {
                    return RetryListener {
                        checkoutSubscription(saasPackageItem)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.coursestore.model.Response>>,
                    res: Response<Data<com.online.coursestore.model.Response>>
                ) {
                    if (res.body() != null) {
                        frag.onCheckout(res.body()!!)
                    }
                }

            })
    }
}