package com.online.coursestore.ui.frag

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.online.coursestore.R
import com.online.coursestore.databinding.EmptyStateBinding
import com.online.coursestore.databinding.FragProfileAboutBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.Utils.toBoolean
import com.online.coursestore.manager.Utils.toInt
import com.online.coursestore.model.Follow
import com.online.coursestore.model.UserProfile
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.ProfileAboutPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import com.online.coursestore.ui.widget.NewMessageDialog

class ProfileAboutFrag : Fragment(), EmptyState, MainActivity.OnRefreshListener {

    private lateinit var mBinding: FragProfileAboutBinding
    private lateinit var mUserProfile: UserProfile
    private lateinit var mPresenter: Presenter.ProfileAboutPresenter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragProfileAboutBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mPresenter = ProfileAboutPresenterImpl(this)
        mUserProfile = requireArguments().getParcelable(App.USER)!!

        if (mUserProfile.bio != null) {
            if (mUserProfile.about != null){
                mBinding.profileAboutBioTv.text = Utils.getTextAsHtml(mUserProfile.bio!!)
                mBinding.profileAboutBioTv.text = mBinding.profileAboutBioTv.text.toString() + "\n\n" + Utils.getTextAsHtml(mUserProfile.about!!)
            }else{
                mBinding.profileAboutBioTv.text = Utils.getTextAsHtml(mUserProfile.bio!!)
            }
        }

        if (mUserProfile.offline.toBoolean() && !mUserProfile.offlineMessage.isNullOrEmpty()) {
            mBinding.profileAboutOfflineHeader.root.visibility = View.VISIBLE

            var headerTitle = if (mUserProfile.isOrganizaton()) {
                getString(R.string.organization)
            } else {
                getString(R.string.instructor)
            }

            headerTitle += " ${getString(R.string.is_not_available)}"

            mBinding.profileAboutOfflineHeader.HeaderInfoTitleTv.text = headerTitle
            mBinding.profileAboutOfflineHeader.HeaderInfoDescTv.text = mUserProfile.offlineMessage
        }

        if (mUserProfile.experiences.isNotEmpty()) {
            val builder = StringBuilder()

            for (experience in mUserProfile.experiences) {
                builder.append("- ${experience}\n")
            }

            mBinding.profileAboutExperiencesValueTv.text = builder.toString()
        } else {
            mBinding.profileAboutExperiencesKeyTv.visibility = View.GONE
            mBinding.profileAboutExperiencesValueTv.visibility = View.GONE
        }

        if (mUserProfile.educations.isNotEmpty()) {
            val builder = StringBuilder()

            for (education in mUserProfile.educations) {
                builder.append("- ${education}\n")
            }

            mBinding.profileAboutEducationsValueTv.text = builder.toString()
        } else {
            mBinding.profileAboutEducationsKeyTv.visibility = View.GONE
            mBinding.profileAboutEducationsValueTv.visibility = View.GONE
        }

        if (mUserProfile.bio.isNullOrEmpty() && mUserProfile.experiences.isEmpty() &&
            mUserProfile.educations.isEmpty()
        ) {
            showEmptyState()
        }

        if (mUserProfile.userIsFollower) {
            (parentFragment as ProfileFrag).updateFollowBtn(true.toInt())
        }
    }

    fun onFollowed(follow: Follow) {
        if (context == null) return

        mUserProfile.userIsFollower = follow.status.toBoolean()
        (parentFragment as ProfileFrag).updateFollowBtn(follow.status)
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_biography, R.string.no_biography, R.string.no_biography_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.profileAboutEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    override fun getRefreshListener(): MainActivity.OnRefreshListener? {
        return this
    }

    override fun refresh() {
        hideEmptyState()
        mUserProfile = App.loggedInUser!!
        init()
    }

    fun onSendMessage() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage()
            return
        }

        val bundle = Bundle()
        bundle.putInt(App.USER_ID, mUserProfile.id)

        val dialog = NewMessageDialog()
        dialog.arguments = bundle

        dialog.show(childFragmentManager, null)
    }

    fun onFollowUnfollow() {
        if (!App.isLoggedIn()) {
            (activity as MainActivity).goToLoginPage()
            return
        }

        val follow = Follow()

        if (mUserProfile.userIsFollower) {
            follow.status = false.toInt()
        } else {
            follow.status = true.toInt()
        }

        mPresenter.follow(mUserProfile.id, follow)
    }

}