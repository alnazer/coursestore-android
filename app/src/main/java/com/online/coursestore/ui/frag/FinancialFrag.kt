package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.TabBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.ViewPagerAdapter
import com.online.coursestore.model.ToolbarOptions
import com.online.coursestore.ui.MainActivity

class FinancialFrag : Fragment() {

    private lateinit var mBinding: TabBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = TabBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.tabContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        val tabLayout = mBinding.tabLayout
        val viewPager = mBinding.viewPager

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.financial)

        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(FinancialSummaryFrag(), getString(R.string.summary))
        adapter.add(FinancialOfflinePaymentsFrag(), getString(R.string.offline_payments))

        if (App.loggedInUser!!.isInstructor() || App.loggedInUser!!.isOrganizaton()) {
            adapter.add(FinancialSalesFrag(), getString(R.string.sales))
            adapter.add(FinancialPayoutFrag(), getString(R.string.payout))
        }

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}