package com.online.coursestore.manager

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.online.coursestore.BuildConfig
import com.online.coursestore.R
import com.online.coursestore.manager.db.AppDb
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.model.*
import com.online.coursestore.model.view.ViewShape
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.widget.AppDialog
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.config.CheckoutConfig
import com.paypal.checkout.config.Environment
import com.paypal.checkout.config.SettingsConfig
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.UserAction

class App : Application() {

    companion object {
        const val MY_FATOORAH_API_KEY = "5lzmSdHb6R3FAE3Jh4YI2cgrMpzyeNoiTnw1bTVxJW78nRMzQsg5AmddXUenNQJpWxVteF_igHH5kLHNdNR8oH7gxrybg_WxODHVxWga9iqmq_yPfoMt9wMtKOT247dtZxWtke2hz6WPumpj2wBx8sPeXgX0YlAzI6y0GBdp-xf__5aKokr3E5bAKwXMBxmMFuz8-BSfE9tyMPMCdQshJMiYtGeF3LA5TCIZbLvetrdWw1Hiq6SNVqFRm_Ch-h7bbzrcS3fiBtLZo2-XvKVUpS6oCGdKw_5KsTk-vAVSnmdY6RKcsMP8Tz7tKvbCK8tAo6uLPhkfNm7SeFQfrJ_zNzoyNutl35c3f4UP6MzFTBX_A08RdLMxgrbhBnGaOmvwZZYft8rnSJ978nurXHQccoapgUX1wz6y2yPcVH7dXrSB7UFMV27W88TBfJL33Hw7-vqDbSxaUc261G1CMUr4NkCNHVLUVFgz8jPbnRW-AOMdkm2blXZDT5mhGFIYbRkQn3W_c15kQ-BqOKF9NVnA-o9ihhHtTyygST0WHfWZ0Q3Zd3pF9hAq34f3Y2Aj5RGHvZXqlcNF3cI3D0WL102fmtygM_Cfiq4GujCdRDMxMiNtkdeZtE-a05kXVO4TITC1e6AHQ2jVNqUTalem66lum-wXqtY8vqaoL7HmKmBw1fhhgr3sqYN3ALTaALfbvucLRuGee0harFjwGyJXU1rdgZiYfwsxzvqllIY0ZlfIhhRoiZhN"
        const val MY_FATOORAH_API_KEY_TEST = "rLtt6JWvbUHDDhsZnfpAhpYk4dxYDQkbcPTyGaKp2TYqQgG7FGZ5Th_WD53Oq8Ebz6A53njUoo1w3pjU1D4vs_ZMqFiz_j0urb_BH9Oq9VZoKFoJEDAbRZepGcQanImyYrry7Kt6MnMdgfG5jn4HngWoRdKduNNyP4kzcp3mRv7x00ahkm9LAK7ZRieg7k1PDAnBIOG3EyVSJ5kK4WLMvYr7sCwHbHcu4A5WwelxYK0GMJy37bNAarSJDFQsJ2ZvJjvMDmfWwDVFEVe_5tOomfVNt6bOg9mexbGjMrnHBnKnZR1vQbBtQieDlQepzTZMuQrSuKn-t5XZM7V6fCW7oP-uXGX-sMOajeX65JOf6XVpk29DP6ro8WTAflCDANC193yof8-f5_EYY-3hXhJj7RBXmizDpneEQDSaSz5sFk0sV5qPcARJ9zGG73vuGFyenjPPmtDtXtpx35A-BVcOSBYVIWe9kndG3nclfefjKEuZ3m4jL9Gg1h2JBvmXSMYiZtp9MR5I6pvbvylU_PP5xJFSjVTIz7IQSjcVGO41npnwIxRXNRxFOdIUHn0tjQ-7LwvEcTXyPsHXcMD8WtgBh-wxR8aKX7WPSsT1O8d8reb2aR7K3rkV3K82K_0OgawImEpwSvp9MNKynEAJQS6ZHe_J_l77652xwPNxMRTMASk1ZsJL"
        const val PAYPAL_CLIENT_ID_LIVE = "AfQhBM1_cU33-2jbs93wW3Kyl0H9Bydk7XpdXEBihTXPJzsRuwO2VQ9OEwi0mmLJutDZUJfT3t0nqHNI"
        const val PAYPAL_CLIENT_ID_SANDBOX   = "AUOyTa5-7H18jcuy5G1OKPUbJ4c0xLh02wEq436O9Z9w1_M-usliUuizqTKQSTJ32jn0DRoJnwlwGeiZ"
        const val ONESIGNAL_APP_ID   = "8b6ed273-a676-4004-a9a4-3e3e198159d5"
        const val VISA_MASTER = "Visa/Master"
        const val PAYPAL = "Paypal"
        const val PAYMENT_SUCCESS = "PaymentSuccess"
        const val VERIFY_PAYMENT_SUCCESS = "paid"
        const val VERIFY_PAYMENT_FAILED = "fail"
        const val SAUDI_CREDIT_CARD = "Saudi Credit Card"
        const val DEBIT_CARDS_UAE = "Debit Cards UAE"
        const val QATAR_DEBIT_CARD = "Qatar Debit Card"
        const val KNET = "KNET"
        const val VISA_MASTER_ID = 2
        const val SAUDI_CREDIT_CARD_ID = 2
        const val DEBIT_CARDS_UAE_ID = 8
        const val QATAR_DEBIT_CARD_ID = 7
        const val KNET_ID = 1
        const val PAYMENT_STATUS = "paymentStatus"
        const val IS_CANCELABLE = "is_cancelable"
        const val IS_LOGIN = "isLogin"
        const val IS_CHARGE_ACCOUNT = "isChargeAccount"
        const val SELECTION_TYPE = "selectionType"
        const val REQUEDT_TO_LOGIN_FROM_INSIDE_APP = "login_request"
        const val TITLE = "title"
        const val TEXT = "text"
        const val IMG = "img"
        const val BUTTON = "button"
        const val SHOULD_REGISTER = "shouldRegister"
        const val SUCCESSED_LOGIN = "SuccessedLogin"
        const val USER_ID = "userId"
        const val SIGN_UP = "signUp"
        const val KEY = "key"
        const val COURSES = "courses"
        const val BADGES = "badges"
        const val COURSE = "coursestore"
        const val USERS = "users"
        const val USER = "user"
        const val ORGANIZATIONS = "organizations"
        const val CATEGORY = "category"
        const val FILTERS = "filters"
        const val SELECTED = "selected"
        const val BLOG = "blog"
        const val COMMENT = "comment"
        const val COMMENTS = "comments"
        const val ID = "id"
        const val MEETINGS = "meetings"
        const val CERTIFICATES = "certificates"
        const val CERTIFICATE = "certificate"
        const val MEETING = "meeting"
        const val URL = "url"
        const val DIR = "dir"
        const val NOTIF = "notif"
        const val SHOW_ADD = "show_add"
        const val TICKET = "ticket"
        const val POSITION = "position"
        const val RESULT = "result"
        const val REDIRECTION = "redirection"
        const val QUIZ = "quiz"
        const val QUIZZES = "quizzes"
        const val INSTRUCTOR_TYPE = "instructorType"
        const val IS_FROM_QUIZ_PAGE = "isFromQuizPage"
        const val AMOUNT = "amount"
        const val PAYOUT_ACCOUT = "payoutAccount"
        const val FINANCIAL = "financial"
        const val ITEM = "item"
        const val ORDER = "order"
        const val ITEMS = "items"
        const val USE_GRID = "useGrid"
        const val REQUEST_LANDSCAPE = "requestLandscape"
        const val CHAPTER_POSITION = "chapterPosition"
        const val CHAPTER_ITEM_POSITION = "chapterItemPosition"
        const val EMPTY_STATE = "emptyState"
        const val OFFLINE = "offline"
        const val ATTACHMENT_POSITION = "attachmentPosition"
        const val TO_DOWNLOADS = "toDownloads"
        const val FILE_NAME_FROM_HEADER = "fileNameFromHeader"
        const val NESTED_ENABLED = "nestedEnabled"

        lateinit var appConfig: AppConfig
        lateinit var currentActivity: AppCompatActivity
        var currentFrag: Fragment? = null
        var loggedInUser: UserProfile? = null
        var quickInfo: QuickInfo? = null


        enum class RegistrationProvider(private val type: Int) {
            GOOGLE(1), FACEBOOK(2);

            fun value(): Int {
                return type
            }
        }

        enum class Registration(private val type: String) {
            EMAIL("email"), MOBILE("mobile");

            fun value(): String {
                return type
            }
        }

        enum class Directory(private val dirName: String) {
            TICKETS_ATTACHMENT("TicketAttachments"),
            CERTIFICATE("Cert"),
            DOWNLOADS("Downloads");

            fun value(): String {
                return dirName
            }
        }

        fun showExitDialog(activity: AppCompatActivity) {
            val bundle = Bundle()
            bundle.putString(App.TITLE, activity.getString(R.string.exit))
            bundle.putString(App.TEXT, activity.getString(R.string.exit_desc))

            val appDialog = AppDialog.instance
            appDialog.arguments = bundle
            appDialog.setOnDialogBtnsClickedListener(
                AppDialog.DialogType.YES_CANCEL,
                object : AppDialog.OnDialogCreated {
                    override fun onCancel() {
                    }

                    override fun onOk() {
                        activity.finish()
                    }

                })
            appDialog.show(activity.supportFragmentManager, null)
        }

        fun initHeader(statusBar: View) {
            val statusBarHeight = statusBarHeight(statusBar.context)
            statusBar.post {
                if (statusBar.height != statusBarHeight) {
                    val params = statusBar.layoutParams as ViewGroup.LayoutParams
                    params.height = statusBarHeight
                    statusBar.requestLayout()
                }
            }
        }

        fun statusBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun navBarHeight(context: Context): Int {
            var result = 0
            val resourceId =
                context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun isLoggedIn(): Boolean {
            return loggedInUser != null
        }

        fun saveToLocal(
            json: String,
            context: Context,
            type: AppDb.DataType,
            keySuffix: String? = null
        ) {
            Thread {
                val db = AppDb(context)
                db.saveData(type, json, keySuffix)
                db.close()
            }.start()
        }

        fun getShapeFromColor(view: View, color: Int, currentCorners: Float = 0f): ViewShape {
            val topShape = ViewShape()
            topShape.view = view

            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.setColor(ContextCompat.getColor(view.context, color))

            topShape.viewBg = gradientDrawable
            topShape.currentCorners = Utils.changeDpToPx(view.context, currentCorners)
            return topShape
        }

        fun logout(activity: MainActivity) {
            Thread {
                val appDb = AppDb(activity)
                appDb.deleteAllData()
                appDb.close()
            }.start()
//            removeLoginProfile(activity)
            loggedInUser = null
            quickInfo = null
            ApiService.createApiService()

            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }


    }

    override fun onCreate() {
        super.onCreate()
        val config = CheckoutConfig(
            application = this,
            clientId = PAYPAL_CLIENT_ID_LIVE,
            environment = Environment.LIVE,
            currencyCode = CurrencyCode.USD,
            userAction = UserAction.PAY_NOW,
            settingsConfig = SettingsConfig(
                loggingEnabled = true
            )
        )
        PayPalCheckout.setConfig(config)

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();
        
    }


}