package com.online.coursestore.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.online.coursestore.R
import com.online.coursestore.databinding.ActivitySplashScreenBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.BuildVars
import com.online.coursestore.manager.PrefManager
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.db.AppDb
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.manager.net.RetryListener
import com.online.coursestore.manager.net.observer.NetworkObserverActivity
import com.online.coursestore.model.Language
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.SplashScreenPresenterImpl
import com.online.coursestore.ui.frag.ErrorFrag


@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : NetworkObserverActivity(), View.OnClickListener {

    private var mStartTime: Long = 0
    private var mShouldLogin = true
    private lateinit var mBinding: ActivitySplashScreenBinding
    private lateinit var mPresenter: Presenter.SplashScreenPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        changeLocale()

        mBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        App.currentActivity = this
        initToolbar()
        setStatusBarColor(R.color.accent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.hide(WindowInsets.Type.systemBars())
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        mStartTime = System.currentTimeMillis()
        mBinding.splashToolbarBackImg.setOnClickListener(this)

        val rotateAnim = AnimationUtils.loadAnimation(this, R.anim.rotate)
        mBinding.splashScreenImgContainer.animation = rotateAnim

        mPresenter = SplashScreenPresenterImpl(this)
        mPresenter.getAppConfig()
    }

    private fun initToolbar() {
        val statusBarHeight = App.statusBarHeight(this)
        mBinding.splashToolbarMargin.post {
            if (mBinding.splashToolbarMargin.height != statusBarHeight) {
                val toolbarParams =
                    mBinding.splashToolbarMargin.layoutParams as ConstraintLayout.LayoutParams
                toolbarParams.height = statusBarHeight
                mBinding.splashToolbarMargin.requestLayout()
            }
        }
    }

    private fun goToNextPage() {
        if (System.currentTimeMillis() - mStartTime > 1000) {
            checkIfUserLoggedIn()
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                checkIfUserLoggedIn()
            }, 1000 - System.currentTimeMillis() + mStartTime)
        }
    }

    private fun checkIfUserLoggedIn() {
        val appDb = AppDb(this)
        val token = appDb.getData(AppDb.DataType.TOKEN)

        val prefManager = PrefManager(this)

        val activity =
            if (mShouldLogin && prefManager.isFirstTimeLaunch) OnboardingActivity::class.java else if (mShouldLogin && !prefManager.skipLogin)
                SignInActivity::class.java else MainActivity::class.java

        startActivity(Intent(this, activity))
        finish()

        appDb.close()
    }

    private fun changeLocale() {
        val prefManager = PrefManager(this)

        if (prefManager.language != null) {
            BaseActivity.language = prefManager.language!!
            Utils.changeLocale(this, prefManager.language!!.code)

            ApiService.createApiServiceWithLocale(prefManager.language!!.code)
        } else {
            val language = Language()
            language.name = BuildVars.DefaultLang.NAME
            language.code = BuildVars.DefaultLang.CODE
            language.isDefault = true
            prefManager.language = language
            BaseActivity.language = language
        }
    }

    fun onAppConfigReceived() {
        App.saveToLocal(
            Gson().toJson(App.appConfig),
            this,
            AppDb.DataType.APP_CONFIG
        )
        goToNextPage()
    }

    override fun onBackPressed() {
        if (ErrorFrag.isFragVisible) {
            App.showExitDialog(this)
        } else {
            super.onBackPressed()
        }
    }

    fun showNoNetFrag() {
        if (isDestroyed || isFinishing) return

        val errorFrag = ErrorFrag.getInstance()
        errorFrag.addOnRetryListener(RetryListener { mPresenter.getAppConfig() })

        val bundle = Bundle()
        bundle.putBoolean(App.COURSES, true)
        errorFrag.arguments = bundle

        try {
            transact(errorFrag)
        } catch (ex: IllegalStateException) {
        }
    }

    fun transact(
        frag: Fragment,
        addToBackstack: Boolean = true
    ) {
        var transaction =
            supportFragmentManager.beginTransaction()
                .replace(R.id.splashScreenContainer, frag)

        if (addToBackstack) {
            transaction = transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    fun setStatusBarColor(color: Int) {
        val colorRes = ContextCompat.getColor(this, color)
        window.statusBarColor = colorRes
        mBinding.splashToolbarMargin.setBackgroundColor(colorRes)
    }

    fun showToolbar(title: String) {
        setStatusBarColor(R.color.white)
        mBinding.splashContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.pageBg))
        mBinding.splashToolbarTitleTv.text = title
        if (!mBinding.splashToolbar.isVisible) {
            mBinding.splashToolbarMargin.visibility = View.VISIBLE
            mBinding.splashToolbar.visibility = View.VISIBLE
        }
    }

    fun hideToolbar() {
        setStatusBarColor(R.color.accent)
        mBinding.splashContainer.setBackgroundResource(R.drawable.gradient_splash)
        if (mBinding.splashToolbar.isVisible) {
            mBinding.splashToolbarMargin.visibility = View.GONE
            mBinding.splashToolbar.visibility = View.GONE
        }
    }

    fun showToolbar(title: Int) {
        showToolbar(getString(title))
    }

    override fun onClick(v: View?) {
        onBackPressed()
    }

}