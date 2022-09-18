package com.online.course.manager.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.online.course.R
import com.online.course.databinding.ItemChapterBinding
import com.online.course.manager.App
import com.online.course.manager.BuildVars
import com.online.course.manager.ToastMaker
import com.online.course.manager.listener.ItemClickListener
import com.online.course.manager.listener.OnItemClickListener
import com.online.course.model.*
import com.online.course.model.view.ContentItem
import com.online.course.ui.MainActivity
import com.online.course.ui.frag.*

class ChapterRvAdapter(
    items: List<ContentItem>,
    private val course: Course,
    private val activity: MainActivity
) :
    BaseArrayAdapter<ContentItem, ChapterRvAdapter.ViewHolder>(items) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChapterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = items[position]
        viewHolder.binding.itemChapterTitleTv.text = item.title
        viewHolder.binding.itemChapterRv.adapter =
            ChapterItemRvAdapter(item.chapterItems, activity.windowManager)
//        if (item.chapterItems.isNotEmpty()) {
//            viewHolder.binding.itemChapterRv.visibility = View.VISIBLE
//        } else {
//            viewHolder.binding.itemChapterRv.visibility = View.GONE
//        }
    }

    inner class ViewHolder(val binding: ItemChapterBinding) :
        RecyclerView.ViewHolder(binding.root), OnItemClickListener {
        init {
            binding.itemChapterRv.addOnItemTouchListener(
                ItemClickListener(
                    binding.itemChapterRv,
                    this
                )
            )
        }

        override fun onClick(view: View?, position: Int, id: Int) {
            val item = items[bindingAdapterPosition].chapterItems[position]
            val nextFrag: Fragment
            val bundle = Bundle()

            if (item is ChapterSessionItem || item is ChapterFileItem || item is ChapterTextItem) {

                bundle.putParcelable(App.COURSE, course)
                bundle.putInt(App.CHAPTER_POSITION, bindingAdapterPosition)
                bundle.putInt(App.CHAPTER_ITEM_POSITION, position)

                when (item) {
                    is ChapterSessionItem -> {
                        if (!course.hasUserBought) {
                            showBuyAlert()
                            return
                        }

                        bundle.putParcelable(App.ITEM, item)
                    }
                    is ChapterFileItem -> {
                        if (item.accessibility == ChapterFileItem.Accessibility.PAID.value && !course.hasUserBought) {
                            showBuyAlert()
                            return
                        }

                        bundle.putParcelable(App.ITEM, item)
                    }
                    is ChapterTextItem -> {
                        if (item.accessibility == ChapterFileItem.Accessibility.PAID.value && !course.hasUserBought) {
                            showBuyAlert()
                            return
                        }
                        bundle.putParcelable(App.ITEM, item)
                    }
                }

                nextFrag = CourseChapterItemFrag()

            } else if (item is Quiz) {
                nextFrag = if (item.isCertificate) {
                    CertificatesTabFrag()
                } else {
                    if (course.hasUserBought) {
                        if (item.authStatus == Quiz.NOT_PARTICIPATED) {
                            bundle.putParcelable(App.QUIZ, item)
                            QuizOverviewFrag()
                        } else {
                            bundle.putInt(App.ID, item.id)
                            QuizResultInfoFrag()
                        }
                    } else {
                        bundle.putParcelable(App.QUIZ, item)
                        QuizOverviewFrag()
                    }
                }
            } else {
                return
            }

            nextFrag.arguments = bundle
            activity.transact(nextFrag)
        }

        private fun showBuyAlert() {
            ToastMaker.show(
                itemView.context,
                itemView.context.getString(R.string.error),
                itemView.context.getString(R.string.you_have_to_buy_this_course),
                ToastMaker.Type.ERROR
            )
        }

        override fun onLongClick(view: View?, position: Int, id: Int) {
        }
    }
}