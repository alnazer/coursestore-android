package com.online.coursestore.manager.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.online.coursestore.R
import com.online.coursestore.databinding.ItemCertificateBinding
import com.online.coursestore.manager.Utils
import com.online.coursestore.model.Quiz
import com.online.coursestore.model.QuizResult

class CertificateRvAdapter<T : Parcelable>(certificates: List<T>) :
    BaseArrayAdapter<T, CertificateRvAdapter.ViewHolder>(certificates) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCertificateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewholder: ViewHolder, position: Int) {
        val item = items[position]
        if (item is QuizResult) {
//            viewholder.binding.itemCertificateTitleTv.text = item.quiz.title
            viewholder.binding.itemCertificateTitleTv.text = item.quiz.course.title
//            viewholder.binding.itemCertificateDescTv.text = item.quiz.course.title
            viewholder.binding.itemCertificateDateTv.text =
                Utils.getDateTimeFromTimestamp(item.createdAt)
            viewholder.binding.itemCertificateTotalMarkTv.text = item.quiz.totalMark.toString()

//            if (item.certificate == null) {
//                viewholder.binding.itemCertificateImg.setImageResource(R.drawable.cert_default)
//            } else {
//                Glide.with(viewholder.itemView.context).load(item.certificate!!.img)
//                    .into(viewholder.binding.itemCertificateImg)
//            }
        } else if (item is Quiz) {
            viewholder.binding.itemCertificateTitleTv.text = item.title
            viewholder.binding.itemCertificateDescTv.text = item.course.title
            viewholder.binding.itemCertificateDateTv.text =
                Utils.getDateTimeFromTimestamp(item.course.createdAt)
            viewholder.binding.itemCertificateTotalMarkTv.text = item.totalMark.toString()

//            if (item.course.img != null) {
//                Glide.with(viewholder.itemView.context).load(item.course.img)
//                    .into(viewholder.binding.itemCertificateImg)
//            }
        }
    }

    class ViewHolder(val binding: ItemCertificateBinding) :
        RecyclerView.ViewHolder(binding.root)
}