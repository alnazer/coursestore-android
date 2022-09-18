package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.Utils.toInt
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Course
import com.online.coursestore.model.Data
import com.online.coursestore.model.KeyValuePair
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.RewardCoursesFrag
import retrofit2.Call
import retrofit2.Response

class RewardCoursesPresenterImpl(private val frag: RewardCoursesFrag) :
    Presenter.RewardCoursesPresenter {

    override fun getRewardCourses(categories: List<KeyValuePair>?, options: List<KeyValuePair>?) {
        val filter = HashMap<String, String>()
        filter["reward"] = true.toInt().toString()

        if (!categories.isNullOrEmpty()){
            filter[categories[0].key] = categories[0].value
        }

        if (!options.isNullOrEmpty()) {
            for (option in options) {
                filter[option.key] = option.value
            }
        }

        ApiService.apiClient!!.getCourses(filter).enqueue(object : CustomCallback<Data<List<Course>>>{
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getRewardCourses(categories, options)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                res: Response<Data<List<Course>>>
            ) {
                if (res.body() != null) {
                    frag.onResultReceived(res.body()!!.data!!)
                }
            }

        })
    }
}