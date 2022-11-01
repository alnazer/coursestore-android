package com.online.coursestore.manager.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.online.coursestore.R
import com.online.coursestore.databinding.ItemDashboardGeneralBinding
import com.online.coursestore.model.MenuItem
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.RewardPointsFrag

class DasboardGeneralSliderAdapter(items: List<MenuItem>, private val activity: MainActivity) :
    BaseArrayAdapter<MenuItem, DasboardGeneralSliderAdapter.ViewHolder>(items),
    View.OnClickListener {

    enum class Type(val value: Int) {
        BALANCE(1),
        BADGE(2),
        POINTS(3);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDashboardGeneralBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val binding = holder.binding

        binding.itemDashboardGeneralTitleTv.text = item.title
        binding.itemDashboardGeneralDescTv.text = item.desc
        if (item.isClickable) {
            binding.itemDashboardGeneralMainContainer.setOnClickListener(this)
            binding.itemDashboardGeneralMainContainer.isFocusable = true
            binding.itemDashboardGeneralMainContainer.isClickable = true
            binding.itemDashboardGeneralMainContainer.foreground =
                ContextCompat.getDrawable(
                    holder.itemView.context,
                    R.drawable.ripple_effect_corner_20
                )
        }

        when (item.type) {
            Type.BALANCE.value -> {
                binding.itemDashboardGeneralImg.setBackgroundResource(R.drawable.round_view_accent_corner20)
                binding.itemDashboardGeneralImg.setImageResource(R.drawable.ic_wallet_white)
            }

            Type.BADGE.value -> {
                binding.itemDashboardGeneralImg.setBackgroundResource(R.drawable.round_view_gold_corner20)
                binding.itemDashboardGeneralProgressIndicator.progress = item.progress
                binding.itemDashboardGeneralProgressIndicatorTv.text = ("${item.progress}%")

                binding.itemDashboardGeneralProgressIndicator.visibility = View.VISIBLE
                binding.itemDashboardGeneralProgressIndicatorTv.visibility = View.VISIBLE
            }

            Type.POINTS.value -> {
                binding.itemDashboardGeneralImg.setBackgroundResource(R.drawable.round_view_orange_corner20)
                binding.itemDashboardGeneralImg.setImageResource(R.drawable.ic_gift)
            }
        }
    }

    inner class ViewHolder(val binding: ItemDashboardGeneralBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onClick(v: View?) {
        activity.transact(RewardPointsFrag())
    }
}