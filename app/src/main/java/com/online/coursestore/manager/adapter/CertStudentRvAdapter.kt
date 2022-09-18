package com.online.coursestore.manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.databinding.ItemStudentCertBinding
import com.online.coursestore.manager.Utils
import com.online.coursestore.model.QuizResult

class CertStudentRvAdapter(results: List<QuizResult>) :
    BaseArrayAdapter<QuizResult, CertStudentRvAdapter.ViewHolder>(results) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStudentCertBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val result = items[position]
        val context = viewholder.itemView.context

        if (result.user!!.avatar != null) {
            Glide.with(context).load(result.user!!.avatar)
                .into(viewholder.binding.itemStudentCertImg)
        }

        viewholder.binding.itemStudentCertNameTv.text = result.user!!.name
        viewholder.binding.itemStudentCertDateTv.text = Utils.getDateFromTimestamp(result.createdAt)
    }

    class ViewHolder(val binding: ItemStudentCertBinding) :
        RecyclerView.ViewHolder(binding.root)
}