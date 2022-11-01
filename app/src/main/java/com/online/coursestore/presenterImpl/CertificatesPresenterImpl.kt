package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.Quiz
import com.online.coursestore.model.QuizResult
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.CertificatesFrag
import retrofit2.Call
import retrofit2.Response

class CertificatesPresenterImpl(private val frag: CertificatesFrag) :
    Presenter.CertificatesPresenter {

    override fun getAchievementCertificates() {
        val achievementCertificatesReq = ApiService.apiClient!!.getAchievementCertificates()
        frag.addNetworkRequest(achievementCertificatesReq)
        achievementCertificatesReq
            .enqueue(object : CustomCallback<Data<List<QuizResult>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getAchievementCertificates()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<QuizResult>>>,
                    response: Response<Data<List<QuizResult>>>
                ) {
                    if (response.body() != null) {
                        frag.onCertsReceived(response.body()!!.data!!)
                    }
                }

            })
    }

    override fun getClassCertificates() {
        val classCertificates = ApiService.apiClient!!.getClassCertificates()
        frag.addNetworkRequest(classCertificates)
        classCertificates.enqueue(object : CustomCallback<Data<Data<List<Quiz>>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAchievementCertificates()
                }
            }

            override fun onResponse(
                call: Call<Data<Data<List<Quiz>>>>,
                response: Response<Data<Data<List<Quiz>>>>
            ) {
                if (response.body() != null) {
                    frag.onClassCertsReceived(response.body()!!.data!!.data!!)
                }
            }

        })
    }

}