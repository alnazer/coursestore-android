package com.online.coursestore.manager.adapter

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.online.coursestore.R
import com.online.coursestore.databinding.ItemCourseCommon2Binding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.listener.ChapterClickListener
import com.online.coursestore.model.ChapterFileItem
import com.online.coursestore.model.ChapterSessionItem
import com.online.coursestore.model.ChapterTextItem
import com.online.coursestore.model.Course
import com.online.coursestore.model.view.ChapterView
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.SplashScreenActivity
import com.online.coursestore.ui.frag.CourseChapterItemFrag
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder


class ChaptersContentAdapter(
    groups: List<ExpandableGroup<*>>?,
    private val course: Course,
    private val activity: Activity,
    private val mOfflineMode: Boolean,
    val chapterClickListener: ChapterClickListener
) : ExpandableRecyclerViewAdapter<ChaptersContentAdapter.ChapterViewHolder, ChaptersContentAdapter.ChapterItemViewHolder>(
    groups
) {
    var selectedPosition = -1

    class ChapterViewHolder(val binding: ItemCourseCommon2Binding) :
        GroupViewHolder(binding.root) {
        init {
            initCard(false)
            binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_arrow_bottom_gull_gray)
            val padding = Utils.changeDpToPx(itemView.context, 23f).toInt()
            binding.itemCourseCommon2Img.setPadding(padding, padding, padding, padding)
        }

        private fun initCard(expand: Boolean) {
            val params =
                binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
            params.topMargin = Utils.changeDpToPx(itemView.context, 8f).toInt()
            if (expand) {
                params.bottomMargin = 0
                binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_top_corner15)
            } else {
                params.bottomMargin = Utils.changeDpToPx(itemView.context, 8f).toInt()
                binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_corner15)
            }
            binding.itemCourseCommonCard2.requestLayout()
        }

        override fun expand() {
            initCard(true)
            binding.itemCourseCommon2CheckImg.visibility = View.INVISIBLE
            binding.itemCourseCommon2BottomDash.visibility = View.VISIBLE
        }

        override fun collapse() {
            initCard(false)
            binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_arrow_bottom_gull_gray)
            binding.itemCourseCommon2BottomDash.visibility = View.INVISIBLE
        }
    }

    class ChapterItemViewHolder(val binding: ItemCourseCommon2Binding) :
        ChildViewHolder(binding.root) {

        init {
            val params =
                binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
            params.topMargin = 0
            params.bottomMargin = 0
            binding.itemCourseCommonCard2.requestLayout()
        }
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): ChapterViewHolder {
        return ChapterViewHolder(
            ItemCourseCommon2Binding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): ChapterItemViewHolder {
        return ChapterItemViewHolder(
            ItemCourseCommon2Binding.inflate(
                LayoutInflater.from(parent?.context),
                parent,
                false
            )
        )
    }

    override fun onBindChildViewHolder(
        holder: ChapterItemViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?,
        childIndex: Int
    ) {
        val context = holder.itemView.context
        val item = (group as ChapterView).items[childIndex]
        holder.binding.itemCourseCommon2TitleTv.text = item.title(context)
//        holder.binding.itemCourseCommon2DescTv.text = item.desc(context)
        holder.binding.itemCourseCommon2Img.setBackgroundResource(item.imgBgResource(context))
        holder.binding.itemCourseCommon2Img.setImageResource(item.imgResource(context))
        if (item.passed()) {
            holder.binding.itemCourseCommon2CheckImg.setImageResource(R.drawable.ic_check_green)
        } else {
            holder.binding.itemCourseCommon2CheckImg.visibility = View.GONE
        }

        if (childIndex == group.itemCount - 1) {
            holder.binding.itemCourseCommon2TopDash.visibility = View.VISIBLE
            holder.binding.itemCourseCommon2BottomDash.visibility = View.INVISIBLE

        } else {
            holder.binding.itemCourseCommon2BottomDash.visibility = View.VISIBLE
            holder.binding.itemCourseCommon2TopDash.visibility = View.VISIBLE
        }

        val params = holder.binding.itemCourseCommonCard2.layoutParams as RecyclerView.LayoutParams
        if (childIndex == group.itemCount - 1) {
            params.bottomMargin = Utils.changeDpToPx(context, 8f).toInt()
            holder.binding.itemCourseCommonCard2.setBackgroundResource(R.drawable.round_view_white_bottom_corner15)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.binding.itemCourseCommonCard2.foreground =
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect_bottom_corner_15)
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.binding.itemCourseCommonCard2.foreground =
                    ContextCompat.getDrawable(context, R.drawable.ripple_effect)
            }
            holder.binding.itemCourseCommonCard2.setBackgroundResource(R.color.white)
            if (selectedPosition == flatPosition){
                holder.binding.itemCourseCommonCard2.setBackgroundColor(activity.getColor(R.color.accent_transparent_10))
                holder.binding.itemCourseCommon2Img.setImageResource(R.drawable.ic_pause_circle_filled)
            }else{
                holder.binding.itemCourseCommonCard2.setBackgroundColor(activity.getColor(R.color.white))
                holder.binding.itemCourseCommon2Img.setImageResource(R.drawable.ic_play2)
            }
            params.bottomMargin = 0
        }
        holder.binding.itemCourseCommonCard2.requestLayout()

        holder.itemView.setOnClickListener {
//            val listPos = expandableList.getUnflattenedPosition(flatPosition)
//
//            val bundle = Bundle()
//            bundle.putParcelable(App.COURSE, course)
//            //flat position starts with 1 not 0
//            bundle.putInt(App.CHAPTER_POSITION, listPos.groupPos)
//            bundle.putInt(App.CHAPTER_ITEM_POSITION, listPos.childPos)
//            bundle.putBoolean(App.OFFLINE, mOfflineMode)
//
//            when (item) {
//                is ChapterSessionItem -> {
//                    bundle.putParcelable(App.ITEM, item)
//                }
//
//                is ChapterFileItem -> {
//                    bundle.putParcelable(App.ITEM, item)
//                }
//
//                is ChapterTextItem -> {
//                    bundle.putParcelable(App.ITEM, item)
//                }
//            }
//
//            val courseChapterPage = CourseChapterItemFrag()
//            courseChapterPage.arguments = bundle
//            if (activity is MainActivity) {
//                activity.transact(courseChapterPage)
//            } else if (activity is SplashScreenActivity) {
//                activity.transact(courseChapterPage)
//            }
            if (item is ChapterFileItem){
                selectedPosition = flatPosition
                chapterClickListener.onClick(item)
                notifyDataSetChanged()
            }
        }
    }

    override fun onBindGroupViewHolder(
        holder: ChapterViewHolder,
        flatPosition: Int,
        group: ExpandableGroup<*>?
    ) {
        val chapterView = group as ChapterView

        holder.binding.itemCourseCommon2Img.setImageResource(R.drawable.ic_grid_white)
        holder.binding.itemCourseCommon2Img.setBackgroundResource(R.drawable.round_view_accent_corner10)
        holder.binding.itemCourseCommon2TitleTv.text = chapterView.title
        holder.binding.itemCourseCommon2DescTv.text = chapterView.description
    }
}