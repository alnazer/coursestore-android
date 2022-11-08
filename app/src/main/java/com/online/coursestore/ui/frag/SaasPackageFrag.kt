package com.online.coursestore.ui.frag

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.FragSaasPackageBinding
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.SaasPackageAdapter
import com.online.coursestore.manager.adapter.TopDashboardRvAdapter
import com.online.coursestore.manager.listener.TopRvClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.*
import com.online.coursestore.model.view.PaymentRedirection
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.SaasPackagePresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.PaymentStatusActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import com.online.coursestore.ui.widget.LoadingDialog

class SaasPackageFrag : NetworkObserverFragment(), EmptyState , TopRvClickListener{

    private lateinit var mBinding: FragSaasPackageBinding
    private lateinit var mPresenter: Presenter.SaasPackagePresenter
    private lateinit var mLoadingDialog: LoadingDialog

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.saasPackageEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this@SaasPackageFrag
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSaasPackageBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = SaasPackagePresenterImpl(this)
        mPresenter.getSaasPackages()
    }


    fun onCheckout(data: Data<Response>) {
        mLoadingDialog.dismiss()

        if (data.isSuccessful && !data.data!!.link.isNullOrEmpty()) {
            val redirection = PaymentRedirection()
            redirection.isNavDrawer = true
            redirection.position = MainActivity.SlideMenuItem.SUBSCRIPTION.value()
            redirection.buttonTitle = getString(R.string.my_package)

            PaymentStatusActivity.paymentRedirection = redirection

            Utils.openLink(requireContext(), data.data!!.link)
//            val bundle = Bundle()
//            bundle.putParcelable(App.ORDER, data.data)
//

//            bundle.putParcelable(App.REDIRECTION, redirection)
//
//            val paymentFrag = ChargeAccountPaymentFrag()
//            paymentFrag.arguments = bundle
//            (activity as MainActivity).transact(paymentFrag)
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    fun onSaasPackageReceived(sassPackage: SaasPackage) {
        mBinding.saasPackageActivePlanTv.text = sassPackage.activePackage.title
        mBinding.saasPackageActivationDateTv.text =
            Utils.getDateFromTimestamp(sassPackage.activePackage.activationDate)
        mBinding.saasPackageRemainedDaysTv.text = sassPackage.activePackage.daysRemained

        if (sassPackage.packages.isEmpty()) {
            showEmptyState(
                R.drawable.no_subscription,
                getString(R.string.no_package),
                getString(R.string.no_package_desc)
            )

            mBinding.saasPackagePlanTv.visibility = View.GONE
        }

        addAccountStatistics(sassPackage)

        mBinding.saasPackageViewPager.adapter = SaasPackageAdapter(sassPackage.packages, this)
        mBinding.saasPackageIndicator.setViewPager2(mBinding.saasPackageViewPager)
    }

    private fun addAccountStatistics(sassPackage: SaasPackage) {
        val items = ArrayList<CommonItem>()

        if (!sassPackage.accountCoursesCount.isNullOrEmpty()) {
            items.add(object : CommonItem {
                override fun title(context: Context): String {
                    return sassPackage.accountCoursesCount!!
                }

                override fun img(): String? {
                    return null
                }

                override fun desc(context: Context): String {
                    return getString(R.string.new_courses)
                }

                override fun imgResource(): Int {
                    return R.drawable.ic_play_green
                }

                override fun imgBgResource(): Int {
                    return R.drawable.circle_light_green
                }
            })
        }

        if (!sassPackage.accountCoursesCapacity.isNullOrEmpty()) {
            items.add(object : CommonItem {
                override fun title(context: Context): String {
                    return sassPackage.accountCoursesCapacity!!
                }

                override fun img(): String? {
                    return null
                }

                override fun desc(context: Context): String {
                    return getString(R.string.live_class_capacity)
                }

                override fun imgResource(): Int {
                    return R.drawable.ic_video_red
                }

                override fun imgBgResource(): Int {
                    return R.drawable.circle_light_red
                }
            })
        }

        if (!sassPackage.accountMeetingCount.isNullOrEmpty()) {
            items.add(object : CommonItem {
                override fun title(context: Context): String {
                    return sassPackage.accountMeetingCount!!
                }

                override fun img(): String? {
                    return null
                }

                override fun desc(context: Context): String {
                    return getString(R.string.meeting_time_slots)
                }

                override fun imgResource(): Int {
                    return R.drawable.ic_time_blue
                }

                override fun imgBgResource(): Int {
                    return R.drawable.circle_light_blue
                }
            })
        }


        if (!sassPackage.accountStudentsCount.isNullOrEmpty()) {
            items.add(object : CommonItem {
                override fun title(context: Context): String {
                    return sassPackage.accountStudentsCount!!
                }

                override fun img(): String? {
                    return null
                }

                override fun desc(context: Context): String {
                    return getString(R.string.students)
                }

                override fun imgResource(): Int {
                    return R.drawable.ic_user_green
                }

                override fun imgBgResource(): Int {
                    return R.drawable.circle_light_green2
                }
            })
        }


        if (!sassPackage.accountInstructorsCount.isNullOrEmpty()) {
            items.add(object : CommonItem {
                override fun title(context: Context): String {
                    return sassPackage.accountInstructorsCount!!
                }

                override fun img(): String? {
                    return null
                }

                override fun desc(context: Context): String {
                    return getString(R.string.instructors)
                }

                override fun imgResource(): Int {
                    return R.drawable.ic_profile_dark_gray
                }

                override fun imgBgResource(): Int {
                    return R.drawable.circle_light_dark_gray
                }
            })
        }

        mBinding.saasPackageAccountStatisticsRv.adapter = TopDashboardRvAdapter(items, this)
    }

    fun onItemSelected(saasPackageItem: SaasPackageItem) {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        saasPackageItem.packageId = saasPackageItem.id

        mPresenter.checkoutSubscription(saasPackageItem)
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                (requireActivity() as MainActivity).transact(MyClassesTabFrag())
            }
            1 -> {
                (requireActivity() as MainActivity).transact(SupportTabFrag())
            }
            2 -> {
                (requireActivity() as MainActivity).transact(MeetingsTabFrag())
            }
            3 -> {
                (requireActivity() as MainActivity).transact(CommentsFrag())
            }
        }
    }
}