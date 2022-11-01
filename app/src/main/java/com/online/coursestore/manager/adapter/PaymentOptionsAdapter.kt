package com.online.coursestore.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.databinding.ItemPaymentOptionBinding
import com.online.coursestore.manager.listener.PaymentOptionClickListener
import com.online.coursestore.model.PaymentOption

class PaymentOptionsAdapter(paymentOptions: List<PaymentOption>, paymentOptionClickListener: PaymentOptionClickListener) :
    BaseArrayAdapter<PaymentOption, PaymentOptionsAdapter.ViewHolder>(paymentOptions){

    val paymentoptions = paymentOptions
    val paymentOptionClickListener = paymentOptionClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionsAdapter.ViewHolder {
        return ViewHolder(
            ItemPaymentOptionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paymentOption =  paymentoptions[position]
        Glide.with(holder.itemView.context).load(paymentOption.img).into(holder.binding.paymentOptionImageItem)
        holder.binding.textPaymentOption.text = paymentOption.name
        holder.itemView.setOnClickListener {
            paymentOptionClickListener.onClick(paymentOption.name!!)
        }
    }

    inner class ViewHolder(val binding: ItemPaymentOptionBinding) :
        RecyclerView.ViewHolder(binding.root)


}