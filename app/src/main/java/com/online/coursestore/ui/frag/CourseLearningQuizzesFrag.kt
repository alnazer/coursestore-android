package com.online.coursestore.ui.frag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.online.coursestore.databinding.RvBinding
import com.online.coursestore.manager.App
import com.online.coursestore.manager.adapter.CourseCommon2RvAdapter
import com.online.coursestore.manager.listener.ItemClickListener
import com.online.coursestore.manager.listener.OnItemClickListener
import com.online.coursestore.model.Course
import com.online.coursestore.model.Quiz
import com.online.coursestore.ui.MainActivity

class CourseLearningQuizzesFrag : Fragment(), OnItemClickListener {
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
        val course = requireArguments().getParcelable<Course>(App.COURSE)!!
        val offlineMode = requireArguments().getBoolean(App.OFFLINE)
        mBinding.rvProgressBar.visibility = View.GONE
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.adapter = CourseCommon2RvAdapter(course.quizzes)
        if (!offlineMode) {
            mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))
        }
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val quiz = (mBinding.rv.adapter as CourseCommon2RvAdapter).items[position] as Quiz

        val frag: Fragment
        val bundle = Bundle()

        frag = if (quiz.authStatus == Quiz.NOT_PARTICIPATED) {
            bundle.putParcelable(App.QUIZ, quiz)
            QuizOverviewFrag()
        } else {
            bundle.putInt(App.ID, quiz.id)
            QuizResultInfoFrag()
        }

        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }

    override fun onLongClick(view: View?, position: Int, id: Int) {
    }
}