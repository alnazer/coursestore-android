package com.online.coursestore.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.databinding.ItemMyCommentBinding
import com.online.coursestore.manager.Utils
import com.online.coursestore.model.Comment

class MyCommentRvAdapter(comments: List<Comment>) :
    BaseArrayAdapter<Comment, MyCommentRvAdapter.ViewHolder>(comments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMyCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val comment = items[position]
        viewholder.binding.itemMyCommentDateTimeTv.text =
            Utils.getDateTimeFromTimestamp(comment.createdAt)

        val img: String?

        if (comment.blog != null) {
            viewholder.binding.itemMyCommentTitleTv.text = comment.blog!!.title
            img = comment.blog!!.image
        } else {
            viewholder.binding.itemMyCommentTitleTv.text = comment.course!!.title
            img = comment.course!!.img
        }

        if (img != null) {
            Glide.with(viewholder.itemView.context).load(img)
                .into(viewholder.binding.itemMyCommentImg)
        }

    }

    class ViewHolder(val binding: ItemMyCommentBinding) :
        RecyclerView.ViewHolder(binding.root)
}