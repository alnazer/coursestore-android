package com.online.coursestore.ui.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.online.coursestore.R
import com.online.coursestore.databinding.RvNestedBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.ChapterRvAdapter
import com.online.coursestore.manager.listener.ChapterClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.*
import com.online.coursestore.model.view.ContentItem
import com.online.coursestore.ui.MainActivity
import kotlin.math.roundToInt

class CourseDetailsContentFrag : NetworkObserverFragment(), ChapterClickListener {

    private lateinit var mBinding: RvNestedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvNestedBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun initBottomPadding() {
        val btnsContainer =
            (parentFragment as CourseDetailsFrag).mBinding.courseDetailsPurchaseBtnsContainer
        btnsContainer.post {
            val padding =
                (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
            mBinding.rvNestedContainer.setPadding(0, 0, 0, padding)
        }
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        if (!course.hasUserBought) {
            initBottomPadding()
        }
        if (requireArguments().containsKey("viewPager")){
            if (requireArguments().getBoolean("viewPager")){
                mBinding.root.rotationY = 180f
            }
        }
        mBinding.rvNestedProgressBar.visibility = View.GONE

        val chapters = ArrayList<Chapter>()
        chapters.addAll(course.sessionChapters)
        chapters.addAll(course.filesChapters)
        chapters.addAll(course.textLessonChapters)

        val contentItems = ArrayList<ContentItem>()
        for (chapter in chapters) {
            val contentItem = ContentItem()
            contentItem.title = chapter.title

            when (chapter) {
                is SessionChapter -> {
                    contentItem.chapterItems = chapter.sessions.toMutableList()
                }
                is FileChapter -> {
                    contentItem.chapterItems = chapter.files.toMutableList()
                }
                is TextChapter -> {
                    contentItem.chapterItems = chapter.textLessons.toMutableList()
                }
            }

            contentItems.add(contentItem)
        }

        if (course.quizzes.isNotEmpty()) {
            val contentItem = ContentItem()
            contentItem.title = getString(R.string.quizzes)
            contentItem.chapterItems = ArrayList()
            for (quiz in course.quizzes) {
                contentItem.chapterItems.add(quiz)
            }

            contentItems.add(contentItem)
        }

        if (course.hasUserBought && course.certificates.isNotEmpty()) {
            val contentItem = ContentItem()
            contentItem.title = getString(R.string.certificates)
            contentItem.chapterItems = ArrayList()
            for (cert in course.certificates) {
                if (cert.authStatus == QuizResult.Result.PASSED.value) {
                    cert.isCertificate = true
                    contentItem.chapterItems.add(cert)
                }
            }

            if (contentItem.chapterItems.isNotEmpty()) {
                contentItems.add(contentItem)
            }
        }

        val adapter = ChapterRvAdapter(contentItems, course, activity as MainActivity, this)
        mBinding.rvNestedRv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvNestedRv.adapter = adapter
    }

    override fun onClick(chapterFileItem: ChapterFileItem) {
        var gson = Gson()
        var videoCipherdata:VideoCipherData = gson.fromJson(gson.toJsonTree(chapterFileItem.videoCipherData).asJsonObject, VideoCipherData::class.java)
        (parentFragment as CourseDetailsFrag).loadVideoCipherVideo(
            (parentFragment as CourseDetailsFrag).obtainNewVdoParams(videoCipherdata.otp!!,
                videoCipherdata.playbackInfo!!)
        )
        (parentFragment as CourseDetailsFrag).showHideBtnPromo(true)
    }

}