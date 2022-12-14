package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Count
import com.online.coursestore.model.Data
import com.online.coursestore.model.FinancialSettings
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SettingsFinancialFrag
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class SettingsFinancialPresenterImpl(private val frag: SettingsFinancialFrag) :
    Presenter.SettingsFinancialPresenter {

    override fun uploadFinancialSettings(financialSettings: FinancialSettings) {
        ApiService.apiClient!!.changeProfileSettings(financialSettings)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        uploadFinancialSettings(financialSettings)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onSettingsSaved(response.body()!!, financialSettings)
                    }
                }

            })
    }

    override fun getAccountTypes() {
        ApiService.apiClient!!.getAccountTypes().enqueue(object :
            CustomCallback<Data<Count<String>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAccountTypes()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<String>>>,
                response: Response<Data<Count<String>>>
            ) {
                if (response.body() != null) {
                    frag.onAccountTypesReceived(response.body()!!.data!!)
                }
            }

        })
    }

    override fun uploadFinancialSettingsFiles(identityFile: File, certFile: File) {
        val identityFileBody = identityFile.asRequestBody()
        val identityFilePart =
            MultipartBody.Part.createFormData("identity_scan", identityFile.name, identityFileBody)

        val certFileBody = certFile.asRequestBody()
        val certFilePart =
            MultipartBody.Part.createFormData("certificate", certFile.name, certFileBody)

        ApiService.apiClient!!.uploadFinanicalSettings(identityFilePart, certFilePart)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        uploadFinancialSettingsFiles(identityFile, certFile)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onFilesSaved(response.body()!!)
                    }
                }

            })
    }
}