package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.MyClassesRvAdapter
import com.online.coursestore.manager.listener.ItemClickListener
import com.online.coursestore.manager.listener.OnItemClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Course
import com.online.coursestore.model.MyClasses
import com.online.coursestore.presenterImpl.MyClassesPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import java.io.Serializable

class MyClassesFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState,
    MainActivity.OnRefreshListener {

    private lateinit var mBinding: RvBinding
    private lateinit var mType: Type

    enum class Type : Serializable {
        PURCHASED,
        MY_CLASSES,
        ORGANIZATION,
        INVITED
    }

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
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )

        if (!App.isLoggedIn()) {
            showLoginState()
            return
        }

        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mType = requireArguments().getSerializable(App.SELECTION_TYPE) as Type

        val presenter = MyClassesPresenterImpl(this)
        if (App.loggedInUser!!.isUser()) {
            if (mType == Type.PURCHASED) {
                presenter.getPurchased()
            } else {
                presenter.getOrganizations()
            }

        } else {
            presenter.getMyClasses()
        }
    }

    fun onPurchasedReceived(items: List<Course>, purchased: Boolean) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = MyClassesRvAdapter(items, purchased)
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        } else {
            showEmptyState()
        }
    }

    fun onMyClassesReceived(myClasses: MyClasses) {
        when (mType) {
            Type.PURCHASED -> {
                if (myClasses.purchases.isNotEmpty()) {
                    onPurchasedReceived(myClasses.purchases, true)
                } else {
                    showEmptyState()
                }
            }

            Type.ORGANIZATION -> {
                if (myClasses.organizations.isNotEmpty()) {
                    onPurchasedReceived(myClasses.organizations, false)
                } else {
                    showEmptyState()
                }
            }

            Type.MY_CLASSES -> {
                if (myClasses.myClasses.isNotEmpty()) {
                    onPurchasedReceived(myClasses.myClasses, false)
                } else {
                    showEmptyState()
                }
            }

            Type.INVITED -> {
                if (myClasses.invitations.isNotEmpty()) {
                    onPurchasedReceived(myClasses.invitations, false)
                } else {
                    showEmptyState()
                }
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val course = (mBinding.rv.adapter as MyClassesRvAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, course)

        val frag: Fragment = if (mType == Type.PURCHASED) {
            CourseDetailsFrag()
        } else {
            CourseOverviewFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        var desc = 0

        when (mType) {
            Type.PURCHASED -> {
                desc = R.string.purchase_no_courses
            }

            Type.ORGANIZATION -> {
                desc = R.string.no_courses_organization
            }

            Type.MY_CLASSES -> {
                desc = R.string.no_courses_class
            }

            Type.INVITED -> {
                desc = R.string.no_courses_invited
            }
        }

        showEmptyState(R.drawable.no_course, R.string.no_courses, desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        init()
    }
}