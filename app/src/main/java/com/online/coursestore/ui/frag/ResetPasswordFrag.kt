package com.online.coursestore.ui.frag

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

import com.online.coursestore.R
import com.online.coursestore.databinding.FragSignInBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.net.ApiService
import com.online.coursestore.model.Data
import com.online.coursestore.model.Login
import com.online.coursestore.model.Response
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.UserAuthFrag
import com.online.coursestore.ui.widget.LoadingDialog


class ResetPasswordFrag : UserAuthFrag() {

    private lateinit var mPresenter: Presenter.SignInPresenter
    private lateinit var mBinding: FragSignInBinding

    private val mInputTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            enableDisableLoginBtn()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 1421
        private const val TAG = "SignInFrag"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragSignInBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init() {
        mGoogleBtn = mBinding.signInGoogleLayout
        mFacebookBtn = mBinding.signInFacebookLayout

        mBinding.signInLoginBtn.setOnClickListener(this)
        mBinding.signInSignUpBtn.setOnClickListener(this)
        mBinding.signInForgotPasswordBtn.setOnClickListener(this)
        mBinding.signInEmailPhoneEdtx.addTextChangedListener(mInputTextWatcher)
        mBinding.signInPasswordEdtx.addTextChangedListener(mInputTextWatcher)
//        mPresenter = SignInPresenterImpl(this)
    }

    override fun onClick(v: View?) {
        super.onClick(v)

        when (v?.id) {
            R.id.signInLoginBtn -> {
                val username = mBinding.signInEmailPhoneEdtx.text.toString()
                val password = mBinding.signInPasswordEdtx.text.toString()


                val loginObj = Login()
                loginObj.username = username
                loginObj.password = password

                mPresenter.login(loginObj)
            }
        }
    }

//    fun onCodeSent(response: Response, email: String?, mobile: String?) {
//        if (response.isSuccessful) {
//            val frag = VerifyCodeFrag.getInstance()
//            val bundle = Bundle()
//            bundle.putString(App.TOKEN, response.token)
//            bundle.putInt(App.CODE_VAL_TIME, response.codeValidationTime)
//            if (email != null)
//                bundle.putString(App.EMAIL, email)
//
//            if (mobile != null)
//                bundle.putString(App.MOBILE, mobile)
//
//            frag.arguments = bundle
//            parentFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
//
//        } else {
//            Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
//        }
//    }

    override fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {

            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
//            val customer = Customer()
//            customer.token = account.idToken


            mLoadingDialog = LoadingDialog.instance
            mLoadingDialog?.show(childFragmentManager, null)
//            mPresenter.send3dPartyData(customer)

        } catch (e: ApiException) {
//            Toast.makeText(requireContext(), getString(R.string.sign_in_failed), Toast.LENGTH_SHORT)
//                .show()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

//            if (BuildVars.LOGS_ENABLED)
//                Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    override fun getFacebookSignInCallback(): FacebookCallback<LoginResult> {
        return object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                if (result != null) {
//                    val customer = Customer()
//                    customer.token = result.accessToken.token
//                    customer.via = App.RegistrationProvider.FACEBOOK.value()
//                    customer.appOs = App.APP_OS
//                    mPresenter.send3dPartyData(customer)
                }
            }

            override fun onCancel() {
//                Toast.makeText(
//                    context,
//                    getString(R.string.sign_in_canceled),
//                    Toast.LENGTH_SHORT
//                ).show()
            }

            override fun onError(error: FacebookException?) {
//                Toast.makeText(
//                    context,
//                    getString(R.string.error_occured) + error?.message,
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }
    }

    fun onSuccessfulLogin(response : Data<Response>) {
        if (response.isSuccessful) {
//            if (customer.via == App.RegistrationProvider.GOOGLE.value()) {
//                mGoogleSignInClient.signOut()
//            } else if (customer.via == App.RegistrationProvider.FACEBOOK.value()) {
//                LoginManager.getInstance().logOut()
//            }

//            App.setCustomer(customer)
//            context?.let { AppKotlin.saveTokenAndEmaileOrMobile(it, customer) }

            ApiService.createAuthorizedApiService(requireContext(), response.data!!.token)

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.putExtra(App.SHOULD_REGISTER, !response.data!!.profileState.isNullOrEmpty())
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            activity?.finish()
        } else {
            onErrorOccured(response)
        }
    }


    private fun enableDisableLoginBtn() {
        val username = mBinding.signInEmailPhoneEdtx.text.toString()
        val password = mBinding.signInPasswordEdtx.text.toString()
        val loginBtn = mBinding.signInLoginBtn

        if (username.isNotEmpty() && password.isNotEmpty()) {
            var checkForEmail = false

            for ((index, char) in username.withIndex()) {
                if (index == 0 && char == '+')
                    continue

                if (!char.isDigit()) {
                    checkForEmail = true
                    break
                }
            }

            if (checkForEmail) {
                val isValidEmail = username.length >= 3 && username.contains("@")
                        && username.contains(".")
                if (isValidEmail)
                    loginBtn.isEnabled = true
            } else {
                loginBtn.isEnabled = true
            }

        } else if (loginBtn.isEnabled) {
            loginBtn.isEnabled = false
        }
    }
}