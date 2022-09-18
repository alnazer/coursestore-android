package com.online.course.presenterImpl

import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.manager.net.*
import com.online.course.model.BaseResponse
import com.online.course.model.ChapterFileItem
import com.online.course.model.ChapterItemMark
import com.online.course.presenter.Presenter
import com.online.course.ui.frag.CourseChapterItemFrag
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import java.io.File

class CourseChapterItemPresenterImpl(private val frag: CourseChapterItemFrag) :
    Presenter.CourseChapterItemPresenter {

    companion object {
        val downloadingRequests: HashMap<Int, Call<*>> = HashMap()
    }

    override fun changeItemStatus(chapterItemMark: ChapterItemMark) {
        ApiService.apiClient!!.changeLessonItemStatus(chapterItemMark.courseId, chapterItemMark)
            .enqueue(object : CustomCallback<BaseResponse> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        changeItemStatus(chapterItemMark)
                    }
                }

                override fun onResponse(
                    call: Call<BaseResponse>,
                    response: Response<BaseResponse>
                ) {
                    if (response.body() != null) {
                        frag.onItemStatusChanged(response.body()!!, chapterItemMark)
                    }
                }

            })
    }

    override fun downloadFile(
        fileItem: ChapterFileItem,
        progressListener: OnDownloadProgressListener
    ) {
        val fileId = fileItem.id

        val baseUrlAndHost = Utils.getBaseUrlAndHostFromUrl(fileItem.file) ?: return

        val downloadRequest =
            ApiService.getDownloadApiClient(baseUrlAndHost[0], progressListener, fileId)
                .download(baseUrlAndHost[1])

        downloadingRequests[fileId] = downloadRequest

        downloadRequest.enqueue(object : CustomCallback<ResponseBody> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    downloadFile(fileItem, progressListener)
                }
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.body() != null) {
                    frag.context?.let {

                        Thread {
                            val byteStream = response.body()!!.byteStream()
                            val filePath = Utils.saveFile(
                                frag.requireContext(),
                                App.Companion.Directory.DOWNLOADS.value(),
                                "${fileId}.${fileItem.fileType}",
                                byteStream
                            )

                            if (filePath != null) {
                                Utils.copyFileToDownloads(
                                    frag.requireContext(),
                                    File(filePath),
                                    "${fileItem.title}.${fileItem.fileType}"
                                )
                            }

                        }.start()
                    }
                }
            }
        })
    }

    override fun cancelDownload(fileId: Int) {
        if (downloadingRequests.containsKey(fileId)) {
            downloadingRequests[fileId]!!.cancel()
            downloadingRequests.remove(fileId)
        }
    }
}