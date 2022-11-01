package com.online.coursestore.ui.widget

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
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
import com.online.coursestore.manager.listener.PaymentOptionClickListener
import com.online.coursestore.model.CartItem
import com.online.coursestore.model.PaymentOption
import com.online.coursestore.model.view.PaymentRedirection
import com.online.coursestore.presenterImpl.CartPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.PaymentStatusActivity
import com.online.coursestore.ui.frag.CartFrag
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.*

class PaymentOptionsDialog(price: String, cartItems: List<CartItem>) : BottomSheetDialogFragment(),
    PaymentOptionClickListener {

    private lateinit var mBinding: DialogPaymentOptionsBinding
    private val price: Double = Utils.getPriceAsDouble(price)
    private val cartItems = cartItems

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

    private fun init() {
        MFSDK.init(App.MY_FATOORAH_API_KEY_TEST, MFCountry.KUWAIT, MFEnvironment.TEST)
        val request = MFInitiatePaymentRequest(price, MFCurrencyISO.Egyptian_Pound_EGP)
        MFSDK.initiatePayment(
            request,
            AR
        ) { result: MFResult<MFInitiatePaymentResponse> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("MyFatoorahTest", "Response: " + Gson().toJson(result.response))
                    initPaymentRecycleView()
                }
                is MFResult.Fail ->
                    Log.d("MyFatoorahTest", "Response: " + Gson().toJson(result.error))
            }
        }

        mBinding.paymentOptionsCancelBtn.setOnClickListener {
            this.dismiss()
        }

        mBinding.paypalButton.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.USD, value = price.toString())
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.i("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                    ToastMaker.show(requireContext(), getString(R.string.successful_payment), getString(R.string.successful_payment_desc))
//                    for (i in cartItems.indices) {
//                        (parentFragment as CartFrag).removeItem(cartItems[i].id, i)
//                    }
//                    this.dismiss()
//                    requireActivity().onBackPressed()
                }
            },
            onError = OnError { errorInfo ->
                Log.v("paypalError", "OnError")
                Log.d("paypalError", "Error details: $errorInfo")
                ToastMaker.show(requireContext(), getString(R.string.failed_payment), getString(R.string.failed_payment_desc))
            }
        )

    }

    fun initPaymentRecycleView() {
        mBinding.progressLoadingPaymentOptions.visibility = View.GONE
        var knet = PaymentOption()
        knet.img = R.drawable.knet
        knet.name = App.KNET

        var visaMaster = PaymentOption()
        visaMaster.img = R.drawable.vm
        visaMaster.name = App.VISA_MASTER

        var saudiCreditCard = PaymentOption()
        saudiCreditCard.img = R.drawable.vm
        saudiCreditCard.name = App.SAUDI_CREDIT_CARD

        var debitCardsUAE = PaymentOption()
        debitCardsUAE.img = R.drawable.vm
        debitCardsUAE.name = App.DEBIT_CARDS_UAE

        var qatarDebitCards = PaymentOption()
        qatarDebitCards.img = R.drawable.np
        qatarDebitCards.name = App.QATAR_DEBIT_CARD


        var listOfPaymentOptions: List<PaymentOption> =
            listOf(knet, visaMaster, saudiCreditCard, debitCardsUAE, qatarDebitCards)
        var paymentOptionsAdapter = PaymentOptionsAdapter(listOfPaymentOptions, this)
        mBinding.paymentOptionsRecyvlerView.adapter = paymentOptionsAdapter
    }

    override fun onClick(name: String) {
//        ToastMaker.show(requireContext(), name, "Clicked")
        when (name) {
            App.KNET -> {
                executePayment(App.KNET_ID)
            }
            App.SAUDI_CREDIT_CARD -> {
                executePayment(App.SAUDI_CREDIT_CARD_ID)
            }
            App.DEBIT_CARDS_UAE -> {
                executePayment(App.DEBIT_CARDS_UAE_ID)
            }
            App.QATAR_DEBIT_CARD -> {
                executePayment(App.QATAR_DEBIT_CARD_ID)
            }
            App.VISA_MASTER -> {
                executePayment(App.VISA_MASTER_ID)
            }
        }
    }

    private fun executePayment(paymentType: Int) {
        val request2 = MFExecutePaymentRequest(paymentType, price)
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
                    ToastMaker.show(requireContext(), getString(R.string.successful_payment), getString(R.string.successful_payment_desc))
//                    for (i in cartItems.indices) {
//                        (parentFragment as CartFrag).removeItem(cartItems[i].id, i)
//                    }
//                    this.dismiss()
//                    requireActivity().onBackPressed()

                }
                is MFResult.Fail -> {
                    Log.d("MyFatoorahTest", "Fail: " + Gson().toJson(result.error))
                    ToastMaker.show(requireContext(), getString(R.string.failed_payment), getString(R.string.failed_payment_desc))
                }
            }
        }
    }

}