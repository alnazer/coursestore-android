package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.FragCourseDetailsInformationBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.ExpandableTextViewHelper
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.Utils.isEllipsized
import com.online.coursestore.manager.adapter.CourseCommonRvAdapter
import com.online.coursestore.manager.adapter.FAQAdapter
import com.online.coursestore.manager.adapter.FeaturedSliderAdapter
import com.online.coursestore.model.Course
import com.online.coursestore.model.CourseCommonItem
import com.online.coursestore.model.view.ChapterView
import com.online.coursestore.ui.MainActivity
import kotlin.math.roundToInt

class CourseDetailsInformationFrag : Fragment(), View.OnClickListener {

    private lateinit var mBinding: FragCourseDetailsInformationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCourseDetailsInformationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(App.OFFLINE)

        initMarkInfos(course)
        initDescription(course)

        if (offlineMode) {
            hideDataInOfflineMode()
        } else {
            initBottomPadding()
            initListInfo(course)
            initPrerequisites(course)
        }
    }

    private fun initBottomPadding() {
        val btnsContainer =
            (parentFragment as CourseDetailsFrag).mBinding.courseDetailsPurchaseBtnsContainer
        btnsContainer.post {
            if (context == null) return@post

            val padding =
                (btnsContainer.height + Utils.changeDpToPx(requireContext(), 20f)).roundToInt()
            mBinding.courseDetailsInformationContainer.setPadding(0, 0, 0, padding)
        }
    }

    private fun hideDataInOfflineMode() {
        mBinding.courseDetailsInformationRv.visibility = View.GONE

        mBinding.courseDetailsInformationPrerequisitesTv.visibility = View.GONE
        mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
        mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
    }

    private fun initListInfo(course: Course) {
        val items = ArrayList<CourseCommonItem>()

        if (course.certificates.isEmpty()) {
            items.add(
                CourseCommonItem.creator(
                    getString(R.string.certificate),
                    getString(R.string.included),
                    R.drawable.ic_badge_white, R.drawable.round_view_accent_corner10
                )
            )
        }

        if (course.quizzes.isNotEmpty()) {
            items.add(
                CourseCommonItem.creator(
                    getString(R.string.quiz),
                    getString(R.string.included),
                    R.drawable.ic_quizzes, R.drawable.round_view_accent_corner10
                )
            )
        }

        if (course.supported) {
            items.add(
                CourseCommonItem.creator(
                    getString(R.string.supported),
                    getString(R.string._class),
                    R.drawable.ic_quizzes, R.drawable.round_view_accent_corner10
                )
            )
        }

        if (course.isDownloadable) {
            items.add(
                CourseCommonItem.creator(
                    getString(R.string.downloadable),
                    getString(R.string.content),
                    R.drawable.ic_quizzes, R.drawable.round_view_accent_corner10
                )
            )
        }

        if (items.isEmpty()) {
            mBinding.courseDetailsInformationRv.visibility = View.GONE
        } else {
            mBinding.courseDetailsInformationRv.adapter = CourseCommonRvAdapter(items)
        }
    }

    private fun initDescription(course: Course) {
        val descTv = mBinding.courseDetailsInformationDescTv
        descTv.text = course.description?.let {
            Utils.getTextAsHtml(it)
        }
        descTv.post {
            if (descTv.isEllipsized()) {
                mBinding.courseDetailsInformationViewMoreBtn.visibility = View.VISIBLE
                mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility =
                    View.VISIBLE
                mBinding.courseDetailsInformationViewMoreBtn.setOnClickListener(this)
            }
        }

        if (course.faqs.isNotEmpty()) {
            val chapterViews = ArrayList<ChapterView>()
            for (faq in course.faqs) {
                chapterViews.add(
                    ChapterView(
                        faq.title,
                        faq.answer,
                        arrayListOf(CourseCommonItem.creator("", "", 1, 1))
                    )
                )
            }

            mBinding.courseDetailsFaqsRv.adapter = FAQAdapter(chapterViews)
        } else {
            mBinding.courseDetailsFaqsRv.visibility = View.GONE
        }
    }

    private fun initMarkInfos(course: Course) {
        mBinding.courseDetailsInformationStudentMarkTv.text = course.studentsCount.toString()
        mBinding.courseDetailsInformationChaptersCountMarkTv.text =
            (course.sessionChapters.size + course.filesChapters.size + course.textLessonChapters.size).toString()

        if (course.isLive()) {
            mBinding.courseDetailsInformationLiveClassMarkImg.visibility = View.VISIBLE
            mBinding.courseDetailsInformationLiveClassKeyMarkTv.visibility = View.VISIBLE
            mBinding.courseDetailsInformationLiveClassMarkTv.visibility = View.VISIBLE

            mBinding.courseDetailsInformationStatusMarkImg.visibility = View.VISIBLE
            mBinding.courseDetailsInformationStatusKeyMarkTv.visibility = View.VISIBLE
            mBinding.courseDetailsInformationStatusMarkTv.visibility = View.VISIBLE

            course.liveCourseStatus?.let {
                val liveStatus : String

                if (course.liveCourseStatus == Course.WebinarStatus.FINISHED.value) {
                    mBinding.courseDetailsInformationStatusMarkTv.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.red
                        )
                    )
                    liveStatus = getString(R.string.finished)
                } else if (course.liveCourseStatus == Course.WebinarStatus.NOT_CONDUCTED.value) {
                    mBinding.courseDetailsInformationStatusMarkTv.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.accent
                        )
                    )
                    liveStatus = getString(R.string.not_conducted)
                } else {
                    liveStatus = getString(R.string.in_progress)
                }

                mBinding.courseDetailsInformationStatusMarkTv.text = liveStatus
            }

            mBinding.courseDetailsInformationStartDateMarkTv.text =
                Utils.getDateFromTimestamp(course.startDate)
        } else {
            mBinding.courseDetailsInformationStartDateMarkTv.text =
                Utils.getDateFromTimestamp(course.createdAt)
            mBinding.courseDetailsInformationStartDateKeyMarkTv.text =
                getString(R.string.publish_date)
        }

        mBinding.courseDetailsInformationDurationMarkTv.text =
            Utils.getDuration(requireContext(), course.duration)
    }

    private fun initPrerequisites(course: Course) {
        if (course.prerequisites.isEmpty()) {
            mBinding.courseDetailsInformationPrerequisitesTv.visibility = View.GONE
            mBinding.courseDetailsInformationPrerequisitesViewPager.visibility = View.GONE
            mBinding.courseDetailsInformationPrerequisitesIndicator.visibility = View.GONE
        } else {
            mBinding.courseDetailsInformationPrerequisitesViewPager.adapter =
                FeaturedSliderAdapter(course.prerequisites, activity as MainActivity)
            mBinding.courseDetailsInformationPrerequisitesIndicator.setViewPager2(
                mBinding.courseDetailsInformationPrerequisitesViewPager
            )
        }
    }

    override fun onClick(v: View?) {
        val btnText = mBinding.courseDetailsInformationViewMoreBtn.text
        if (btnText == getString(R.string.view_more)) {
            ExpandableTextViewHelper.expandTextView(mBinding.courseDetailsInformationDescTv)
            mBinding.courseDetailsInformationViewMoreBtn.text = getString(R.string.view_less)
            mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility =
                View.INVISIBLE
        } else {
            ExpandableTextViewHelper.collapseTextView(mBinding.courseDetailsInformationDescTv, 6)
            mBinding.courseDetailsInformationViewMoreBtn.text = getString(R.string.view_more)
            mBinding.courseDetailsInformationDescTvViewMoreBottomGradient.visibility = View.VISIBLE
        }
    }
}