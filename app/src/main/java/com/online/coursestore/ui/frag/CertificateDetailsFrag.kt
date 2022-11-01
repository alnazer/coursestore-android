package com.online.coursestore.ui.frag

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.online.coursestore.R
import com.online.coursestore.databinding.FragCertificateDetailsBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.BuildVars
import com.online.coursestore.manager.ToastMaker
import com.online.coursestore.manager.Utils
import com.online.coursestore.manager.adapter.CertStudentRvAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Quiz
import com.online.coursestore.model.QuizResult
import com.online.coursestore.model.ToolbarOptions
import com.online.coursestore.presenter.Presenter
import com.online.coursestore.presenterImpl.CertificateDetailsPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.widget.ProgressiveLoadingDialog
import java.io.File
import java.lang.Exception

class CertificateDetailsFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mBinding: FragCertificateDetailsBinding
    private var mResult: QuizResult? = null
    private lateinit var mQuiz: Quiz
    private var mSavedCertPath: String? = null
    private lateinit var mCertStudents: ArrayList<QuizResult>
    private lateinit var mStoragePermissionResultLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var mPresenter: Presenter.CertificateDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mStoragePermissionResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                    permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                ) {
                    onClick(mBinding.certificateDetailsDownloadBtn)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragCertificateDetailsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.certificate_overview)

        when (val parcelable: Any = requireArguments().getParcelable(App.CERTIFICATE)!!) {
            is QuizResult -> {
                mResult = parcelable
                mQuiz = mResult!!.quiz

                mPresenter = CertificateDetailsPresenterImpl(this)

                if (mResult!!.certificate == null) {
                    mBinding.certificateDetailsImg.setImageResource(R.drawable.cert_default)
                } else {
                    Glide.with(requireContext()).load(mResult!!.certificate!!.img)
                        .into(mBinding.certificateDetailsImg)
                }

                initResultCertMarks()

                if (!mQuiz.authCanDownloadCertificate) {
                    mBinding.certificateDetailsBtnsContainer.visibility = View.GONE
                }

                if (!App.loggedInUser!!.isUser()) {
                    mPresenter.getStudents()
                }
            }
            is Quiz -> {
                mQuiz = parcelable
                mBinding.certificateDetailsBtnsContainer.visibility = View.GONE

                initQuizCertMarks()

                if (mQuiz.course.img != null) {
                    Glide.with(requireContext()).load(mQuiz.course.img)
                        .addListener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                mBinding.certificateDetailsImgOverlay.visibility = View.VISIBLE
                                return false
                            }

                        }).into(mBinding.certificateDetailsImg)
                }

                mBinding.certificateDetailsShareTv.text = getString(R.string.certificate_overview)
                mBinding.certificateDetailsShareDescTv.text =
                    getString(R.string.certificate_overview_desc)
            }
            else -> {
                return
            }
        }


        mBinding.certificateDetailsTitleTv.text = mQuiz.title
        mBinding.certificateDetailsDescTv.text = mQuiz.course.title

