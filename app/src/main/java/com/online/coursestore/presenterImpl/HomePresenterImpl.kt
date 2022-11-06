package com.online.coursestore.presenterImpl

import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.Course
import com.online.coursestore.model.Data
import com.online.coursestore.model.SoonCourse
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.frag.HomeFrag
import retrofit2.Call
import retrofit2.Response

class HomePresenterImpl(private val frag: HomeFrag) : Presenter.HomePresenter {

    override fun getFeaturedCourses() {
        val featuredCourses = ApiService.apiClient!!.getFeaturedCourses()
        frag.addNetworkRequest(featuredCourses)
        featuredCourses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getFeaturedCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onFeaturedCoursesReceived(response.body()!!.data!!)
                }
            }
        })
    }

    override fun getNewestCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getNewestCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onNewestCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getBestRatedCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBestRatedCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
//                    frag.onBestRatedCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getBestSellingCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getBestSellingCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
//                    frag.onBestSellersCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getAllCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getAllCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
                    frag.onAllCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getSoonCourses() {
        val courses = ApiService.apiClient!!.getSoonCourses()
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<SoonCourse>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getSoonCourses()
                }
            }

            override fun onResponse(
                call: Call<Data<List<SoonCourse>>>,
                response: Response<Data<List<SoonCourse>>>
            ) {
                if (response.body() != null) {
                    frag.onSoonCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getDiscountedCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getDiscountedCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
//                    frag.onDiscountedCoursesRecevied(response.body()!!)
                }
            }
        })
    }

    override fun getFreeCourses(map: HashMap<String, String>) {
        val courses = ApiService.apiClient!!.getCourses(map)
        frag.addNetworkRequest(courses)
        courses.enqueue(object : CustomCallback<Data<List<Course>>> {
            override fun onStateChanged(): RetryListener? {
                return RetryListener {
                    getFreeCourses(map)
                }
            }

            override fun onResponse(
                call: Call<Data<List<Course>>>,
                response: Response<Data<List<Course>>>
            ) {
                if (response.body() != null) {
//                    frag.onFreeCoursesRecevied(response.body()!!)
                }
            }
        })
    }
}