package com.online.coursestore.manager.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.R
import com.online.coursestore.databinding.ItemMyClassesBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.model.Course
import kotlin.math.roundToInt

class MyClassesRvAdapter(items: List<Course>, private val purchased: Boolean = false) :
    BaseArrayAdapter<Course, MyClassesRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyClassesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val course = items[position]
        val context = viewholder.itemView.context

        viewholder.binding.itemMyClassesTimeDurationTv.text =
            Utils.getDuration(context, course.duration)

        if (purchased && course.progress != null) {
            if (course.progress!! > 50) {
                viewholder.binding.itemMyClassesProgressBar.progressTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.accent)
                    )
            }

            viewholder.binding.itemMyClassesProgressBar.progress = course.progress!!.roundToInt()
            viewholder.binding.itemMyClassesProgressBar.visibility = View.VISIBLE
        } else {
            viewholder.binding.itemMyClassesProgressBar.visibility = View.GONE
        }


        if (course.img != null) {
            Glide.with(viewholder.itemView.context).load(course.img)
                .into(viewholder.binding.itemMyClassesImg)
        }
        viewholder.binding.itemMyClassesTitleTv.text = course.title
        viewholder.binding.itemMyClassesRatingBar.rating = course.rating
        viewholder.binding.itemMyClassesCategoryTv.text = course.category

        if (course.priceWithDiscount == 0.0) {
            viewholder.binding.itemFavoriteCoursePriceTv.text = context.getString(R.string.free)
        } else {
            viewholder.binding.itemFavoriteCoursePriceTv.text =
                ("${App.appConfig.currency.sign}${course.priceWithDiscount}")
        }

        if (purchased){
            viewholder.binding.itemFavoriteCoursePriceTv.visibility = View.INVISIBLE
        }

        if (course.isLive()) {
            viewholder.binding.itemMyClassesDateTitleTv.text =
                context.getString(R.string.start_date)
            viewholder.binding.itemMyClassesDateTv.text =
                Utils.getDateFromTimestamp(course.startDate)
            viewholder.binding.itemMyClassesProgressBar.max = 100
            viewholder.binding.itemMyClassesProgressBar.progress = course.progress?.toInt()!!
        } else {
            viewholder.binding.itemMyClassesDateTitleTv.text =
                context.getString(R.string.publish_date)
            viewholder.binding.itemMyClassesDateTv.text =
                Utils.getDateFromTimestamp(course.createdAt)
        }

    }

    class ViewHolder(val binding: ItemMyClassesBinding) : RecyclerView.ViewHolder(binding.root)
}