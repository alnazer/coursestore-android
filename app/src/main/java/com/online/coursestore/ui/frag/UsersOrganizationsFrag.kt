package com.online.coursestore.ui.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.UsersOrganizationGridAdapter
import com.online.coursestore.manager.listener.ItemClickListener
import com.online.coursestore.manager.listener.OnItemClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Count
import com.online.coursestore.model.KeyValuePair
import com.online.coursestore.model.User
import com.online.coursestore.model.view.EmptyStateData
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.ProvidersPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import java.io.Serializable

class UsersOrganizationsFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState {

    private lateinit var mBinding: RvBinding
    private var mType: ProviderType? = null
    private lateinit var mPresenter: Presenter.ProvidersPresenter

    enum class ProviderType : Serializable {
        INSTRUCTORS,
        ORGANIZATIONS,
        CONSULTANTS
    }

    companion object {
        private const val TAG = "UsersOrganizationsFrag"
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

    override fun onStart() {
        super.onStart()
        if (mType != null) {
            Log.d(TAG, "onStart: mType=$mType")
        }
    }

    private fun init() {
        val users = requireArguments().getParcelableArrayList<User>(App.USERS)
        val type = requireArguments().getSerializable(App.SELECTION_TYPE)
        val nestedEnabled = requireArguments().getBoolean(App.NESTED_ENABLED)
        if (requireArguments().containsKey("viewPager")){
            if (requireArguments().getBoolean("viewPager")){
                mBinding.root.rotationY = 180f
            }
        }
        if (nestedEnabled) {
            mBinding.rv.isNestedScrollingEnabled = true
        }

        if (users != null) {
            initRv(users)

        } else if (type != null) {
            mType = type as ProviderType
            mPresenter = ProvidersPresenterImpl(this)
            mPresenter.getProviders(mType as ProviderType, null)
        }
    }

    private fun initRv(users: List<User>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE

        if (users.isEmpty()) {
            val emptyStateData = requireArguments().getParcelable<EmptyStateData>(App.EMPTY_STATE)
            showEmptyState(emptyStateData!!)
        } else {
            if (mBinding.rv.adapter == null) {
                val adapter = UsersOrganizationGridAdapter(users)
                mBinding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
                mBinding.rv.adapter = adapter
                mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
            } else {
                val adapter = mBinding.rv.adapter as UsersOrganizationGridAdapter
                adapter.items.addAll(users)
                adapter.notifyItemRangeInserted(0, adapter.itemCount)
            }
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val user = (mBinding.rv.adapter as UsersOrganizationGridAdapter).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.USER, user)

        val frag = ProfileFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
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

    fun onProvidersReceived(count: Count<User>) {
        initRv(count.items)

        if (count.count > 0) {
            mType?.let { (parentFragment as ProvidersFrag).updateTab(it, count.count) }
        }
    }

    fun filter(filter: ArrayList<KeyValuePair>?) {
        mBinding.rvProgressBar.visibility = View.VISIBLE

        val adapter = mBinding.rv.adapter as UsersOrganizationGridAdapter
        val itemCount = adapter.itemCount
        adapter.items.clear()
        adapter.notifyItemRangeRemoved(0, itemCount)

        mPresenter.getProviders(mType!!, filter)
    }
}