package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.OnDownloadProgressListener
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.ProgressiveLoadingDialog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception

class ProgressiveLoadingDialogPresenterImpl(private val dialog: ProgressiveLoadingDialog) :
    Presenter.ProgressiveLoadingPresenter {

    companion object{
        private const val TAG = "ProgressiveLoadingDialo"
    }

    override fun downloadFile(
        fileDir: String?,
        fileUrl: String,
        downloadListener: OnDownloadProgressListener,
        toDownloads: Boolean,
        fileNameFromHeader: Boolean,
    ) {
        val baseUrlAndHost = Utils.getBaseUrlAndHostFromUrl(fileUrl) ?: return

        val download = ApiService.getDownloadApiClient(baseUrlAndHost[0], downloadListener, null)
            .download(baseUrlAndHost[1])
        dialog.addNetworkRequest(download)
        download.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    dialog.context?.let {
                        Thread {
                            try {
                                val filename: String = if (fileNameFromHeader) {
                                    val disposition = response.headers()["Content-Disposition"]
                                    val contentSplit = disposition!!.split("filename=")
                                    contentSplit[1].replace("filename=", "")
                                        .replace("\"", "").trim();
                                } else {
                                    Utils.extractFileNameFromUrl(fileUrl)
                                }

                                val byteStream = response.body()!!.byteStream()
                                val filePath = Utils.saveFile(
                                    dialog.requireContext(),
                                    fileDir ?: "",
                                    filename,
                                    byteStream
                                )
                                if (toDownloads) {
                                    dialog.context.let {
                                        if (filePath != null) {
                                            Utils.copyFileToDownloads(
                                                dialog.requireContext(),
                                                File(filePath),
                                                filename
                                            )
                                        }
                                    }
                                }

                                dialog.onFileSaved(filePath)
                            } catch (ex: Exception) {
                                dialog.onFileSaveFailed()
                            }
                        }.start()
                    }
                }
            }
        })
    }
}