//        mBinding.certificateDetailsGradeTv.text = mResult.userGrade.toString()
//        mBinding.certificateDetailsPassGradeTv.text = mResult.quiz.passMark.toString()
//        mBinding.certificateDetailsTakenDateTv.text =
//            Utils.getDateFromTimestamp(mResult.createdAt)
//        mBinding.certificateDetailsCertIdTv.text = ("#" + mResult.id)

        mBinding.certificateDetailsDownloadBtn.setOnClickListener(this)
        mBinding.certificateDetailsShareBtn.setOnClickListener(this)
    }

    private fun initQuizCertMarks() {
        mBinding.certificateDetailsGradeKeyTv.text = getString(R.string.grade)
        mBinding.certificateDetailsGradeTv.text = mQuiz.passMark.toString()

        mBinding.certificateDetailsPassGradeImg.setImageResource(R.drawable.ic_chart)
        mBinding.certificateDetailsPassGradeKeyTv.text = getString(R.string.average)
        mBinding.certificateDetailsPassGradeTv.text = mQuiz.averageGrade.toString()

        mBinding.certificateDetailsTakenDateImg.setImageResource(R.drawable.ic_user)
        mBinding.certificateDetailsTakenDateKeyTv.text = getString(R.string.total_students)
        mBinding.certificateDetailsTakenDateTv.text = mQuiz.participatedCount.toString()

        mBinding.certificateDetailsCertIdImg.setImageResource(R.drawable.ic_calendar)
        mBinding.certificateDetailsCertIdKeyTv.text = getString(R.string.date_created)
        mBinding.certificateDetailsCertIdTv.text =
            Utils.getDateFromTimestamp(mQuiz.course.createdAt)
    }

    private fun initResultCertMarks() {
        mBinding.certificateDetailsGradeKeyTv.text = getString(R.string.your_grade)
        mBinding.certificateDetailsGradeTv.text = mResult!!.userGrade.toString()

        mBinding.certificateDetailsPassGradeImg.setImageResource(R.drawable.ic_done)
        mBinding.certificateDetailsPassGradeKeyTv.text = getString(R.string.pass_grade)
        mBinding.certificateDetailsPassGradeTv.text = mQuiz.passMark.toString()

        mBinding.certificateDetailsTakenDateImg.setImageResource(R.drawable.ic_calendar)
        mBinding.certificateDetailsTakenDateKeyTv.text = getString(R.string.taken_date)
        mBinding.certificateDetailsTakenDateTv.text = Utils.getDateFromTimestamp(mQuiz.createdAt)

        mBinding.certificateDetailsCertIdImg.setImageResource(R.drawable.ic_more_circle)
        mBinding.certificateDetailsCertIdKeyTv.text = getString(R.string.cert_id)

        if (mResult!!.certificate != null) {
            mBinding.certificateDetailsCertIdTv.text = mResult!!.certificate!!.id.toString()
        } else {
            mBinding.certificateDetailsCertIdTv.text = "-"
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.certificateDetailsDownloadBtn -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    mStoragePermissionResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )
                } else {
                    downloadCert(true)
                }
            }

            R.id.certificateDetailsShareBtn -> {
                if (mSavedCertPath != null) {
                    try {
                        Utils.shareFile(
                            requireContext(),
                            getString(R.string.share_certificate_with),
                            FileProvider.getUriForFile(
                                requireContext(),
                                requireContext().packageName + ".provider",
                                File(mSavedCertPath!!)
                            )
                        )
                    } catch (ex: Exception) {
                    }
                } else {
                    downloadCert(false)
                }
            }

            R.id.certificateDetailsLatestStdViewAllBtn -> {
                val bundle = Bundle()
                bundle.putParcelableArrayList(App.RESULT, mCertStudents)

                val frag = CertStudentsFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }
        }
    }

    private fun downloadCert(toDownloads: Boolean) {
        val bundle = Bundle()
        if (mResult!!.certificate == null) {
            bundle.putString(
                App.URL,
                BuildVars.CERT_DOWNLOAD_URL.replace("{}", mResult!!.id.toString())
            )
            bundle.putBoolean(App.FILE_NAME_FROM_HEADER, true)
        } else {
            bundle.putString(App.URL, mResult!!.certificate!!.img)
        }

        bundle.putString(App.DIR, App.Companion.Directory.CERTIFICATE.value())
        bundle.putBoolean(App.TO_DOWNLOADS, toDownloads)

        val loadingDialog = ProgressiveLoadingDialog()
        loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
            override fun onItem(filePath: String, vararg args: Any) {
                mSavedCertPath = filePath

                if (mResult!!.certificate == null) {
                    onFileSaved(mSavedCertPath!!)
                }

                if (toDownloads) {
                    ToastMaker.show(
                        requireContext(),
                        getString(R.string.success),
                        getString(R.string.file_saved_in_your_downloads),
                        ToastMaker.Type.SUCCESS
                    )
                } else {
                    onClick(mBinding.certificateDetailsShareBtn)
                }
            }
        })
        loadingDialog.arguments = bundle
        loadingDialog.show(childFragmentManager, null)
    }

    fun onStudentsReceived(data: List<QuizResult>) {
        if (data.isNotEmpty()) {
            mBinding.certificateDetailsLatestStdRv.adapter = CertStudentRvAdapter(data)
            mBinding.certificateDetailsLatestStdTv.visibility = View.VISIBLE
            mBinding.certificateDetailsLatestStdRv.visibility = View.VISIBLE
            if (data.size > 5) {
                mCertStudents = data as ArrayList<QuizResult>
                mBinding.certificateDetailsLatestStdViewAllBtn.setOnClickListener(this)
                mBinding.certificateDetailsLatestStdViewAllBtn.visibility = View.VISIBLE
            }
        }
    }

    fun onFileSaved(filePath: String) {
        Glide.with(requireContext()).load(File(filePath))
            .into(mBinding.certificateDetailsImg)
    }
}