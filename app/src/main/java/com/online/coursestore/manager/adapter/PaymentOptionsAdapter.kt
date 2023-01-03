package com.online.coursestore.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.databinding.ItemPaymentOptionBinding
import com.online.coursestore.manager.listener.PaymentOptionClickListener
import com.online.coursestore.model.PaymentChannel

class PaymentOptionsAdapter(paymentOptions: List<PaymentChannel>,
                            val paymentOptionClickListener: PaymentOptionClickListener
) :
    BaseArrayAdapter<PaymentChannel, PaymentOptionsAdapter.ViewHolder>(paymentOptions){

    val paymentoptions = paymentOptions

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
        Glide.with(holder.itemView.context).load(paymentOption.image).into(holder.binding.paymentOptionImageItem)
        holder.binding.textPaymentOption.text = paymentOption.title
        holder.itemView.setOnClickListener {
            paymentOptionClickListener.onClick(paymentOption)
        }
    }

    inner class ViewHolder(val binding: ItemPaymentOptionBinding) :
        RecyclerView.ViewHolder(binding.root)


}