package com.online.coursestore.ui.frag

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import androidx.annotation.WorkerThread
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.online.coursestore.R
import com.online.coursestore.databinding.FragCourseDetailsBinding
import com.online.coursestore.manager.*
import com.online.coursestore.manager.Utils.toBoolean
import com.online.coursestore.manager.adapter.ViewPagerAdapter
import com.online.coursestore.manager.db.AppDb
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.manager.player.PlayerHelper
import com.online.coursestore.manager.player.FileVideoPlayerHelper
import com.online.coursestore.manager.player.VdoPlayerControlView
import com.online.coursestore.model.*
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.CommonApiPresenterImpl
import com.online.coursestore.presenterImpl.CourseDetailsPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.VideoPlayerActivity
import com.online.coursestore.ui.widget.*
import com.paypal.checkout.order.Amount
import com.paypal.pyplcheckout.sca.runOnUiThread
import com.robinhood.ticker.TickerUtils
import com.vdocipher.aegis.media.ErrorDescription
import com.vdocipher.aegis.media.Track
import com.vdocipher.aegis.player.PlayerHost
import com.vdocipher.aegis.player.VdoInitParams
import com.vdocipher.aegis.player.VdoPlayer
import com.vdocipher.aegis.player.VdoPlayerSupportFragment
import com.vdocipher.aegis.ui.view.VdoPlayerUIFragment
import org.json.JSONException
import java.io.IOException
import java.lang.IllegalStateException


