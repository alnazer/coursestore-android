package com.online.coursestore.manager.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.online.coursestore.databinding.ItemTopDashboardRvBinding
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.listener.TopRvClickListener
import com.online.coursestore.model.CommonItem
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.MyClassesTabFrag
import kotlin.math.log

class TopDashboardRvAdapter(items: List<CommonItem>, topRvClickListener: TopRvClickListener) :
    BaseArrayAdapter<CommonItem, TopDashboardRvAdapter.ViewHolder>(items) {

    val topRvClickListener = topRvClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTopDashboardRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.binding.root.context

        if (position == 0) {
            val layoutParams =
                holder.binding.itemDashboardTopCard.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginStart = Utils.changeDpToPx(context, 16f).toInt()
            holder.binding.itemDashboardTopCard.requestLayout()
        }else if (position == itemCount - 1) {
            val layoutParams =
                holder.binding.itemDashboardTopCard.layoutParams as LinearLayout.LayoutParams
            layoutParams.marginEnd = Utils.changeDpToPx(context, 16f).toInt()
            holder.binding.itemDashboardTopCard.requestLayout()
        }
        Log.d("item", item.desc(context))
        holder.binding.itemDashboardTopImg.setImageResource(item.imgResource()!!)
        holder.binding.itemDashboardTopImg.setBackgroundResource(item.imgBgResource()!!)
        holder.binding.itemDashboardTopTitleTv.text = item.title(context)
        holder.binding.itemDashboardTopDescTv.text = item.desc(context)
        holder.itemView.setOnClickListener {
            topRvClickListener.onItemClick(position)
        }
    }

    inner class ViewHolder(val binding: ItemTopDashboardRvBinding) :
        RecyclerView.ViewHolder(binding.root)
}
