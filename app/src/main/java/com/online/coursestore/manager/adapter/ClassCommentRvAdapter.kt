package com.online.coursestore.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.databinding.ItemClassCommentBinding
import com.online.coursestore.manager.Utils
import com.online.coursestore.model.Comment

class ClassCommentRvAdapter(comments: List<Comment>) :
    BaseArrayAdapter<Comment, ClassCommentRvAdapter.ViewHolder>(comments) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemClassCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val comment = items[position]

        if (comment.user?.avatar != null) {
            Glide.with(viewholder.itemView.context).load(comment.user?.avatar)
                .into(viewholder.binding.itemClassCommentImg)
        }

        viewholder.binding.itemClassCommentNameTv.text = comment.user?.name
//        viewholder.binding.itemClassCommentDateTv.text =
//            Utils.getDateFromTimestamp(comment.createdAt)
        viewholder.binding.itemClassCommentDateTv.text = comment.createdAt
        if (comment.blog != null) {
            viewholder.binding.itemClassCommentTitleTv.text = comment.blog!!.title
        } else {
            viewholder.binding.itemClassCommentTitleTv.text = comment.course!!.title
        }
    }

    class ViewHolder(val binding: ItemClassCommentBinding) :
        RecyclerView.ViewHolder(binding.root)
}