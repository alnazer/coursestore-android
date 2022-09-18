package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.AddToCart
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.CourseDetailsFrag
import retrofit2.Call
import retrofit2.Response

class CourseDetailsPresenterImpl(private val frag: CourseDetailsFrag) :
    Presenter.CourseDetailsPresenter {

    override fun subscribe(addToCart: AddToCart) {
        ApiService.apiClient!!.subscribe(addToCart).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    subscribe(addToCart)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    frag.onSubscribed(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }

    override fun addCourseToUserCourse(courseId: Int) {
        val addFreeCourseReq = ApiService.apiClient!!.addFreeCourse(courseId)
        frag.addNetworkRequest(addFreeCourseReq)
        addFreeCourseReq.enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    addCourseToUserCourse(courseId)
                }
            }

            override fun onResponse(
                call: Call<BaseResponse>,
                response: Response<BaseResponse>
            ) {
                if (response.body() != null) {
                    frag.onCourseAdded(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                frag.onRequestFailed()
            }
        })
    }
}