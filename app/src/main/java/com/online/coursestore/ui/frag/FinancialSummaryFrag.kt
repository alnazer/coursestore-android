package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.FragFinancialSummaryBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.CommonRvAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.*
import com.online.coursestore.model.view.PaymentRedirection
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.CommonApiPresenterImpl
import com.online.coursestore.presenterImpl.FinancialSummaryPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.PaymentStatusActivity
import com.online.coursestore.ui.frag.abstract.EmptyState

class FinancialSummaryFrag : NetworkObserverFragment(), View.OnClickListener,
    ItemCallback<QuickInfo>, EmptyState {

    private lateinit var mBinding: FragFinancialSummaryBinding
    private lateinit var mPresenter: Presenter.FinancialSummaryPresenter
    private lateinit var mCommonPresenter: Presenter.CommonApiPresenter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragFinancialSummaryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.financialSummaryEmptyState.root.visibility = View.INVISIBLE

        mCommonPresenter = CommonApiPresenterImpl.getInstance()
        mCommonPresenter.getQuickInfo(this)

        mPresenter = FinancialSummaryPresenterImpl(this)
        mPresenter.getSummary()

        mBinding.financialSummaryChargeBtn.setOnClickListener(this)
    }

    fun onSummariesReceived(summaries: List<FinancialSummary>) {
        mBinding.financialSummaryRvProgressBar.visibility = View.INVISIBLE
        if (summaries.isNotEmpty()) {
            mBinding.financialSummaryRv.adapter = CommonRvAdapter(summaries)
        } else {
            showEmptyState()
        }
    }

    override fun onClick(v: View?) {
        val redirection = PaymentRedirection()
        redirection.isNavDrawer = true
        redirection.position = MainActivity.SlideMenuItem.FINANCIAL.value()
        redirection.buttonTitle = getString(R.string.financial)

        PaymentStatusActivity.paymentRedirection = redirection

        mPresenter.chargeAccount(PaymentRequest())
//        val dialog = ChargeDialog()
//        dialog.show(childFragmentManager, null)


//        val bundle = Bundle()
//        bundle.putParcelable(App.REDIRECTION, redirection)
//
//        val chargeAccountFrag = ChargeAccountPaymentFrag()
//        chargeAccountFrag.arguments = bundle
//
//        (activity as MainActivity).transact(chargeAccountFrag)
    }

    override fun onItem(quickInfo: QuickInfo, vararg args: Any) {
        if (context == null) return

        App.quickInfo = quickInfo
        mBinding.financialSummaryCreditTv.text =
            Utils.formatPrice(requireContext(), quickInfo.balance, false)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_balance, R.string.no_balance_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialSummaryEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onCheckout(data: Data<Response>) {
        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
            Utils.openLink(requireContext(), data.data!!.link)
        } else {
            if (context == null) return
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }
}