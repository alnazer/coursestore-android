package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Category
import com.online.coursestore.model.Count
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.widget.ProvidersFiltersDialog
import retrofit2.Call
import retrofit2.Response

class ProvidersFiltersPresenterImpl(private val dialog: ProvidersFiltersDialog) :
    Presenter.ProvidersFiltersPresenter {

    override fun getCategories() {
        val categories = ApiService.apiClient!!.getCategories()
        dialog.addNetworkRequest(categories)
        categories.enqueue(object : CustomCallback<Data<Count<Category>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getCategories()
                }
            }

            override fun onResponse(
                call: Call<Data<Count<Category>>>,
                response: Response<Data<Count<Category>>>
            ) {
                if (response.body() != null)
                    dialog.onCategoriesRecevied(response.body()!!)
            }

        })
    }
}