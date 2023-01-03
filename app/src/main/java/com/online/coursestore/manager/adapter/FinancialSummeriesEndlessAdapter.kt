package com.online.coursestore.manager.adapter

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.R
import com.online.coursestore.databinding.ItemCommonBinding
import com.online.coursestore.model.CommonItem
import com.online.coursestore.ui.MainActivity

class FinancialSummeriesEndlessAdapter (items: List<CommonItem?>, private val activity: MainActivity) :
    EndLessLoadMoreAdapter<CommonItem, FinancialSummeriesEndlessAdapter.ViewHolder>(
        items,
        R.layout.item_loading_row
    ) {

    override fun getItemViewType(position: Int): Int {
        return if (items[position] != null) TYPE_ITEMS else TYPE_LOAD
    }

    override fun onCreateViewItem(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent!!.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewItem(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        if (viewHolder is FinancialSummeriesEndlessAdapter.ViewHolder){
            val item = items[position]
            val context = viewHolder.binding.root.context

            viewHolder.binding.itemCommonTitleTv.text = item.title(context)
            viewHolder.binding.itemCommonDescTv.text = item.desc(context)

            if (item.cardBg() != null) {
                viewHolder.binding.itemCommonCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        item.cardBg()!!
                    )
                )
            }

            if (item.showUnseenStatus()) {
                viewHolder.binding.itemCommonUnseenCircle.visibility = View.VISIBLE
            }

            if (item.img() != null) {
                Glide.with(context).load(item.img())
                    .into(viewHolder.binding.itemCommonImg)
            } else if (item.imgResource() != null) {
                viewHolder.binding.itemCommonImg.setImageResource(item.imgResource()!!)
            }

            val imgPadding = item.imgPadding(context)
            if (imgPadding != null) {
                viewHolder.binding.itemCommonImg.setPadding(
                    imgPadding,
                    imgPadding,
                    imgPadding,
                    imgPadding
                )
            }

            if (item.imgBgResource() != null) {
                viewHolder.binding.itemCommonImg.setBackgroundResource(item.imgBgResource()!!)
            } else if (item.imgBgDrawable(context) != null) {
                viewHolder.binding.itemCommonImg.background = item.imgBgDrawable(context)
            }

            if (!item.isClickable()) {
                viewHolder.binding.itemCommonCard.isFocusable = false
                viewHolder.binding.itemCommonCard.isClickable = false
                viewHolder.binding.itemCommonCard.foreground = null
            }

            if (item.status(context) != null) {
                val itemCommonStatusTv = viewHolder.binding.itemCommonStatusTv
                val status = item.status(context)!!
                itemCommonStatusTv.text = status.status
                itemCommonStatusTv.setTextColor(status.textColor)

                if (status.textBg != 0) {
                    itemCommonStatusTv.setBackgroundResource(status.textBg)
                }

                if (status.textSize != 0f) {
                    itemCommonStatusTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, status.textSize)
                }

                itemCommonStatusTv.visibility = View.VISIBLE
            }
        }
    }

    inner class ViewHolder(val binding: ItemCommonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun hideSatusCircle() {
            binding.itemCommonUnseenCircle.visibility = View.GONE
        }
    }


}