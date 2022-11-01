package com.online.coursestore.ui.frag

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.online.coursestore.R
import com.online.coursestore.databinding.FragSearchBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.ClassListGridRvAdapter
import com.online.coursestore.manager.adapter.ViewPagerAdapter
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.*
import com.online.coursestore.model.view.EmptyStateData
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.SearchPresenterImpl
import com.online.coursestore.presenterImpl.SearchResultPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.widget.LoadingDialog

class SearchFrag : NetworkObserverFragment(), View.OnClickListener{

    private lateinit var mBinding: FragSearchBinding
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mPresenter : Presenter.SearchPresenter
    private lateinit var mPresenterResult: Presenter.SearchResultPresenter
    private lateinit var mSearchQuery: String
    private lateinit var mLoadingDialog: LoadingDialog
    val classesFrag = ClassesFrag()
    val usersFrag = UsersOrganizationsFrag()
    val organizationsFrag = UsersOrganizationsFrag()

    private val mTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (s != null) {
                if (s.isNotEmpty()) {
                    if (mBottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    }
                } else if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    mBinding.searchRv.visibility = View.VISIBLE
                    mBinding.searchResultTabContainer.tabContainer.visibility = View.GONE
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSearchBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.search)

        mPresenter = SearchPresenterImpl(this)
        mPresenter.getBestRatedCourses()

        mBottomSheetBehavior = BottomSheetBehavior.from(mBinding.searchBtnContainer)
        mBottomSheetBehavior.isHideable = true
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        mBinding.searchEdtx.addTextChangedListener(mTextWatcher)
        mBinding.searchBtn.setOnClickListener(this)
        setKeyboardEnterListener()

    }

    private fun setKeyboardEnterListener() {
        mBinding.searchEdtx.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(
                v: View?,
                keyCode: Int,
                event: KeyEvent
            ): Boolean {
                // If the event is a key-down event on the "enter" button
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    // Perform action on key press
                    onClick(mBinding.searchEdtx)
                    return true
                }
                return false
            }
        })

    }

    override fun onClick(v: View?) {
        val s = mBinding.searchEdtx.text.toString()
        if (s.isEmpty()) {
            return
        }

        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        mSearchQuery = s

        mPresenterResult = SearchResultPresenterImpl(this)
        mPresenterResult.search(mSearchQuery)

//        val bundle = Bundle()
//        bundle.putString(App.KEY, s)
//
//        val resultFrag = SearchResultFrag()
//        resultFrag.arguments = bundle
//
//        (activity as MainActivity).transact(resultFrag)
    }

    fun onBestRatedCoursesRecevied(data: Data<List<Course>>) {
        mBinding.searchRvProgressBar.visibility = View.GONE
        mBinding.searchRv.adapter = ClassListGridRvAdapter(data.data!!, activity as MainActivity)
    }

    fun onSearchResultReceived(data: Data<SearchObject>) {
        val courses = data.data!!.courses.items
        val coursesCount = data.data!!.courses.count

        val users = data.data!!.users.items
        val usersCount = data.data!!.users.count

        val organizations = data.data!!.organizations.items
        val organizationsCount = data.data!!.organizations.count
        mBinding.searchRv.visibility = View.GONE
        mBinding.searchResultTabContainer.tabContainer.visibility = View.VISIBLE

//        mBinding.searchEdtx.setText(("${coursesCount + usersCount + organizationsCount}" +
//                " ${getString(R.string.results_found_for)} \"$mSearchQuery\""))

//        mBinding.searchResultTv.text =
//            ("${coursesCount + usersCount + organizationsCount}" +
//                    " ${getString(R.string.results_found_for)} \"$mSearchQuery\"")


        val tabLayout = mBinding.searchResultTabContainer.tabLayout
        val viewPager = mBinding.searchResultTabContainer.viewPager

        var classesTitle = getString(R.string.classes)
        if (coursesCount > 0) {
            classesTitle += " (${coursesCount})"
        }

        var usersTitle = getString(R.string.users)
        if (usersCount > 0) {
            usersTitle += " (${usersCount})"
        }

        var organizationsTitle = getString(R.string.organizations)
        if (organizationsCount > 0) {
            organizationsTitle += " (${organizationsCount})"
        }

        val emptyStateData = EmptyStateData(
            R.drawable.no_result,
            R.string.result_no_found,
            R.string.try_more_for_search
        )

//        val classesFrag = ClassesFrag()
//        val usersFrag = UsersOrganizationsFrag()
//        val organizationsFrag = UsersOrganizationsFrag()
        var bundle = Bundle()
//          bundle.putParcelableArrayList(App.COURSES, courses as ArrayList<Course>)
        Log.d("SizeSearch", courses.size.toString())
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        classesFrag.arguments = bundle

        bundle = Bundle()
        bundle.putParcelableArrayList(App.USERS, users as ArrayList<User>)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        usersFrag.arguments = bundle

        bundle = Bundle()
        bundle.putParcelableArrayList(App.USERS, organizations as ArrayList<User>)
        bundle.putParcelable(App.EMPTY_STATE, emptyStateData)
        organizationsFrag.arguments = bundle
        val adapter = ViewPagerAdapter(childFragmentManager)
        adapter.add(classesFrag, classesTitle)
        adapter.add(usersFrag, usersTitle)
        adapter.add(organizationsFrag, organizationsTitle)
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = adapter
        classesFrag.updateCourses(courses as ArrayList<Course>)
        mLoadingDialog.dismiss()


    }
}