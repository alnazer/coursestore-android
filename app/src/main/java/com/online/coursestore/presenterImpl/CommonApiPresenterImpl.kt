package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import retrofit2.Call
import retrofit2.Response

class CommonApiPresenterImpl private constructor() : Presenter.CommonApiPresenter {

    companion object {
        private lateinit var mPresenterImpl: CommonApiPresenterImpl

        fun getInstance(): CommonApiPresenterImpl {
            if (!this::mPresenterImpl.isInitialized) {
                mPresenterImpl = CommonApiPresenterImpl()
            }

            return mPresenterImpl
        }
    }

    override fun getQuickInfo(callback: ItemCallback<QuickInfo>) {
        ApiService.apiClient!!.getQuickInfo().enqueue(object : CustomCallback<QuickInfo> {
            override fun onStateChanged(): RetryListener {
                return RetryListener {
                    getQuickInfo(callback)
                }
            }

            override fun onResponse(call: Call<QuickInfo>, response: Response<QuickInfo>) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!)
                }
            }

            override fun onFailure(call: Call<QuickInfo>, t: Throwable) {
                super.onFailure(call, t)
                callback.onFailed()
            }
        })
    }

    override fun addToCart(addToCart: AddToCart, callback: ItemCallback<BaseResponse>) {
        ApiService.apiClient!!.addToCart(addToCart).enqueue(object : CustomCallback<BaseResponse> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    addToCart(addToCart, callback)
                }
            }

            override fun onResponse(call: Call<BaseResponse>, response: Response<BaseResponse>) {
                if (response.body() != null) {
                    callback.onItem(response.body()!!)
                }
            }

            override fun onFailure(call: Call<BaseResponse>, t: Throwable) {
                super.onFailure(call, t)
                callback.onFailed()
            }
        })
    }

    override fun getUserInfo(userId: Int, callback: ItemCallback<UserProfile>) {
        ApiService.apiClient!!.getUserProfile(userId)
            .enqueue(object : CustomCallback<Data<Data<UserProfile>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getUserInfo(userId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Data<UserProfile>>>,
                    response: Response<Data<Data<UserProfile>>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!.data!!)
                    }
                }

                override fun onFailure(call: Call<Data<Data<UserProfile>>>, t: Throwable) {
                    super.onFailure(call, t)
                    callback.onFailed()
                }

            })
    }

    override fun getCourseDetails(courseId: Int, callback: ItemCallback<Course>) {
        ApiService.apiClient!!.getCourseDetails(courseId)
            .enqueue(object : CustomCallback<Data<Course>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getCourseDetails(courseId, callback)
                    }
                }

                override fun onResponse(
                    call: Call<Data<Course>>,
                    response: Response<Data<Course>>
                ) {
                    if (response.body() != null) {
                        callback.onItem(response.body()!!.data!!)
                    }
                }

                override fun onFailure(call: Call<Data<Course>>, t: Throwable) {
                    super.onFailure(call, t)
                    callback.onFailed()
                }

            })
    }
}