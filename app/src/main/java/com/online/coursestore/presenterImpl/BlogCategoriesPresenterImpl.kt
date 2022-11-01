package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Category
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.BlogCategoriesDialog
import retrofit2.Call
import retrofit2.Response

class BlogCategoriesPresenterImpl(private val dialog: BlogCategoriesDialog) : Presenter.BlogCategoriesPresenter {
    override fun getBlogCategories() {
        ApiService.apiClient!!.getBlogCategories()
            .enqueue(object : CustomCallback<Data<List<Category>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getBlogCategories()
                    }
                }

                override fun onResponse(
                    call: Call<Data<List<Category>>>,
                    response: Response<Data<List<Category>>>
                ) {
                    if (response.body() != null) {
                        dialog.onBlogCatsReceived(response.body()!!.data!!)
                    }
                }

            })
    }
}