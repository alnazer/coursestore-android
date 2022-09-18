package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Course
import com.online.coursestore.model.Data
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.SearchFrag
import retrofit2.Call
import retrofit2.Response

class SearchPresenterImpl(private val frag: SearchFrag) : Presenter.SearchPresenter {

    override fun getBestRatedCourses() {
        val map = HashMap<String, String>()
        map["offset"] = "0"
        map["limit"] = "3"
        map["sort"] = "best_rates"

        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBestRatedCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onBestRatedCoursesRecevied(response.body()!!)
                }
            }
        })
    }
}