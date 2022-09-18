package com.online.course.ui.widget

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.online.course.R
import com.online.course.databinding.DialogProgressiveLoadingBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.OnDownloadProgressListener
import com.online.course.manager.net.observer.NetworkObserverDialog
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.ProgressiveLoadingDialogPresenterImpl


class ProgressiveLoadingDialog : NetworkObserverDialog(), View.OnClickListener,
    OnDownloadProgressListener {

    private lateinit var mPresenter: Presenter.ProgressiveLoadingPresenter
    private lateinit var mBinding: DialogProgressiveLoadingBinding
    private var mOnFileSaved: ItemCallback<String>? = null


    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            dialog!!.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog!!.window!!.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogProgressiveLoadingBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.dialogProgressiveCancelBtn.setOnClickListener(this)

        val url = requireArguments().getString(App.URL)!!
        val dir = requireArguments().getString(App.DIR)
        val toDownloads = requireArguments().getBoolean(App.TO_DOWNLOADS, false)
        val nameFromHeader = requireArguments().getBoolean(App.FILE_NAME_FROM_HEADER, false)
        mPresenter = ProgressiveLoadingDialogPresenterImpl(this)
        mPresenter.downloadFile(dir, url, this, toDownloads, nameFromHeader)
    }

    override fun onClick(v: View?) {
        dismiss()
    }

    override fun onAttachmentDownloadedError() {
    }

    override fun onAttachmentDownloadUpdate(percent: Float, id: Int?) {
        Handler(Looper.getMainLooper()).post {
            mBinding.dialogProgressiveBar.progress = percent.toInt()
            mBinding.dialogProgressivePercentageTv.text = "$percent%"
        }
    }

    fun setOnFileSavedListener(onFileSaved: ItemCallback<String>) {
        mOnFileSaved = onFileSaved
    }

    fun onFileSaved(filePath: String?) {
        Handler(Looper.getMainLooper()).post {
            filePath?.let { mOnFileSaved?.onItem(it) }
            context?.let { dismiss() }
        }
    }

    fun onFileSaveFailed() {
        Handler(Looper.getMainLooper()).post{
            ToastMaker.show(
                requireContext(),
                getString(R.string.error),
                getString(R.string.failed_to_download),
                ToastMaker.Type.ERROR
            )
            context?.let { dismiss() }

        }
    }
}