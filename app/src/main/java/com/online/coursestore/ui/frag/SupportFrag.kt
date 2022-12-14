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
import com.online.coursestore.manager.adapter.CommonRvAdapter
import com.online.coursestore.manager.listener.ItemCallback
import com.online.coursestore.manager.listener.ItemClickListener
import com.online.coursestore.manager.listener.OnItemClickListener
import com.online.coursestore.manager.net.observer.NetworkObserverFragment
import com.online.coursestore.model.Data
import com.online.coursestore.model.Ticket
import com.online.coursestore.model.TicketConversation
import com.online.coursestore.presenterImpl.SupportPresenterImpl
import com.online.coursestore.ui.MainActivity
import com.online.coursestore.ui.frag.abstract.EmptyState
import com.online.coursestore.ui.widget.NewTicketDialog
import java.io.Serializable

class SupportFrag : NetworkObserverFragment(), OnItemClickListener, View.OnClickListener,
    EmptyState {

    private lateinit var mBinding: RvBinding
    private lateinit var mType: Type

    enum class Type : Serializable {
        TICKETS,
        CLASS_SUPPORT,
        MY_CLASS_SUPPORT
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = RvBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        mBinding.rvEmptyState.root.visibility = View.INVISIBLE
        mBinding.rvContainer.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.transparent
            )
        )
        mType = requireArguments().getSerializable(App.SELECTION_TYPE) as Type

        val presenter = SupportPresenterImpl(this)

        when (mType) {
            Type.TICKETS -> {
                mBinding.rvAddBtn.visibility = View.VISIBLE
                presenter.getTickets()
            }
            Type.CLASS_SUPPORT -> {
                mBinding.rvAddBtn.visibility = View.VISIBLE
                presenter.getClassSupport()
            }
            Type.MY_CLASS_SUPPORT -> {
                presenter.getMyClassSupport()
            }
        }

        mBinding.rvAddBtn.setOnClickListener(this)
    }

    override fun onClick(view: View?, position: Int, id: Int) {
        val item = (mBinding.rv.adapter as CommonRvAdapter).items[position] as Ticket

        val bundle = Bundle()
        bundle.putParcelable(App.TICKET, item)

        val frag = TicketConversationFrag()
        frag.arguments = bundle
        (activity as MainActivity).transact(frag)
    }


    override fun onLongClick(view: View?, position: Int, id: Int) {
    }

    override fun onClick(v: View?) {
        val bundle = Bundle()

        when (mType) {
            Type.TICKETS -> {
                bundle.putSerializable(App.SELECTION_TYPE, NewTicketDialog.Type.PLATFORM_SUPPORT)
            }
            Type.CLASS_SUPPORT -> {
                bundle.putSerializable(App.SELECTION_TYPE, NewTicketDialog.Type.COURSE_SUPPORT)
            }
        }

        val newTicketBinding = NewTicketDialog()
        newTicketBinding.arguments = bundle
        newTicketBinding.setOnTicketAdded(object : ItemCallback<Ticket> {
            override fun onItem(ticket: Ticket, vararg args: Any) {
                hideEmptyState()

                val ticketConverstaion = TicketConversation()
                ticketConverstaion.message = ticket.message!!
                ticketConverstaion.createdAt = System.currentTimeMillis() / 1000
                ticketConverstaion.sender = App.loggedInUser!!

                val conversations = ticket.conversations.toMutableList()
                conversations.add(ticketConverstaion)

                ticket.conversations = conversations

                val adapter = mBinding.rv.adapter as CommonRvAdapter
                adapter.items.add(0, ticket)
                adapter.notifyItemInserted(0)

                mBinding.rv.smoothScrollToPosition(0)
            }
        })
        newTicketBinding.show(childFragmentManager, null)
    }

    fun showEmptyState() {
        when (mType) {
            Type.TICKETS -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_tickets,
                    R.string.no_tickets_desc
                )
            }
            Type.CLASS_SUPPORT -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_courses,
                    R.string.purchase_no_courses
                )
            }
            Type.MY_CLASS_SUPPORT -> {
                showEmptyState(
                    R.drawable.no_comments,
                    R.string.no_courses,
                    R.string.no_courses_class
                )
            }
        }
    }

    override fun emptyViewBinding(): EmptyStateBinding {
        return mBinding.rvEmptyState
    }

    override fun getVisibleFrag(): Fragment {
        return this
    }

    fun onSupportsReceived(data: Data<List<Ticket>>) {
        mBinding.rvProgressBar.visibility = View.INVISIBLE
        mBinding.rv.adapter = CommonRvAdapter(data.data!!)
        mBinding.rv.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rv.addOnItemTouchListener(ItemClickListener(mBinding.rv, this))

        if (data.data!!.isEmpty()) {
            showEmptyState()
        }
    }
}