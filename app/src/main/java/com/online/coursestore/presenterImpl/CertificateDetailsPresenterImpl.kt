package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.QuizResult
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.CertificateDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CertificateDetailsPresenterImpl(private val frag: CertificateDetailsFrag) :
    Presenter.CertificateDetailsPresenter {

    override fun getStudents() {
        val certificateStudentsReq = ApiService.apiClient!!.getCertificateStudents()
        frag.addNetworkRequest(certificateStudentsReq)
        certificateStudentsReq.enqueue(object : CustomCallback<Data<List<QuizResult>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getStudents()
                }
            }

            override fun onResponse(
                call: Call<Data<List<QuizResult>>>,
                response: Response<Data<List<QuizResult>>>
            ) {
                if (response.body() != null) {
                    frag.onStudentsReceived(response.body()!!.data!!)
                }
            }

        })
    }
}