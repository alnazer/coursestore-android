package com.online.coursestore.manager.listener

import com.online.coursestore.model.PaymentChannel

interface PaymentOptionClickListener {
    fun onClick(paymentChannel:PaymentChannel)
}