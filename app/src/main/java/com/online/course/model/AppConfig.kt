package com.online.course.model

import com.google.gson.annotations.SerializedName

class AppConfig {
    @SerializedName("register_method")
    lateinit var registrationMethod: String

    @SerializedName("offline_bank_account")
    lateinit var offLineBankAccounts: List<String>

    @SerializedName("user_language")
    lateinit var supportedLangs: Map<String, String>

    @SerializedName("payment_channels")
    lateinit var activePaymentChannels: ActivePaymentChannels

    @SerializedName("minimum_payout_amount")
    var minimumPayoutAmount: Double = 0.0

    @SerializedName("currency")
    lateinit var currency: Currency
}