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
import com.online.coursestore.manager.adapter.CertificateRvAdapter
import com.online.coursestore.manager.listener.ItemClickListener
import com.online.coursestore.manager.listener.OnItemClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Quiz
import com.online.coursestore.model.QuizResult
import com.online.coursestore.presenterImpl.CertificatesPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState

class CertificatesFrag : NetworkObserverFragment(), OnItemClickListener, EmptyState {

    private lateinit var mBinding: RvBinding

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
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        val presenter = CertificatesPresenterImpl(this)

        if (arguments != null && requireArguments().getBoolean(App.INSTRUCTOR_TYPE)) {
            presenter.getClassCertificates()
        } else {
            presenter.getAchievementCertificates()
        }
    }

    fun onCertsReceived(data: List<QuizResult>) {
        if (data.isNotEmpty()) {
            setAdapter(CertificateRvAdapter(data))
        } else {
            showEmptyState()
        }
    }

    fun onClassCertsReceived(data: List<Quiz>) {
        if (data.isNotEmpty()) {
            setAdapter(CertificateRvAdapter(data))
        } else {
            showEmptyState()
        }
    }

    private fun setAdapter(adapter: CertificateRvAdapter<*>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = adapter
        mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val certificate = (mBinding.rv.adapter as CertificateRvAdapter<*>).items[position]

        val bundle = Bundle()
        bundle.putParcelable(App.CERTIFICATE, certificate)

        val frag = CertificateDetailsFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    fun showEmptyState() {
        showEmptyState(R.drawable.no_certificate, R.string.no_certificates, R.string.no_certificates_desc)
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }
}