package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.FragFinancialPayoutBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.CommonRvAdapter
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.PayoutAccount
import com.online.coursestore.model.PayoutRes
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.FinancialPayoutPresenterImpl
import com.online.coursestore.ui.frag.abstract.EmptyState
import com.online.coursestore.ui.widget.PayoutRequestDialog

class FinancialPayoutFrag : NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragFinancialPayoutBinding
    private lateinit var mPresenter: Presenter.FinancialPayoutPresenter

    private lateinit var mPayoutAccount: PayoutAccount

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragFinancialPayoutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = FinancialPayoutPresenterImpl(this)
        mPresenter.getPayouts()
    }

    fun onPayoutsReceived(res: PayoutRes) {
        mPayoutAccount = res.payoutAccount
        mBinding.financialPayoutRvProgressBar.visibility = View.GONE
        if (res.payouts.isNotEmpty()) {
            mBinding.financialPayoutRv.adapter = CommonRvAdapter(res.payouts)
        } else {
            showEmptyState()
        }

        mBinding.financialPayoutAmountTv.text =
            Utils.formatPrice(requireContext(), res.payoutAccount.amonut, false)
        mBinding.financialPayoutRequestBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()
        bundle.putParcelable(App.PAYOUT_ACCOUT, mPayoutAccount)

        val dialog = PayoutRequestDialog()
        dialog.arguments = bundle
        dialog.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_payout, R.string.no_payout, R.string.no_payout_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.financialPayoutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}