package com.online.coursestore.manager.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.coursestore.databinding.ItemCourseBinding
import com.online.coursestore.databinding.ItemSoonBinding
import com.online.coursestore.model.SoonCourse
import com.online.coursestore.ui.MainActivity

class HomeSoonAdapter(items: List<SoonCourse>, private val activity: MainActivity) :
    BaseArrayAdapter<SoonCourse, HomeSoonAdapter.ViewHolder>(items) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSoonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val course = items[position]
        val context = viewholder.itemView.context

        if (course.img != null) {
            Glide.with(context).load(course.img)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        viewholder.binding.itemSoonImgOverlay.visibility = View.VISIBLE
                        return false
                    }

                }).into(viewholder.binding.itemSoonImg)
        }

        viewholder.binding.itemSoonTitleTv.text = course.title
//        viewholder.binding.itemSoonInstructorTv.text = course.instructor
    }

    inner class ViewHolder(val binding: ItemSoonBinding) :
        RecyclerView.ViewHolder(binding.root){

    }
}