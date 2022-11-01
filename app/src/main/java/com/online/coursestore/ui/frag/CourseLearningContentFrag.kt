package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.coursestore.R
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.ChaptersContentAdapter
import com.online.coursestore.model.*
import com.online.coursestore.model.view.ChapterView
import java.util.ArrayList

class CourseLearningContentFrag : Fragment() {
    private lateinit var mBinding: RvBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(App.OFFLINE)

        val chapters = ArrayList<Chapter>()
        chapters.addAll(course.sessionChapters)
        chapters.addAll(course.filesChapters)
        chapters.addAll(course.textLessonChapters)

        val items = ArrayList<ChapterView>()

        for (chapter in chapters) {

            val innerItems: List<CourseCommonItem>
            val size: Int

            when (chapter) {
                is SessionChapter -> {
                    size = chapter.sessions.size
                    innerItems = chapter.sessions.toMutableList()
                }
                is FileChapter -> {
                    size = chapter.files.size
                    innerItems = chapter.files.toMutableList()
                }
                is TextChapter -> {
                    size = chapter.textLessons.size
                    innerItems = chapter.textLessons.toMutableList()
                }

                else -> {
                    return
                }
            }

            items.add(
                ChapterView(
                    chapter.title,
                    "$size ${getString(R.string.lessons)}",
                    innerItems
                )
            )
        }

        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = ChaptersContentAdapter(items, course, requireActivity(), offlineMode)
    }
}