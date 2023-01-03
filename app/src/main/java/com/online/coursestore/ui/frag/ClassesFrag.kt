package com.online.coursestore.ui.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.ClassListGridRvAdapter
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Course
import com.online.coursestore.model.ToolbarOptions
import com.online.coursestore.model.view.EmptyStateData
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.ClassesPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.schedule

class ClassesFrag : NetworkObserverFragment(), EmptyState {
    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.ClassesPresenter
    private var mTitle: String? = null
    private lateinit var classes: ArrayList<Course>

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

    fun updateCourses(c: ArrayList<Course>){
        classes = c
        if (this:: mBinding.isInitialized){
            if (c.size == 0){
                mBinding.rv.visibility = View.INVISIBLE
            }
            init()
        }
    }

    private fun init() {
        if (requireArguments().getParcelableArrayList<Course>(App.COURSES) != null){
            classes = requireArguments().getParcelableArrayList<Course>(App.COURSES) as ArrayList<Course>
        }
        if (requireArguments().containsKey("viewPager")){
            if (requireArguments().getBoolean("viewPager")){
                mBinding.root.rotationY = 180f
            }
        }
        //var classes = requireArguments().getParcelableArrayList<Course>(App.COURSES)
        val useGrid = requireArguments().getBoolean(App.USE_GRID)
        val nestedEnabled = requireArguments().getBoolean(App.NESTED_ENABLED)
        mTitle = requireArguments().getString(App.TITLE)
        val emptyStateData = requireArguments().getParcelable<EmptyStateData>(App.EMPTY_STATE)

        if (mTitle != null) {
            val toolbarOptions = ToolbarOptions()
            toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

            (activity as MainActivity).showToolbar(toolbarOptions, mTitle!!)
        }

        if (nestedEnabled) {
            mBinding.rv.isNestedScrollingEnabled = true
        }

        if (useGrid) {
            mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        } else {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        }

        if (this::classes.isInitialized){
            if (classes != null) {
                mBinding.rvProgressBar.visibility = View.GONE
                if (classes.isEmpty()) {
                    showEmptyState(emptyStateData!!)
                    return
                }

                if (useGrid) {
                    val params = mBinding.rv.layoutParams as FrameLayout.LayoutParams
                    params.marginStart = Utils.changeDpToPx(requireContext(), 16f).toInt()
                    params.marginEnd = Utils.changeDpToPx(requireContext(), 16f).toInt()
                    mBinding.rv.requestLayout()

                    val adapter = ClassListGridRvAdapter(
                        classes, activity as MainActivity,
                        mBinding.rv.layoutManager as GridLayoutManager
                    )
                    mBinding.rv.adapter = adapter
                } else {
                    val adapter = ClassListGridRvAdapter(classes, activity as MainActivity)
                    mBinding.rv.adapter = adapter
                    mBinding.rv.visibility = View.VISIBLE
                    hideEmptyState()
                }
            }
        }else{
            mBinding.rvProgressBar.visibility = View.GONE
            val map = requireArguments().getSerializable(App.FILTERS) as HashMap<String, String>
            mPresenter = ClassesPresenterImpl(
                this,
                mBinding.rv,
                map
            )

            mPresenter.getCourses()
        }
    }


    fun showEmptyStateForCourse() {
        showEmptyState(
            R.drawable.no_course,
            getString(R.string.no_courses),
            "${getString(R.string.no)} $mTitle ${getString(R.string.found)}"
        )
    }

    fun showEmptyState(emptyStateData: EmptyStateData) {
        showEmptyState(emptyStateData.img, emptyStateData.titleRes, emptyStateData.descRes)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}