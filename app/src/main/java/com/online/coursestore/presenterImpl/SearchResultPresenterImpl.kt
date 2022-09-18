package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Data
import com.online.coursestore.model.SearchObject
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SearchResultFrag
import retrofit2.Call
import retrofit2.Response

class SearchResultPresenterImpl(private val searchFrag: SearchResultFrag) :
    Presenter.SearchResultPresenter {

    override fun search(s: String) {
        val searchCoursesAndUsers = ApiService.apiClient!!.searchCoursesAndUsers(s)
        searchFrag.addNetworkRequest(searchCoursesAndUsers)
        searchCoursesAndUsers.enqueue(object : CustomCallback<Data<SearchObject>> {
            override fun onResponse(
                call: Call<Data<SearchObject>>,
                response: Response<Data<SearchObject>>
            ) {
                if (response.body() != null) {
                    searchFrag.onSearchResultReceived(response.body()!!)
                }
            }

            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    search(s)
                }
            }

        })
    }
}