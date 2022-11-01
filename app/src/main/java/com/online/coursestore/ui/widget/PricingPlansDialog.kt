package com.online.coursestore.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.online.coursestore.R
import com.online.coursestore.databinding.DialogPricingPlansBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.adapter.PricingPlanRvAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.coursestore.model.AddToCart
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Course
import com.online.coursestore.model.PricingPlan
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.CommonApiPresenterImpl
import com.online.coursestore.presenterImpl.PricingPlansPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.CourseDetailsFrag
import java.lang.StringBuilder
import java.util.ArrayList

class PricingPlansDialog : NetworkObserverBottomSheetDialog(), View.OnClickListener,
    ItemCallback<PricingPlan> {

    private lateinit var mBinding: DialogPricingPlansBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: Presenter.PricingPlansPresenter

    private val mResCallback = object : ItemCallback<BaseResponse> {
        override fun onItem(res: BaseResponse, vararg args: Any) {
            if (context == null) return

            if (res.isSuccessful) {
                (activity as MainActivity).updateCart()
                dismiss()
            } else {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.error),
                    res.message,
                    ToastMaker.Type.ERROR
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogPricingPlansBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCourse = requireArguments().getParcelable(App.COURSE)!!

        val plans = ArrayList<PricingPlan>()

        if (mCourse.canBuyWithPoints) {
            mPresenter = PricingPlansPresenterImpl(this)

            val pointsPlan = PricingPlan()
            pointsPlan.title = getString(R.string.buy_with_points)
            pointsPlan.description =
                StringBuilder(getString(R.string.you_have)).append(" ")
                    .append(App.quickInfo!!.availablePoints).append(" ")
                    .append(getString(R.string.reward_points_lower)).append(".").toString()

            pointsPlan.price = mCourse.points!!.toDouble()
            pointsPlan.isPointsItem = true
            plans.add(pointsPlan)
        }

        plans.addAll(mCourse.pricingPlans)

        val adapter = PricingPlanRvAdapter(plans, mCourse.points!!, mBinding.pricingPlanRv)
        adapter.setOnItemSelectedListener(this)
        mBinding.pricingPlanRv.adapter = adapter

        mBinding.pricingPlanCancelBtn.setOnClickListener(this)
        mBinding.pricingPlanAddToCartBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.pricing_plan_cancel_btn -> {
                dismiss()
            }

            R.id.pricing_plan_add_to_cart_btn -> {
                val plan =
                    (mBinding.pricingPlanRv.adapter as PricingPlanRvAdapter).getSelectedItem()

                if (plan != null) {
                    if (plan.isPointsItem) {
                        mPresenter.purchaseWithPoints(mCourse.id)
                    } else {
                        val addToCart = AddToCart()
                        addToCart.pricingPlanId = plan.id
                        addToCart.webinarId = mCourse.id

                        val presenter = CommonApiPresenterImpl.getInstance()
                        presenter.addToCart(addToCart, mResCallback)
                    }
                }
            }
        }
    }

    override fun onItem(plan: PricingPlan, vararg args: Any) {
        mBinding.pricingPlanAddToCartBtn.isEnabled = true
        if (plan.isPointsItem) {
            mBinding.pricingPlanAddToCartBtn.text = getString(R.string.purchase)
        } else {
            mBinding.pricingPlanAddToCartBtn.text = getString(R.string.add_to_cart)
        }
    }

    fun onPurchase(response: BaseResponse) {
        if (response.isSuccessful) {
            (parentFragment as CourseDetailsFrag).refreshPage()
            dismiss()
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

}