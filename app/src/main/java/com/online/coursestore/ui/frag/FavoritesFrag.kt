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
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.adapter.FavoritesRvAdapter
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.BaseResponse
import com.online.coursestore.model.Favorite
import com.online.coursestore.model.ToolbarOptions
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.FavoritesPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState

class FavoritesFrag : NetworkObserverFragment(), EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mPresenter: Presenter.FavoritesPresenter

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
//        mBinding.rvContainer.setBackgroundColor(
//            ContextCompat.getColor(
//                requireContext(),
//                android.R.color.transparent
//            )
//        )
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.NAV
        toolbarOptions.endIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.favorites)

        mPresenter = FavoritesPresenterImpl(this)
        mPresenter.getFavorites()
    }

    fun onFavoritesReceived(items: List<Favorite>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        if (items.isNotEmpty()) {
            mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
            mBinding.rv.adapter = FavoritesRvAdapter(items.toMutableList(), this)
        } else {
            showEmptyState()
        }
    }

    fun removeItem(courseId: Int, adapterPosition: Int) {
        mPresenter.removeFromFavorite(courseId, adapterPosition)
    }

    fun onItemRemoved(response: BaseResponse, adapterPosition: Int) {
        if (context == null) return

        if (response.isSuccessful) {
            val adapter = mBinding.rv.adapter as FavoritesRvAdapter
            adapter.items.removeAt(adapterPosition)
            adapter.notifyItemRemoved(adapterPosition)

            if (adapter.itemCount == 0) {
                showEmptyState()
            }

        } else {
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_favorites, R.string.no_favorites, R.string.no_favorites_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}