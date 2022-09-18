package com.online.course.manager.adapter

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.ItemCartMeetingBinding
import com.online.course.databinding.ItemFavoriteBinding
import com.online.course.manager.App
import com.online.course.manager.Utils
import com.online.course.model.CartItem
import com.online.course.ui.frag.CartFrag
import com.online.course.ui.widget.AppDialog

class CartRvAdapter(items: List<CartItem>, private val frag: CartFrag) :
    BaseArrayAdapter<CartItem, RecyclerView.ViewHolder>(items) {

    companion object {
        const val TYPE_MEETING = 1
        const val TYPE_COURSE = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].course != null) TYPE_COURSE else TYPE_MEETING
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_MEETING) {
            return CartMeetingViewHolder(
                ItemCartMeetingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return CartCourseViewHolder(
                ItemFavoriteBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(viewholder: RecyclerView.ViewHolder, position: Int) {
        var course = items[position].course
        var meeting = items[position].meeting
        val context = viewholder.itemView.context

        if (viewholder is CartCourseViewHolder) {
            course = course!!
            if (course.discount > 0) {
                viewholder.binding.itemFavoriteCourseDiscountPctTv.text =
                    (course.discount.toString() + "%" + context.getString(R.string.off))

                viewholder.binding.itemFavoriteCoursePriceTv.text =
                    Utils.formatPrice(context, course.priceWithDiscount)

                viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.text =
                    Utils.formatPrice(context, course.price)

                if (course.price > 0) {
                    viewholder.binding.itemFavoriteCoursePriceWithDiscountTv.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG
                }

            } else {
                viewholder.binding.itemFavoriteCoursePriceTv.text =
                    Utils.formatPrice(context, course.price)
            }

            if (course.img != null) {
                Glide.with(viewholder.itemView.context).load(course.img)
                    .into(viewholder.binding.itemFavoriteCourseImg)
            }
            viewholder.binding.itemFavoriteCourseTitleTv.text = course.title
            viewholder.binding.itemFavoriteCourseRatingBar.rating = course.rating
            viewholder.binding.itemFavoriteCourseDateTv.text =
                Utils.getDateFromTimestamp(course.createdAt)
        } else if (viewholder is CartMeetingViewHolder) {
            meeting = meeting!!

            if (meeting.user.avatar != null) {
                Glide.with(viewholder.itemView.context).load(meeting.user.avatar)
                    .into(viewholder.binding.itemCartMeetingImg)
            }

            val date = Utils.getDateFromTimestamp(meeting.date, "yyyy-MM-dd")

            val startTime = Utils.getCurrentDateTime(
                "$date ${meeting.time.start}",
                meeting.meeting?.timeZone,
                "yyyy-MM-dd HH:mma",
                "yyyy-MM-dd HH:mma"
            )!!
            val endTime = Utils.getCurrentDateTime(
                "$date ${meeting.time.end}",
                meeting.meeting?.timeZone,
                "yyyy-MM-dd HH:mma",
                "yyyy-MM-dd HH:mma"
            )!![1]

            val descBuilder = StringBuilder(context.getString(R.string.meeting_reservation))
                .append("\n").append(startTime[0]).append(" | ")
                .append(startTime[1]).append("-").append(endTime)/*.append(" (")
                .append(meeting.meeting?.timeZone).append(")")*/

            viewholder.binding.itemCartMeetingReservationDesc.text = descBuilder.toString()
            viewholder.binding.itemCartMeetingNameTv.text = meeting.user.name
            viewholder.binding.itemCartMeetingDateTv.text = Utils.getDateFromTimestamp(meeting.date)

            viewholder.binding.itemCartMeetingPriceTv.text =
                Utils.formatPrice(context, meeting.amount)
        }
    }

    inner class CartCourseViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, View.OnLongClickListener {

        init {
            binding.itemFavoriteCourseContainer.setOnLongClickListener(this)
            binding.itemFavoriteRemoveContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            removeItem(itemView.context, items[bindingAdapterPosition], bindingAdapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            binding.itemFavoriteSwipeLayout.open()
            return true
        }
    }


    inner class CartMeetingViewHolder(val binding: ItemCartMeetingBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.itemCartMeetingContainer.setOnLongClickListener(this)
            binding.itemCartMeetingRemoveContainer.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            removeItem(itemView.context, items[bindingAdapterPosition], bindingAdapterPosition)
        }

        override fun onLongClick(v: View?): Boolean {
            binding.itemCartMeetingSwipeLayout.open()
            return true
        }
    }

    private fun removeItem(context: Context, cartItem: CartItem, position: Int) {
        val dialog = AppDialog.instance
        val bundle = Bundle()

        bundle.putString(App.TITLE, context.getString(R.string.delete))
        bundle.putString(App.TEXT, context.getString(R.string.delete_from_fav))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                }

                override fun onOk() {
                    frag.removeItem(cartItem.id, position)
                }
            })

        dialog.show(frag.childFragmentManager, null)
    }
}