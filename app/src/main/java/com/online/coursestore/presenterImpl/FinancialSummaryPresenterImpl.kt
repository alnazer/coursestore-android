package com.online.coursestore.presenterImpl

import androidx.recyclerview.widget.RecyclerView
import com.online.coursestore.manager.adapter.EndLessLoadMoreAdapter
import com.online.coursestore.manager.adapter.FinancialSummeriesEndlessAdapter
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.CustomCallback
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.FinancialSummaryFrag
import retrofit2.Call
import retrofit2.Response

class FinancialSummaryPresenterImpl(
    private val frag: FinancialSummaryFrag,
    private val rv: RecyclerView,
    private val map: HashMap<String, String>
) :
    Presenter.FinancialSummaryPresenter {

//    companion object {
//        private const val PAGE_COUNT = 3
//    }

    private val mItems: MutableList<CommonItem?>
    private val mAdapter: FinancialSummeriesEndlessAdapter
    private var mOffset = 0
    private var PAGE_COUNT = 10

    init {
        mItems = ArrayList()
        mAdapter = FinancialSummeriesEndlessAdapter(mItems, frag.activity as MainActivity)
        rv.adapter = mAdapter
        mAdapter.setLoadMoreListener {
            getSummary()
        }
        mAdapter.setLoading(true)
    }

//    override fun getSummary() {
//        val financialSummaryReq = ApiService.apiClient!!.getFinancialSummary()
//        frag.addNetworkRequest(financialSummaryReq)
//        financialSummaryReq.enqueue(object : CustomCallback<Data<Count<FinancialSummary>>> {
//            override fun onStateChanged(): RetryListener? {
//                return RetryListener {
//                    getSummary()
//                }
//            }
//
//            override fun onResponse(
//                call: Call<Data<Count<FinancialSummary>>>,
//                response: Response<Data<Count<FinancialSummary>>>
//            ) {
//                if (response.body() != null) {
//                    frag.onSummariesReceived(response.body()!!.data!!.items)
//                }
//            }
//        })
//    }

    override fun getSummary() {
        rv.post {
            mItems.add(null)
            mAdapter.notifyItemInserted(mItems.size - 1)

            map["offset"] = mOffset.toString()
            map["limit"] = PAGE_COUNT.toString()
            val financialItems = ApiService.apiClient!!.getFinancialSummary(map)
            frag.addNetworkRequest(financialItems)
            financialItems.enqueue(object : CustomCallback<Data<Count<FinancialSummary>>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        getSummary()
                    }
                }

                override fun onResponse(
                    call: Call<Data<Count<FinancialSummary>>>,
                    response: Response<Data<Count<FinancialSummary>>>
                ) {
                    if (response.body() != null) {
                        val newItems = response.body()!!.data!!.items
                        mOffset += PAGE_COUNT
                        val index = mItems.size - 1
                        val adapter = rv.adapter as EndLessLoadMoreAdapter<*, *>
                        mItems.removeAt(index)
                        adapter.notifyItemRemoved(index)
                        if (newItems.isNotEmpty()) {
                            mItems.addAll(newItems)
                            adapter.notifyItemRangeInserted(index, newItems.size)
                        } else {
                            adapter.isMoreDataAvailable = false
                            if (mItems.size == 0) {
                                frag.showEmptyState()
                            }
                        }
                        frag.hideRvProgress()
                        adapter.setLoading(false)
                    }
                }
            })
        }
    }


    override fun chargeAccount(paymentRequest: PaymentRequest) {
        ApiService.apiClient!!.chargeAccount(paymentRequest)
            .enqueue(object : CustomCallback<Data<com.online.coursestore.model.Response>> {
                override fun onStateChanged(): RetryListener? {
                    return RetryListener {
                        chargeAccount(paymentRequest)
                    }
                }

                override fun onResponse(
                    call: Call<Data<com.online.coursestore.model.Response>>,
                    response: retrofit2.Response<Data<com.online.coursestore.model.Response>>
                ) {
                    if (response.body() != null) {
                        frag.onCheckout(response.body()!!)
                    }
                }

            })
    }

}