class CourseDetailsFrag : NetworkObserverFragment(), View.OnClickListener,
    TabLayout.OnTabSelectedListener,
    ItemCallback<Course>, PlayerHelper.Listener, PlayerHost.InitializationListener {

    lateinit var mBinding: FragCourseDetailsBinding
    private lateinit var mCourse: Course
    private lateinit var mPresenter: Presenter.CourseDetailsPresenter
    private lateinit var mCommonPresenter: Presenter.CommonApiPresenter
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mCurrentBtnsState: BtnContainerState
    private lateinit var mBottomSheetBehavoir: BottomSheetBehavior<*>
    private var mSpecialOfferCountDown: CountDownTimer? = null
    private var mVideoHelper: FileVideoPlayerHelper? = null
    private var mIsCollapsed = false
    private var mActivityFullScreenStarted = false
    private var mShowHours = false
    private var mShowDays = false
    private lateinit var playerFragment: VdoPlayerSupportFragment
    private lateinit var videoCipherPlayer :VdoPlayer
    private lateinit var paymentDialog: PaymentOptionsDialog
    private var currentOrientation = Configuration.ORIENTATION_PORTRAIT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    }

    private val mOnCommentAdded = object : ItemCallback<Comment> {
        override fun onItem(item: Comment, vararg args: Any) {
            try {
                val adapter = mBinding.courseDetailsViewPager.adapter as ViewPagerAdapter
                val frag = adapter.getItem(3) as CourseDetailsCommentsFrag
                frag.addComment(item)
            } catch (ex: Exception) {
            }
        }
    }

    private val mOnReviewAdded = object : ItemCallback<Review> {
        override fun onItem(item: Review, vararg args: Any) {
            try {
                val adapter = mBinding.courseDetailsViewPager.adapter as ViewPagerAdapter
                val frag = adapter.getItem(2) as CourseDetailsReviewsFrag
                frag.addReview(item)
            } catch (ex: Exception) {
            }
        }
    }

    enum class BtnContainerState {
        INFO,
        COMMENT,
        REVIEW,
        HIDE
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mBinding.courseDetailsPurchaseBtnsContainer.visibility = View.GONE
            (activity as MainActivity).hideToolbar()
            mBinding.courseDetailsRatingCountTv.visibility = View.GONE
            mBinding.courseDetailsRatingBar.visibility = View.GONE
            mBinding.courseDetailsTitleTv.visibility = View.GONE
            mBinding.courseDetailsCategoryTv.visibility = View.GONE
            mBinding.courseDetailsInTv.visibility = View.GONE
            mBinding.courseDetailsProgressBar.visibility = View.GONE
            mBinding.courseDetailsProgressTv.visibility = View.GONE
            mBinding.courseDetailsCollapsingLayout.setPadding(0,0,0,0)
            mBinding.courseDetailsCollapsingLayout.requestLayout()
            var params = mBinding.courseDetailsCoordinatorLayout.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = 0
            mBinding.courseDetailsCoordinatorLayout.layoutParams = params
            val ratio = Utils.getScreenWidth(requireActivity().windowManager)/Utils.getScreenHeight(requireActivity().windowManager).toFloat()
            playerFragment.setAspectRatio(ratio)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBinding.courseDetailsPurchaseBtnsContainer.visibility = View.VISIBLE
            showToolbar()
            mBinding.courseDetailsRatingCountTv.visibility = View.VISIBLE
            mBinding.courseDetailsRatingBar.visibility = View.VISIBLE
            mBinding.courseDetailsTitleTv.visibility = View.VISIBLE
            mBinding.courseDetailsCategoryTv.visibility = View.VISIBLE
            mBinding.courseDetailsProgressBar.visibility = View.VISIBLE
            mBinding.courseDetailsProgressTv.visibility = View.VISIBLE
            mBinding.courseDetailsInTv.visibility = View.VISIBLE
            val padding = Utils.changeDpToPx(requireContext(),16f).toInt()
            mBinding.courseDetailsCollapsingLayout.setPadding(padding,0,padding,padding)
            mBinding.courseDetailsCollapsingLayout.requestLayout()
            var params = mBinding.courseDetailsCoordinatorLayout.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = Utils.changeDpToPx(requireContext(), 8f).toInt()
            mBinding.courseDetailsCoordinatorLayout.layoutParams = params
            playerFragment.setAspectRatio(16f/9f)
        }
    }

    private val mAddToCartCallback = object : ItemCallback<BaseResponse> {
        override fun onItem(res: BaseResponse, vararg args: Any) {
            Log.d("videoCipherData", "onitem")
            if (context == null) return

            if (res.isSuccessful) {
                (activity as MainActivity).updateCart()

                ToastMaker.show(
                    requireContext(),
                    getString(R.string.success),
                    res.message,
                    ToastMaker.Type.SUCCESS
                )
            } else {
                ToastMaker.show(
                    requireContext(),
                    getString(R.string.error),
                    res.message,
                    ToastMaker.Type.ERROR
                )
            }
        }
    }

    companion object {
        private const val TAG = "CourseDetailsFrag"
        private const val DISABLED = "disabled"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragCourseDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onResume() {
        super.onResume()
        if (mActivityFullScreenStarted) {
            mActivityFullScreenStarted = false

            val player = mBinding.courseDetailsPlayerView.player!!
            val playerState = PlayerHelper.playerState!!

            if (playerState.currentPosition != player.currentPosition) {
                player.seekTo(playerState.currentPosition)
                player.playWhenReady = false
            }

            if (playerState.isPlaying != player.isPlaying) {
                mVideoHelper?.playPauseVideo()
            }

            if (!player.isPlaying) {
                mVideoHelper?.setVideoPosition()
            }

        } else {
            mVideoHelper?.restorePlayerState()
        }
    }

    override fun onStop() {
        super.onStop()
        mVideoHelper?.savePlayerState()
    }

    override fun onDestroyView() {
        mSpecialOfferCountDown?.cancel()
        mVideoHelper?.release()
        super.onDestroyView()
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }

    fun showToolbar(){
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK
        toolbarOptions.endIcon = ToolbarOptions.Icon.CART

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.course_details)
    }

    private fun init() {
        mPresenter = CourseDetailsPresenterImpl(this)

        showToolbar()

        initAppBarLayout()
        initBottomSheet()

        mCourse = requireArguments().getParcelable(App.COURSE)!!
        mBinding.promoVideoButton.setOnClickListener(this)
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)

        if (mCourse.hasUserBought){
            mBinding.courseDetailsProgressTv.text =
                ("${mCourse.progress!!.toInt()}% ${getText(R.string.completed)}")

            mBinding.courseDetailsProgressBar.progress = mCourse.progress!!.toInt()
            mBinding.courseDetailsProgressBar.visibility = View.VISIBLE
            mBinding.courseDetailsProgressTv.visibility = View.VISIBLE
        }else {
            mBinding.courseDetailsProgressTv.visibility = View.GONE
            mBinding.courseDetailsProgressBar.visibility = View.GONE
        }

        mBinding.courseDetailsTitleTv.text = mCourse.title
        mBinding.courseDetailsInTv.text = getString(R.string._in_captical)
        mBinding.courseDetailsCategoryTv.text = mCourse.category
        if (mCourse.rating > 0f){
            mBinding.courseDetailsRatingBar.rating = mCourse.rating
            mBinding.courseDetailsRatingCountTv.text = mCourse.reviewsCount.toString()
        }else{
            mBinding.courseDetailsRatingBar.visibility = View.INVISIBLE
            mBinding.courseDetailsRatingCountTv.visibility = View.INVISIBLE
        }

        mCommonPresenter = CommonApiPresenterImpl.getInstance()
        mCommonPresenter.getCourseDetails(mCourse.id, this)

        playerFragment = childFragmentManager.findFragmentById(R.id.vdoCipherfragment) as VdoPlayerSupportFragment
        playerFragment.initialize(this)

    }

    private fun initBottomSheet() {
        mBottomSheetBehavoir = BottomSheetBehavior.from(mBinding.courseDetailsPurchaseBtnsContainer)
        mBottomSheetBehavoir.isHideable = true
        mBottomSheetBehavoir.isDraggable = false
        mBottomSheetBehavoir.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun obtainNewVdoParams(otp :String, playbackinfo :String): VdoInitParams {
        val vdoParams = VdoInitParams.Builder()
            .setOtp(otp)
            .setPlaybackInfo(playbackinfo)
            .setPreferredCaptionsLanguage("ar")
            .build()
        Log.i(TAG, "obtained new otp and playbackInfo")
        return vdoParams
    }


    override fun onInitializationSuccess(p0: PlayerHost?, p1: VdoPlayer?, p2: Boolean) {
        Log.d("VideoCipherInit", "Success")
        videoCipherPlayer = p1!!
        mBinding.videoCipherControlView.setPlayer(videoCipherPlayer)
        mBinding.videoCipherControlView.setFullscreenActionListener(fullscreenToggleListener)
//        mBinding.videoCipherControlView.setControllerVisibilityListener(visibilityListener)

        if (mCourse.video != null){
            mBinding.courseDetailsImg.visibility = View.GONE
            adjustLayoutToImg(R.id.videoCipherContainer)
            playerFragment.view!!.visibility = View.VISIBLE
            mBinding.videoCipherContainer.visibility = View.VISIBLE
        }
    }


    private val visibilityListener = object : VdoPlayerControlView.ControllerVisibilityListener {
        override fun onControllerVisibilityChange(visibility: Int) {
            Log.i(TAG, "controller visibility $visibility")
            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (visibility != View.VISIBLE) {
                    showSystemUi(false)
                }
            }
        }
    }

    private fun showSystemUi(show: Boolean) {
        Log.v(TAG, (if (show) "show" else "hide") + " system ui")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (!show) {
                requireActivity().window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
            } else {
                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            }
        }
    }

    private val fullscreenToggleListener = object : VdoPlayerControlView.FullscreenActionListener {
        override fun onFullscreenAction(enterFullscreen: Boolean): Boolean {
            showFullScreen(enterFullscreen)
            return true
        }
    }

    private fun showFullScreen(show: Boolean) {
        Log.v(TAG, (if (show) "enter" else "exit") + " fullscreen")

        requireActivity().requestedOrientation = if (show) {
            // go to landscape orientation for fullscreen mode
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            // go to portrait orientation
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
    }

    fun loadVideoCipherVideo(params : VdoInitParams){
        videoCipherPlayer.addPlaybackEventListener(videoCipherPlaybackListener)
        videoCipherPlayer.load(params)
    }

    override fun onInitializationFailure(p0: PlayerHost?, p1: ErrorDescription?) {
        Log.d("VideoCipherInit", "Failed " + p1!!.errorMsg)
        mBinding.courseDetailsImg.visibility = View.VISIBLE
        adjustLayoutToImg(R.id.course_details_img)
        playerFragment.view!!.visibility = View.GONE
        mBinding.videoCipherContainer.visibility = View.GONE
    }

    private val videoCipherPlaybackListener = object : VdoPlayer.PlaybackEventListener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.d("VideoCipherPlaybackListener", "onPlayerStateChanged")
        }

        override fun onTracksChanged(tracks: Array<Track>, tracks1: Array<Track>) {
            Log.d("VideoCipherPlaybackListener", "onTracksChanged")
        }

        override fun onBufferUpdate(bufferTime: Long) {
            Log.d("VideoCipherPlaybackListener", "onBufferUpdate")
        }

        override fun onSeekTo(millis: Long) {
            Log.d("VideoCipherPlaybackListener", "onSeekTO")
        }

        override fun onProgress(millis: Long) {}

        override fun onPlaybackSpeedChanged(speed: Float) {
            Log.d("VideoCipherPlaybackListener", "onPlaybackSpeedChanged $speed")
        }

        override fun onLoading(vdoInitParams: VdoInitParams) {
            Log.d("VideoCipherPlaybackListener", "onLoading")
        }

        override fun onLoadError(vdoInitParams: VdoInitParams, errorDescription: ErrorDescription) {
            val err = "onLoadError code: " + errorDescription.errorCode + ": " + errorDescription.errorMsg
            Log.d("VideoCipherPlaybackListener", "onLoadError "+ err)
        }

        override fun onLoaded(vdoInitParams: VdoInitParams) {
            Log.d("VideoCipherPlaybackListener", "onLoaded")
        }

        override fun onError(vdoParams: VdoInitParams, errorDescription: ErrorDescription) {
            val err = "onError code " + errorDescription.errorCode + ": " + errorDescription.errorMsg
            Log.d("VideoCipherPlaybackListener", "onError " + err)
        }

        override fun onMediaEnded(vdoInitParams: VdoInitParams) {
            Log.d("VideoCipherPlaybackListener", "onMediaEnded")
        }
    }



    private fun initAppBarLayout() {
        mBinding.courseDetailsAppBar.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->

            mBinding.courseDetailsCollapsingLayout.alpha =
                (1 - (Math.abs(verticalOffset) / appBarLayout.totalScrollRange)).toFloat()

            if (!mIsCollapsed && appBarLayout.totalScrollRange - Math.abs(verticalOffset) < 300) {
                //  Collapsed
                showOrHidePurchaseBtn(true)

                mIsCollapsed = true
                mVideoHelper?.savePlayerState()

            } else if (Math.abs(verticalOffset) < 100 && mIsCollapsed) {
                //Expanded

                showOrHidePurchaseBtn(false)
                mIsCollapsed = false
                mVideoHelper?.restorePlayerState()
            }
        })
    }

    private fun showOrHidePurchaseBtn(show: Boolean) {
        val currentTab = mBinding.courseDetailsTabLayout.selectedTabPosition
        if ((currentTab == 1 || currentTab == 2) && !mCourse.hasUserBought) {
            if (show) {
                showInfoBtnsAndDetails()
            } else {
                hideContainer()
            }
        }
    }

    private fun initPrice() {
        if (mCourse.priceWithDiscount != mCourse.price && mCourse.discount > 0) {
            mBinding.courseDetailsPriceWithDiscountValueTv.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            mBinding.courseDetailsPriceWithDiscountValueTv.text =
                Utils.formatPrice(requireContext(), mCourse.price)
            mBinding.courseDetailsPriceValueTv.text =
                Utils.formatPrice(requireContext(), mCourse.priceWithDiscount)
        } else {
            mBinding.courseDetailsPriceValueTv.text =
                Utils.formatPrice(requireContext(), mCourse.price)
        }


        mBinding.courseDetailsPriceKeyTv.visibility = View.VISIBLE
        mBinding.courseDetailsPriceValueTv.visibility = View.VISIBLE
        mBinding.courseDetailsPriceWithDiscountValueTv.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.promo_video_button -> {
                loadVideoCipherVideo(obtainNewVdoParams(mCourse.video?.otp!!, mCourse.video?.playbackInfo!!))
                showHideBtnPromo(false)
            }

            R.id.course_details_instructor_container -> {
                val bundle = Bundle()
                bundle.putParcelable(App.USER, mCourse.teacher)

                val frag = ProfileFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }

            R.id.course_details_more_btn -> {
                val bundle = Bundle()
                bundle.putParcelable(App.COURSE, mCourse)

                val dialog = ClassDetailsMoreDialog()
                dialog.arguments = bundle
                dialog.show(childFragmentManager, null)
            }

            R.id.course_details_subscribe_btn -> {
                if (mBinding.courseDetailsSubscribeBtn.tag == DISABLED) {
                    showCourseStartAlert()
                    return
                }

                if (!App.isLoggedIn()) {
                    (activity as MainActivity).goToLoginPage(null)
                    return
                }

                if (mCourse.authHasSubscription) {
                    val dialog = AppDialog()
                    val bundle = Bundle()
                    bundle.putString(App.TITLE, getString(R.string.subscribe))
                    bundle.putString(App.TEXT, getString(R.string.subscribe_desc))
                    dialog.arguments = bundle
                    dialog.setOnDialogBtnsClickedListener(
                        AppDialog.DialogType.YES_CANCEL,
                        object : AppDialog.OnDialogCreated {

                            override fun onCancel() {
                            }

                            override fun onOk() {
                                mLoadingDialog = LoadingDialog.instance
                                mLoadingDialog.show(childFragmentManager, null)

                                val addToCart = AddToCart()
                                addToCart.webinarId = mCourse.id

                                mPresenter.subscribe(addToCart)
                            }

                        })
                    dialog.show(childFragmentManager, null)
                } else {
                    (activity as MainActivity).transact(SubscriptionFrag())
                }
            }

            R.id.course_details_enroll_btn -> {
                if (mBinding.courseDetailsEnrollBtn.tag == DISABLED) {
                    showCourseStartAlert()
                    return
                }

                if (!App.isLoggedIn()) {
                    (activity as MainActivity).goToLoginPage(null)
                    return
                }

                val btnText = mBinding.courseDetailsEnrollBtn.text

                if (btnText == getString(R.string.write_a_review)) {
                    val bundle = Bundle()
                    bundle.putInt(App.ID, mCourse.id)

                    val dialog = CourseReviewDialog()
                    dialog.setOnReviewSavedListener(mOnReviewAdded)
                    dialog.arguments = bundle
                    dialog.show(childFragmentManager, null)

                } else if (btnText == getString(R.string.leave_a_comment)) {
                    val bundle = Bundle()
                    bundle.putInt(App.ID, mCourse.id)
                    bundle.putSerializable(App.SELECTION_TYPE, CommentDialog.Type.COMMENT_COURSE)

                    val dialog = CommentDialog.instance
//                    dialog.setOnCommentSavedListener(mOnCommentAdded)
                    dialog.arguments = bundle
                    dialog.show(childFragmentManager, null)

                } else if (btnText == getString(R.string.go_to_learning_page)) {
                    val bundle = Bundle()
                    bundle.putParcelable(App.COURSE, mCourse)

                    val learningPage = CourseLearningTabsFrag()
                    learningPage.arguments = bundle
                    (activity as MainActivity).transact(learningPage)

                } else if (btnText == getText(R.string.add_to_cart)) {
                    if ((mCourse.price == 0.0 &&
                                (mCourse.pricingPlans.isEmpty() || allPricingPlansAreDisabled())) ||
                        ((mCourse.pricingPlans.isEmpty() || allPricingPlansAreDisabled()) &&
                                !mCourse.canBuyWithPoints)
                    ) {
                        if (mCourse.price == 0.0) {
                            addFreeCourse()
                        } else {
                            val addToCart = AddToCart()
                            addToCart.webinarId = mCourse.id

                            val presenter = CommonApiPresenterImpl.getInstance()
                            presenter.addToCart(addToCart, mAddToCartCallback)
                        }
                    } else {
                        val bundle = Bundle()
                        bundle.putParcelable(App.COURSE, mCourse)

                        val dialog = PricingPlansDialog()
                        dialog.arguments = bundle
                        dialog.show(childFragmentManager, null)
                    }
                }
            }

            R.id.course_details_buy_btn -> {
                val mAmounts = Amounts()
                mAmounts.total = mCourse.price
                mAmounts.total_usd = mCourse.price_usd
                mAmounts.total_kd = mCourse.price_kd
                paymentDialog = PaymentOptionsDialog(mAmounts, mCourse.id, null)
                paymentDialog.show(childFragmentManager, null)
            }

            R.id.player_play_pause_btn -> {
                mVideoHelper!!.playPauseVideo()
            }

            R.id.player_controller_mute_btn -> {
                mVideoHelper!!.muteUnmuteVideo()
            }

            R.id.player_controller_fullscreen_btn -> {
                showVideoInFullscreen(true)
            }
        }
    }

    private fun showCourseStartAlert() {
        ToastMaker.show(
            requireContext(),
            getString(R.string.error),
            getString(R.string.course_started),
            ToastMaker.Type.ERROR
        )
    }

    private fun addFreeCourse() {
        val dialog = AppDialog.instance
        val bundle = Bundle()

        bundle.putString(App.TITLE, getString(R.string.add))
        bundle.putString(App.TEXT, getString(R.string.add_to_user_course))
        dialog.arguments = bundle
        dialog.setOnDialogBtnsClickedListener(
            AppDialog.DialogType.YES_CANCEL,
            object : AppDialog.OnDialogCreated {

                override fun onCancel() {
                    dialog.dismiss()
                }

                override fun onOk() {
                    mLoadingDialog = LoadingDialog.instance
                    mLoadingDialog.show(childFragmentManager, null)
                    mPresenter.addCourseToUserCourse(mCourse.id)
                }
            })

        dialog.show(childFragmentManager, null)
    }

    private fun allPricingPlansAreDisabled(): Boolean {
        for (plan in mCourse.pricingPlans) {
            if (plan.isValid) {
                return false
            }
        }

        return true
    }

    private fun showInfoBtnsAndDetails() {
        if (this::mCurrentBtnsState.isInitialized && mCurrentBtnsState == BtnContainerState.INFO) {
            return
        }

        if (mCourse.hasUserBought) {
            hideSubscribeBtn()

            mBinding.courseDetailsEnrollBtn.text = getString(R.string.go_to_learning_page)

            if (mCourse.progress != null) {
                mBinding.courseDetailsPriceKeyTv.text =
                    ("${mCourse.progress!!.toInt()}% ${getText(R.string.completed)}")

                mBinding.courseDetailsLinearProgressBar.progress = mCourse.progress!!.toInt()
                mBinding.courseDetailsLinearProgressBar.visibility = View.VISIBLE
                mBinding.courseDetailsPriceKeyTv.visibility = View.VISIBLE
            } else {
                mBinding.courseDetailsPriceKeyTv.visibility = View.GONE
                mBinding.courseDetailsPriceValueTv.visibility = View.GONE
            }

//            showContainer()

        } else {

            if (mCourse.isSubscribable) {
                showSubscribeBtn()
            } else {
                hideSubscribeBtn()
            }

            initPrice()
            mBinding.courseDetailsEnrollBtn.text = getText(R.string.add_to_cart)
            mBinding.courseDetailsBuyBtn.visibility = View.VISIBLE
            showContainer()

            if (mCourse.isLive() && mCourse.liveCourseStatus != Course.WebinarStatus.NOT_CONDUCTED.value) {
                disableContainer()
            }
        }
    }

    private fun disableContainer() {
        mBinding.courseDetailsEnrollBtn.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.gray81)
        mBinding.courseDetailsSubscribeBtn.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.gray81)

        mBinding.courseDetailsEnrollBtn.tag = DISABLED
        mBinding.courseDetailsSubscribeBtn.tag = DISABLED
    }

    private fun initTabs() {
        val tabLayout = mBinding.courseDetailsTabLayout
        val viewPager = mBinding.courseDetailsViewPager

        val bundle = Bundle()
        bundle.putParcelable(App.COURSE, mCourse)
        bundle.putBoolean("viewPager", true)

//        val contentFrag = CourseDetailsContentFrag()
        val content2Frag = CourseLearningTabsFrag()
        val informationFrag = CourseDetailsInformationFrag()
        val reviewsFrag = CourseDetailsReviewsFrag()
        val commentsFrag = CourseDetailsCommentsFrag()
        informationFrag.arguments = bundle
//        contentFrag.arguments = bundle
        content2Frag.arguments = bundle
        reviewsFrag.arguments = bundle
        commentsFrag.arguments = bundle

        val adapter = ViewPagerAdapter(childFragmentManager)
//        adapter.add(contentFrag, getString(R.string.content))
        adapter.add(content2Frag, getString(R.string.content))
        adapter.add(informationFrag, getString(R.string.information))
        adapter.add(reviewsFrag, getString(R.string.reviews))
        adapter.add(commentsFrag, getString(R.string.comments))

        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)
    }

    private fun initVideoImg() {
        when {
//            mCourse.video != null && URLUtil.isValidUrl(mCourse.video) -> {
//                mBinding.courseDetailsPlayerView.setKeepContentOnPlayerReset(true)
//                mBinding.courseDetailsPlayerView.visibility = View.VISIBLE
//                mBinding.courseDetailsPlayerControllerView.root.visibility = View.VISIBLE
//
//                mVideoHelper = FileVideoPlayerHelper(requireContext())
//                mVideoHelper!!.setOnCallbackListener(this)
//                mBinding.courseDetailsPlayerView.player = null
//                mBinding.courseDetailsPlayerView.player = mVideoHelper!!.player
////                mVideoHelper!!.initPlayer(mCourse.video!!, null)
//                initVideoController()
//            }
            mCourse.img != null -> {
                Glide.with(requireContext()).load(mCourse.img).into(mBinding.courseDetailsImg)
                mBinding.courseDetailsPlayerView.visibility = View.GONE

                mBinding.courseDetailsPlayerControllerView.root.visibility = View.GONE
//                adjustLayoutToImg(R.id.course_details_img)
//                mBinding.courseDetailsImg.visibility = View.VISIBLE
            }
            else -> {
                adjustLayoutToImg(R.id.course_details_category_tv)
            }
        }
    }

    private fun adjustLayoutToImg(layout: Int) {
        val params =
            mBinding.courseDetailsMoreBtn.layoutParams as ConstraintLayout.LayoutParams

        params.topToBottom = layout
        mBinding.courseDetailsMoreBtn.requestLayout()
    }

    fun showHideBtnPromo(show: Boolean){
        if (show){
            mBinding.promoVideoButton.visibility = View.VISIBLE
        }else{
            mBinding.promoVideoButton.visibility = View.GONE
        }
    }

    private fun initTeacher() {
        val teacher = mCourse.teacher

        mBinding.courseDetailsInstructorContainer.setOnClickListener(this)

        if (teacher.avatar != null) {
            Glide.with(requireContext()).load(teacher.avatar)
                .into(mBinding.courseDetailsInstructorImg)
        }

        if (teacher.offline.toBoolean()) {
            mBinding.courseDetailsInstructorNotAvailableContainer.HeaderInfoImg
                .setBackgroundResource(R.drawable.circle_blue)
            mBinding.courseDetailsInstructorNotAvailableContainer.HeaderInfoImg
                .setImageResource(R.drawable.ic_danger)
            mBinding.courseDetailsInstructorNotAvailableContainer.HeaderInfoTitleTv.text =
                getString(R.string.temp_unavailable)

            val tempTxt = "${teacher.name}  ${getString(R.string.is_temp_unavailable)}"
            mBinding.courseDetailsInstructorNotAvailableContainer.HeaderInfoDescTv.text = tempTxt
            mBinding.courseDetailsInstructorNotAvailableContainer.root.visibility = View.VISIBLE
        }

        mBinding.courseDetailsInstructorNameTv.text = teacher.name
        mBinding.courseDetailsInstructorRatingBar.rating = teacher.rating
    }

    private fun initSpecialOffer(specialOffer: SpecialOffer) {
        val specialOfferTime = specialOffer.toDate * 1000

        if (specialOfferTime > System.currentTimeMillis()) {

            mBinding.courseDetailsSpecialOfferPercentTv.text = ("${specialOffer.percent}%")

            val offerTime = specialOfferTime - System.currentTimeMillis()

            setSpecialTime(offerTime, true)

            mSpecialOfferCountDown =
                object : CountDownTimer(offerTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        setSpecialTime(millisUntilFinished, false)
                    }

                    override fun onFinish() {
                        mCourse.activeSpecialOffer = null
                        mBinding.courseDetailsSpecialOfferContainer.visibility = View.GONE
                    }
                }
            mSpecialOfferCountDown!!.start()

            mBinding.courseDetailsSpecialOfferContainer.visibility = View.VISIBLE
        }
    }

    private fun setSpecialTime(millis: Long, init: Boolean) {
        initSpecialOfferViews()

        var totalSeconds: Long = (millis + 500) / 1000

        val days = totalSeconds / (3600 * 24)
        if (days > 0) {
            totalSeconds -= days * 24 * 3600
        }

        val secs = totalSeconds % 60
        val mins = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600

        if (init && days > 0) {
            mShowDays = true
            mBinding.courseDetailsSpecialOfferDaysTv.visibility = View.VISIBLE
        }

        if (init && (mShowDays || hours > 0)) {
            mShowHours = true
            mBinding.courseDetailsSpecialOfferHoursTv.visibility = View.VISIBLE
        }

        if (init) {
            mBinding.courseDetailsSpecialOfferMinsTv.visibility = View.VISIBLE
            mBinding.courseDetailsSpecialOfferSecondsTv.visibility = View.VISIBLE
        }

        if (mShowDays) {
            mBinding.courseDetailsSpecialOfferDaysTickerView.text =
                (" ${String.format("%02d", days)} :")
        }

        if (mShowHours) {
            mBinding.courseDetailsSpecialOfferHoursTickerView.text =
                (" ${String.format("%02d", hours)} :")
        }

        mBinding.courseDetailsSpecialOfferMinsTickerView.text =
            (" ${String.format("%02d", mins)} :")
        mBinding.courseDetailsSpecialOfferSecondsTickerView.text =
            (" ${String.format("%02d", secs)} ")

        if (init && BuildVars.LOGS_ENABLED) {
            Log.d(TAG, "setSpecialTime: days:${days}")
            Log.d(TAG, "setSpecialTime: hours:${hours}")
            Log.d(TAG, "setSpecialTime: mins:${mins}")
            Log.d(TAG, "setSpecialTime: secs:${secs}")
        }
    }

    private fun initSpecialOfferViews() {
        mBinding.courseDetailsSpecialOfferSecondsTickerView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.courseDetailsSpecialOfferSecondsTickerView.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.bold)

        mBinding.courseDetailsSpecialOfferMinsTickerView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.courseDetailsSpecialOfferMinsTickerView.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.bold)

        mBinding.courseDetailsSpecialOfferHoursTickerView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.courseDetailsSpecialOfferHoursTickerView.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.bold)

        mBinding.courseDetailsSpecialOfferDaysTickerView.setCharacterLists(TickerUtils.provideNumberList())
        mBinding.courseDetailsSpecialOfferDaysTickerView.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.bold)
    }

    private fun hideSubscribeBtn() {
        if (mBinding.courseDetailsSubscribeBtn.visibility == View.GONE) {
            return
        }

        mBinding.courseDetailsSubscribeBtn.visibility = View.GONE

        val params =
            mBinding.courseDetailsEnrollBtn.layoutParams as ConstraintLayout.LayoutParams

        params.marginEnd = Utils.changeDpToPx(requireContext(), 16f).toInt()
        mBinding.courseDetailsEnrollBtn.requestLayout()
    }

    private fun showSubscribeBtn() {
        if (mBinding.courseDetailsSubscribeBtn.isVisible) {
            return
        }

        val params =
            mBinding.courseDetailsEnrollBtn.layoutParams as ConstraintLayout.LayoutParams

        params.marginEnd = Utils.changeDpToPx(requireContext(), 10f).toInt()
        mBinding.courseDetailsEnrollBtn.requestLayout()

        mBinding.courseDetailsSubscribeBtn.visibility = View.VISIBLE
    }

    fun onSubscribed(response: BaseResponse) {
        if (context == null) return

        if (response.isSuccessful) {
            mCommonPresenter.getCourseDetails(mCourse.id, this)
        } else {
            mLoadingDialog.dismiss()
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }

    fun refreshPage() {
        mLoadingDialog = LoadingDialog.instance
        mLoadingDialog.show(childFragmentManager, null)
        mCommonPresenter.getCourseDetails(mCourse.id, this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        Log.d(TAG, "onTabReselected")
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        Log.d(TAG, "onTabUnselected")
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        Log.d(TAG, "onTabSelected")

        when (tab.text) {
            getString(R.string.information) -> {
//                showInfoBtnsAndDetails()
                if (mCourse.hasUserBought) {
                    hideContainer()
                } else {
                    showInfoBtnsAndDetails()
                }
            }

            getString(R.string.content) -> {
//                if (mCourse.hasUserBought || !mIsCollapsed) {
//                    hideContainer()
//                } else {
//                    showOrHidePurchaseBtn(true)
//                }
                if (mCourse.hasUserBought) {
                    hideContainer()
                } else {
                    showInfoBtnsAndDetails()
                }
            }

            getString(R.string.reviews) -> {
                hideContainerTvs()
                showOnlyEnrollBtn()
                showReviewBtn()
            }

            getString(R.string.comments) -> {
                showContainer()
                hideContainerTvs()
                showOnlyEnrollBtn()
                showCommentBtn()
            }
        }
    }

    private fun hideContainerTvs() {
        mBinding.courseDetailsPriceWithDiscountValueTv.visibility = View.GONE
        mBinding.courseDetailsPriceKeyTv.visibility = View.GONE
        mBinding.courseDetailsPriceValueTv.visibility = View.GONE
        mBinding.courseDetailsLinearProgressBar.visibility = View.GONE
    }

    private fun hideContainer() {
        mBottomSheetBehavoir.state = BottomSheetBehavior.STATE_HIDDEN
        mCurrentBtnsState = BtnContainerState.HIDE
    }

    private fun showOnlyEnrollBtn() {
        hideSubscribeBtn()
        if (!mBinding.courseDetailsEnrollBtn.isVisible) {
            mBinding.courseDetailsEnrollBtn.visibility = View.VISIBLE
            mBinding.courseDetailsBuyBtn.visibility = View.VISIBLE
        }
    }

    private fun showContainer() {
        mBottomSheetBehavoir.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun showCommentBtn() {
        mBinding.courseDetailsEnrollBtn.text = getString(R.string.leave_a_comment)
        mBinding.courseDetailsBuyBtn.visibility = View.GONE
        mCurrentBtnsState = BtnContainerState.COMMENT
    }

    private fun showReviewBtn() {
        mBinding.courseDetailsBuyBtn.visibility = View.GONE
        if (mCourse.hasUserBought) {
            var showBtn = true
            for (review in mCourse.reviews) {
                if (review.auth) {
                    showBtn = false
                    break
                }
            }

            if (showBtn) {
                showContainer()
                mCurrentBtnsState = BtnContainerState.REVIEW
                mBinding.courseDetailsEnrollBtn.text = getString(R.string.write_a_review)
            } else {
                mCurrentBtnsState = BtnContainerState.HIDE
                hideContainer()
            }
        } else if (mIsCollapsed) {
            showOrHidePurchaseBtn(true)
        } else {
            hideContainer()
        }
    }

    override fun onItem(item: Course, vararg args: Any) {
        if (context == null) return
        try {
            if (item.hasUserBought) {
                App.saveToLocal(
                    Gson().toJson(item),
                    requireContext(),
                    AppDb.DataType.COURSE,
                    item.id.toString()
                )
            }

            mLoadingDialog.dismiss()

            mCourse = item

            loadVideoCipherVideo(obtainNewVdoParams(mCourse.video?.otp!!, mCourse.video?.playbackInfo!!))

            mBinding.courseDetailsMoreBtn.setOnClickListener(this)
            mBinding.courseDetailsSubscribeBtn.setOnClickListener(this)
            mBinding.courseDetailsEnrollBtn.setOnClickListener(this)
            mBinding.courseDetailsBuyBtn.setOnClickListener(this)

            initTeacher()
            initTabs()
            initVideoImg()

            if (!mCourse.hasUserBought && mCourse.activeSpecialOffer != null) {
                initSpecialOffer(mCourse.activeSpecialOffer!!)
            }

            showInfoBtnsAndDetails()
        } catch (ex: IllegalStateException) {
        }
    }

    private fun showVideoInFullscreen(requestLanscape: Boolean) {
        mActivityFullScreenStarted = true
        val player = mVideoHelper!!.player

        val playerState = PlayerState()
        playerState.currentPosition = player.currentPosition
        playerState.isPlaying = player.isPlaying
//        playerState.path = mCourse.video!!
        playerState.playerType = PlayerHelper.Type.LOCAL

        PlayerHelper.playerState = playerState
        val intent = Intent(requireContext(), VideoPlayerActivity::class.java)
        intent.putExtra(App.REQUEST_LANDSCAPE, requestLanscape)
        startActivity(intent)
    }

    private fun initVideoController() {
        mBinding.courseDetailsPlayerControllerView.playerPlayPauseBtn.setOnClickListener(this)
        mBinding.courseDetailsPlayerControllerView.playerControllerMuteBtn.setOnClickListener(this)
        mBinding.courseDetailsPlayerControllerView.playerControllerFullscreenBtn.setOnClickListener(
            this
        )
        mBinding.courseDetailsPlayerView.videoSurfaceView?.setOnClickListener {
            showVideoInFullscreen(false)
        }
    }

    private fun initVideoPlayerView(width: Int, height: Int) {
        mBinding.courseDetailsPlayerView.post {
            mBinding.courseDetailsPlayerView.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val layoutParams = mBinding.courseDetailsPlayerView.layoutParams

                    var changed = false

                    if (height < layoutParams.height) {
                        changed = true
                        layoutParams.height = height
                    }

                    if (width < layoutParams.width) {
                        changed = true
                        layoutParams.width = width
                    }

                    if (changed) {
                        mBinding.courseDetailsPlayerView.requestLayout()
                    }

                    outline.setRoundRect(
                        Rect(0, 0, width, height),
                        Utils.changeDpToPx(requireContext(), 20f)
                    )
                }
            }

            mBinding.courseDetailsPlayerView.clipToOutline = true
        }
    }

    override fun onMute(mute: Boolean) {
        if (mute) {
            mBinding.courseDetailsPlayerControllerView.playerControllerMuteBtn
                .setImageResource(R.drawable.ic_mute)
        } else {
            mBinding.courseDetailsPlayerControllerView.playerControllerMuteBtn
                .setImageResource(R.drawable.ic_sound)
        }
    }

    override fun onVideoPaused() {
        mBinding.courseDetailsPlayerControllerView.playerPlayPauseBtn
            .setImageResource(R.drawable.ic_play_circle)
    }

    override fun onVideoPlayed() {
        mBinding.courseDetailsPlayerControllerView.playerPlayPauseBtn
            .setImageResource(R.drawable.ic_pause_circle)
    }

    override fun timeToString(millis: Long): String? {
        return Utils.getTimeWithNoSpace(millis)
    }

    override fun onUpdateCurrentPosition(currentPosition: String, videoDuration: String) {
        mBinding.courseDetailsPlayerControllerView.playerControllerTv.text =
            ("$currentPosition / $videoDuration")
    }

    override fun onFinished() {
        mBinding.courseDetailsPlayerControllerView.playerPlayPauseBtn
            .setImageResource(R.drawable.ic_play_circle)
    }

    override fun onSurfaceSizeChanged(width: Int, height: Int) {
        initVideoPlayerView(width, height)
    }

    fun onCourseAdded(response: BaseResponse) {
        if (response.isSuccessful) {
            mCommonPresenter.getCourseDetails(mCourse.id, this)
        } else {
            mLoadingDialog.dismiss()
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                response.message,
                ToastMaker.Type.ERROR
            )
        }
    }



}