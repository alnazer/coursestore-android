package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Blog
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.BlogsFrag
import retrofit2.Call
import retrofit2.Response

class BlogPresenterImpl(private val frag: BlogsFrag) : Presenter.BlogPresenter {

    override fun getBlogs() {
        val blogs = ApiService.apiClient!!.getBlogs()
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getBlogs(catId: Int) {
        val blogs = ApiService.apiClient!!.getBlogs(catId)
        frag.addNetworkRequest(blogs)
        blogs.enqueue(object : CustomCallback<Data<List<Blog>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBlogs(catId)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Blog>>>,
                response: Response<Data<List<Blog>>>
            ) {
                if (response.body() != null) {
                    frag.onBlogsRecevied(response.body()!!.data!!)
                }
            }
        })
    }
}