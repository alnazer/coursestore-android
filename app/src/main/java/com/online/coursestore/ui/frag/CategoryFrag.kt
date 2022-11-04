package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.FragCategoryBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.ClassListGridRvAdapter
import com.online.coursestore.manager.adapter.FeaturedSliderAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.CategoryPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import com.online.coursestore.ui.widget.ClassesFiltersDialog
import com.online.coursestore.ui.widget.ClassesOptionsDialog
import com.paypal.platform.authsdk.partnerauth.lls.domain.VISITOR_ID

class CategoryFrag : NetworkObserverFragment(), View.OnClickListener, EmptyState {

    private lateinit var mBinding: FragCategoryBinding
    private lateinit var mPresenter: Presenter.CategoryPresenter
    private lateinit var mFilters: ArrayList<CourseFilter>
    private lateinit var mCategory: Category
    private var mSelectedOptions: ArrayList<KeyValuePair>? = null
    private var mSelectedFilters: ArrayList<KeyValuePair>? = null


    private val mOptionsCallback = object : ItemCallback<List<KeyValuePair>> {
        override fun onItem(item: List<KeyValuePair>, vararg args: Any) {
            removeItems()
            mSelectedOptions = item as ArrayList<KeyValuePair>
            mPresenter.getCatFeaturedCourses(mCategory.id, mSelectedOptions, mSelectedFilters)
        }
    }

    private val mFiltersCallback = object : ItemCallback<List<KeyValuePair>> {
        override fun onItem(item: List<KeyValuePair>, vararg args: Any) {
            removeItems()
            mSelectedFilters = item as ArrayList<KeyValuePair>
            mPresenter.getCatFeaturedCourses(mCategory.id, mSelectedOptions, mSelectedFilters)
        }
    }

    private fun removeItems() {
        val adapter = mBinding.categoryCourseRv.adapter as ClassListGridRvAdapter
        val itemCount = adapter.itemCount
        adapter.items.clear()
        adapter.notifyItemRangeRemoved(0, itemCount)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCategoryBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mCategory = requireArguments().getParcelable(App.CATEGORY)!!

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, mCategory.title)

        mPresenter = CategoryPresenterImpl(this)

        mBinding.categoryOptionsBtn.setOnClickListener(this)
        mBinding.categoryFiltersBtn.setOnClickListener(this)
        mBinding.categoryListDisplayBtn.setOnClickListener(this)

        mPresenter.getCatFeaturedCourses(mCategory.id)
        mPresenter.getCatFiltersAndCourses(mCategory.id)
    }

    fun showProgress(){
        mBinding.categoryCourseRvProgressBar.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.categoryOptionsBtn -> {
                val dialog = ClassesOptionsDialog()

                val bundle = Bundle()
                bundle.putSerializable(App.SELECTED, mSelectedOptions)

                dialog.arguments = bundle

                dialog.setCallback(mOptionsCallback)
                dialog.show(childFragmentManager, null)
            }

            R.id.categoryFiltersBtn -> {
                if (this::mFilters.isInitialized) {
                    val dialog = ClassesFiltersDialog()

                    val bundle = Bundle()
                    bundle.putParcelableArrayList(App.SELECTED, mSelectedFilters)
                    bundle.putParcelableArrayList(App.FILTERS, mFilters)

                    dialog.arguments = bundle

                    dialog.setCallback(mFiltersCallback)
                    dialog.show(childFragmentManager, null)
                }
            }

            R.id.categoryListDisplayBtn -> {
                val categoryCourseRv = mBinding.categoryCourseRv
                if (categoryCourseRv.adapter == null)
                    return

                val layoutManager = categoryCourseRv.layoutManager as GridLayoutManager

                if (layoutManager.spanCount == 1) {
                    mBinding.categoryListDisplayBtn.setImageResource(R.drawable.ic_list)
                    layoutManager.spanCount = 2
                } else {
                    mBinding.categoryListDisplayBtn.setImageResource(R.drawable.ic_grid)
                    layoutManager.spanCount = 1
                }

                val adapter = categoryCourseRv.adapter!!
                adapter.notifyItemRangeChanged(0, adapter.itemCount)
            }
        }
    }

    fun onResultReceived(data: Data<CatCourses>) {
        mBinding.categoryCourseRvProgressBar.visibility = View.GONE
        mFilters = (data.data!!.filters as ArrayList<CourseFilter>?)!!

        val rv = mBinding.categoryCourseRv


        if (rv.adapter == null) {
            val gridLayoutManager = GridLayoutManager(requireContext(), 2)
            rv.layoutManager = gridLayoutManager

            rv.adapter = ClassListGridRvAdapter(
                data.data!!.courses!!,
                activity as MainActivity, gridLayoutManager
            )
        } else {
            val adapter = rv.adapter as ClassListGridRvAdapter
            adapter.items.addAll(data.data!!.courses!!)
            adapter.notifyItemRangeInserted(0, adapter.itemCount)
        }

        if (data.data!!.courses!!.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    fun onFeaturedCoursesReceived(data: Data<List<Course>>) {
//        mBinding.categoryFeaturedViewPagerProgressBar.visibility = View.GONE
//        if (data.data!!.isNotEmpty()) {
//            val adapter = FeaturedSliderAdapter(data.data!!, activity as MainActivity)
//            mBinding.categoryFeaturedViewPager.adapter = adapter
//            mBinding.categoryFeaturedIndicator.setViewPager2(mBinding.categoryFeaturedViewPager)
//        } else {
//            mBinding.categoryFeaturedViewPager.visibility = View.GONE
//            mBinding.categoryFeaturedIndicator.visibility = View.GONE
//        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_data, R.string.data_not_found, R.string.data_not_found_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.categoryCourseEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}