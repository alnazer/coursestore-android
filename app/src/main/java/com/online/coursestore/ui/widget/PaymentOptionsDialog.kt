package com.online.coursestore.ui.widget

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.google.gson.Gson
import com.myfatoorah.sdk.entity.executepayment.MFExecutePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentResponse
import com.myfatoorah.sdk.entity.paymentstatus.MFGetPaymentStatusResponse
import com.myfatoorah.sdk.utils.MFAPILanguage.AR
import com.myfatoorah.sdk.utils.MFCountry
import com.myfatoorah.sdk.utils.MFCurrencyISO
import com.myfatoorah.sdk.utils.MFEnvironment
import com.myfatoorah.sdk.views.MFResult
import com.myfatoorah.sdk.views.MFSDK
import com.myfatoorah.sdk.views.MFSDK.executePayment
import com.online.coursestore.R
import com.online.coursestore.databinding.DialogPaymentOptionsBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.PaymentOptionsAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.listener.PaymentOptionClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverBottomSheetDialog
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.PaymentPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.CartFrag
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.*
import com.paypal.checkout.order.Order

class PaymentOptionsDialog(amounts: Amounts,var courseID: Int?,mCouponValidation: CouponValidation?) : NetworkObserverBottomSheetDialog(),
    PaymentOptionClickListener, ItemCallback<CouponValidation> {

    private lateinit var mBinding: DialogPaymentOptionsBinding
    private lateinit var mPresenter: Presenter.PaymentPresenter
    private var amounts = amounts
    private var mCouponValidation = mCouponValidation
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var selectedPaymentChannel: PaymentChannel
    private var checkoutResponse: CheckoutResponse? = null
    private var paypalPaymentStatus: Boolean = false

    override fun onStart() {
        super.onStart()
        WidgetHelper.removeBottomSheetDialogHalfExpand(dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mBinding = DialogPaymentOptionsBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }


    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    private fun init() {
        mLoadingDialog = LoadingDialog.instance
        mBinding.courseAddCouponBtn.setOnClickListener {
            val dialog = CouponDialog(courseID)
            dialog.setOnCouponAdded(this)
            dialog.show(childFragmentManager, null)
        }
        if (courseID == null){
            mBinding.courseAddCouponBtn.visibility = View.GONE
        }
        initData()
        initPaymentRecycleView()
        mBinding.paymentOptionsCancelBtn.setOnClickListener {
            this.dismiss()
        }

        mBinding.paypalButton.setup(
            createOrder = CreateOrder { createOrderActions ->
                val order =
                    Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.USD, value = Utils.convertArabic(String.format("%.2f", amounts.total_usd))!!)
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove = OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
//                    ToastMaker.show(requireContext(), getString(R.string.successful_payment), getString(R.string.successful_payment_desc))
                    paypalPaymentStatus = true
                    checkoutOrder(createCheckout(null, true))
                }
            },
            onCancel = OnCancel {
                Log.v(tag, "OnCancel")
                Log.d(tag, "Buyer cancelled the checkout experience.")
                paypalPaymentStatus = false
                checkoutOrder(createCheckout(null, true))
            },
            onError = OnError { errorInfo ->
                Log.v("paypalError", "OnError")
                Log.d("paypalError", "Error details: $errorInfo")
//                ToastMaker.show(requireContext(), getString(R.string.failed_payment), getString(R.string.failed_payment_desc))
                paypalPaymentStatus = false
                checkoutOrder(createCheckout(null, true))
            }
        )

    }

    private fun initMyFatoorah(paymentChannel: PaymentChannel){
        MFSDK.init(App.MY_FATOORAH_API_KEY, MFCountry.KUWAIT, MFEnvironment.LIVE)
        val request = MFInitiatePaymentRequest(amounts.total_kd, MFCurrencyISO.KUWAIT_KWD)
        MFSDK.initiatePayment(
            request,
            AR
        ) { result: MFResult<MFInitiatePaymentResponse> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("MyFatoorahTest", "Response: " + Gson().toJson(result.response))
                    executePayment(paymentChannel)
                }
                is MFResult.Fail ->
                    Log.d("MyFatoorahTest", "Response: " + Gson().toJson(result.error))
            }
        }
    }

    private fun initData() {
        mPresenter = PaymentPresenterImpl(this)
    }

    fun initPaymentRecycleView() {
        mBinding.progressLoadingPaymentOptions.visibility = View.GONE
        val listOfPaymentChannels = ArrayList<PaymentChannel>()
        for (paymentChannel in App.appConfig.activePaymentChannels.paymentChannels){
            if(!paymentChannel.title.contains("paypal") && !paymentChannel.title.contains("Apple Pay")){
                listOfPaymentChannels.add(paymentChannel)
            }
        }
        val paymentOptionsAdapter = PaymentOptionsAdapter(listOfPaymentChannels, this)
        mBinding.paymentOptionsRecyvlerView.adapter = paymentOptionsAdapter
    }

    override fun onClick(paymentChannel: PaymentChannel) {
        selectedPaymentChannel = paymentChannel

        checkoutOrder(createCheckout(paymentChannel, false))
    }

    fun createCheckout(paymentChannel: PaymentChannel?, isPaypal: Boolean) : Checkout{
        val checkout = Checkout()
        checkout.return_type = "order"
        if (isPaypal){
            checkout.PaymentMethodId = ""
            checkout.gateway_id = "1"
        }else{
            checkout.PaymentMethodId = paymentChannel!!.paymentMethodID
            checkout.gateway_id = paymentChannel.id.toString()
        }
        if (courseID != null){
            checkout.webinar_id = courseID!!.toString()
        }else{
            checkout.webinar_id = ""
        }
        if (mCouponValidation != null){
            checkout.discount_id = mCouponValidation!!.coupon.discountId.toString()
        }else{
            checkout.discount_id = ""
        }
        return checkout
    }

    private fun executePayment(paymentChannel: PaymentChannel) {
        val request2 = MFExecutePaymentRequest(Integer.parseInt(paymentChannel.paymentMethodID!!), amounts.total_kd)
        executePayment(
            requireActivity(),
            request2,
            AR,
            onInvoiceCreated = {
                Log.d("MyFatoorahTest", "invoiceId: $it")
            }
        ) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("MyFatoorahTest", "Response: " + Gson().toJson(result.response))
//                    ToastMaker.show(requireContext(), getString(R.string.successful_payment), getString(R.string.successful_payment_desc))
                    verifyPayment(App.VERIFY_PAYMENT_SUCCESS)
                }
                is MFResult.Fail -> {
                    Log.d("MyFatoorahTest", "Fail: " + Gson().toJson(result.error))
//                    ToastMaker.show(requireContext(), getString(R.string.failed_payment), getString(R.string.failed_payment_desc))
                    verifyPayment(App.VERIFY_PAYMENT_FAILED)
                }
            }
        }
    }

    fun checkoutOrder(checkout: Checkout){
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)
        mPresenter.checkout(checkout)
    }

    fun onCheckout(data: Data<CheckoutResponse>) {
        mLoadingDialog.dismiss()
        if (data.isSuccessful) {
            checkoutResponse = data.data!!
            if (checkoutResponse!!.payment_data == "Paypal"){
                if (paypalPaymentStatus){
                    verifyPayment(App.VERIFY_PAYMENT_SUCCESS)
                }else{
                    verifyPayment(App.VERIFY_PAYMENT_FAILED)
                }
            }else{
                initMyFatoorah(selectedPaymentChannel)
            }
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun verifyPayment(status: String){
        val verifyPayment = VerifyPayment()
        verifyPayment.status = status
        verifyPayment.orderId = checkoutResponse!!.id
        mPresenter.verifyPayment(verifyPayment)
    }

    fun onVerifyPaymentResponse(data: Data<VerifyPaymentResponse>){
        mLoadingDialog.dismiss()
        if (data.isSuccessful) {
            if (data.data!!.status!!){
                actionPaymentSuccess()
            }else{
                ToastMaker.show(requireContext(), getString(R.string.failed_payment), getString(R.string.failed_payment_desc))
            }
        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                data.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun actionPaymentSuccess(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.putExtra(App.PAYMENT_SUCCESS, true)
        requireActivity().finish()
        startActivity(intent)
    }

    override fun onItem(item: CouponValidation, vararg args: Any) {
        mCouponValidation = item
        amounts = item.amounts
        ToastMaker.show(requireContext(), getString(R.string.coupon_added),"")
    }
}