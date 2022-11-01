package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.coursestore.R
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.QuizResultRvAdapter
import com.online.coursestore.model.QuizResult
import com.online.coursestore.model.ToolbarOptions
import com.online.coursestore.ui.MainActivity

class CertStudentsFrag : Fragment() {

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
        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, R.string.certificate_students)

        mBinding.rvProgressBar.visibility = View.GONE
        val results = requireArguments().getParcelableArrayList<QuizResult>(App.RESULT)!!
        val adapter = QuizResultRvAdapter(results, false)
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = adapter
    }
}