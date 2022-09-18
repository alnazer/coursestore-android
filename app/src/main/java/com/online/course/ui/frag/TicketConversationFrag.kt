package com.online.course.ui.frag

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.online.course.R
import com.online.course.databinding.FragTicketConversationBinding
import com.online.course.manager.App
import com.online.course.manager.ToastMaker
import com.online.course.manager.Utils
import com.online.course.manager.listener.ItemCallback
import com.online.course.manager.net.observer.NetworkObserverFragment
import com.online.course.model.BaseResponse
import com.online.course.model.Ticket
import com.online.course.model.TicketConversation
import com.online.course.model.ToolbarOptions
import com.online.course.presenter.Presenter
import com.online.course.presenterImpl.TicketConversationPresenterImpl
import com.online.course.ui.MainActivity
import com.online.course.ui.widget.AppDialog
import com.online.course.ui.widget.LoadingDialog
import com.online.course.ui.widget.NewTicketDialog
import com.online.course.ui.widget.ProgressiveLoadingDialog
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File


class TicketConversationFrag : NetworkObserverFragment(), View.OnClickListener {

    private lateinit var mPresenter: Presenter.TicketConversationPresenter
    private lateinit var mTicket: Ticket
    private lateinit var mLoadingDialog: LoadingDialog
    private lateinit var mConversations: MutableList<TicketConversation>
    private lateinit var mBinding: FragTicketConversationBinding
    private var mPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragTicketConversationBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bgGray))

        mPresenter = TicketConversationPresenterImpl(this)
        mTicket = requireArguments().getParcelable(App.TICKET)!!
        mPosition = requireArguments().getInt(App.POSITION)
        addConversations(mTicket.conversations.toMutableList())

        if (mTicket.course != null) {
            mBinding.ticketConversationHeaderInfoDescTv.text = mTicket.course!!.title
            mBinding.ticketConversationHeaderContainer.visibility = View.VISIBLE
            mBinding.ticketConversationHeaderContainer.setOnClickListener(this)
        }

        val toolbarOptions = ToolbarOptions()
        toolbarOptions.startIcon = ToolbarOptions.Icon.BACK

        (activity as MainActivity).showToolbar(toolbarOptions, mTicket.title)

        if (mTicket.status == Ticket.Status.CLOSE.value()) {
            mBinding.ticketConversationCancelBtn.visibility = View.GONE
        }

        mBinding.ticketConversationReplyBtn.setOnClickListener(this)
        mBinding.ticketConversationCancelBtn.setOnClickListener(this)
    }

    fun addConversations(conversations: MutableList<TicketConversation>) {
        mConversations = conversations
        val adapter = GroupieAdapter()

        for ((i, conversation) in conversations.withIndex()) {
            if (conversation.supporter != null || conversation.sender!!.id != App.loggedInUser!!.id) {
                if (conversation.supporter == null) {
                    conversation.supporter = conversation.sender
                }

                adapter.add(SystemUserItem(conversation))
                if (conversation.attachment != null) {
                    val attachmentItem = AttachmentItem(i, true)
                    adapter.add(attachmentItem)
                }

            } else {
                adapter.add(UserItem(conversation))
                if (conversation.attachment != null)
                    adapter.add(AttachmentItem(i, false))
            }
        }

        mBinding.ticketConversationRv.adapter = adapter
    }

    inner class ViewHolder(rootView: View) : GroupieViewHolder(rootView)

    inner class SystemUserItem(private val ticketConversation: TicketConversation) :
        Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.item_conversation_support_user
        }


        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val userChatDateTimeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.userSupportChatDateTimeTv)
            val userChatTv = viewHolder.itemView.findViewById<TextView>(R.id.userSupportChatTv)
            val userChatImg =
                viewHolder.itemView.findViewById<CircleImageView>(R.id.userSupportChatImg)

            if (ticketConversation.supporter!!.avatar != null) {
                Glide.with(viewHolder.itemView.context)
                    .load(ticketConversation.supporter!!.avatar)
                    .into(userChatImg)
            }

            userChatTv.text = ticketConversation.message
            userChatDateTimeTv.text = Utils.getDateTimeFromTimestamp(ticketConversation.createdAt)
        }
    }

    inner class UserItem(private val ticketConversation: TicketConversation) :
        Item<GroupieViewHolder>() {

        override fun getLayout(): Int {
            return R.layout.item_conversation_user
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val userChatDateTimeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.userChatDateTimeTv)
            val userChatTv = viewHolder.itemView.findViewById<TextView>(R.id.userChatTv)

            userChatTv.text = ticketConversation.message
            userChatDateTimeTv.text = Utils.getDateTimeFromTimestamp(ticketConversation.createdAt)
        }
    }

    inner class AttachmentItem(
        private val index: Int,
        private val systemUserAttachment: Boolean
    ) : Item<GroupieViewHolder>(), View.OnClickListener {

        override fun getLayout(): Int {
            return R.layout.item_conversation_attachment
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val attachmentFileSizeProgressBar =
                viewHolder.itemView.findViewById<ProgressBar>(R.id.attachmentFileSizeProgressBar)

            val attachmentFileSizeTv =
                viewHolder.itemView.findViewById<TextView>(R.id.attachmentFileSizeTv)

            val ticketConversation = mConversations[index]
            if (ticketConversation.fileSize == null) {
                mPresenter.getFileSize(
                    ticketConversation.attachment!!,
                    object : ItemCallback<Long> {
                        override fun onItem(size: Long, vararg args: Any) {
                            Handler(Looper.getMainLooper()).post {
                                attachmentFileSizeProgressBar.visibility = View.GONE
                                ticketConversation.fileSize = size
                                mConversations[index] = ticketConversation
                                attachmentFileSizeTv.text =
                                    Utils.humanReadableByteCountSI(
                                        ticketConversation.fileSize!!
                                    )
                            }
                        }
                    })
            } else {
                attachmentFileSizeTv.text = Utils.humanReadableByteCountSI(
                    ticketConversation.fileSize!!
                )
            }
        }

        override fun createViewHolder(itemView: View): GroupieViewHolder {
            val attachmentContainer = itemView.findViewById<View>(R.id.attachmentContainer)

            if (systemUserAttachment) {
                val layoutParams = attachmentContainer.layoutParams as FrameLayout.LayoutParams
                layoutParams.gravity = Gravity.LEFT
                itemView.requestLayout()
            }
            attachmentContainer.setOnClickListener(this)
            return super.createViewHolder(itemView)
        }

        override fun onClick(v: View?) {
            val fileUrl = mConversations[index].attachment

            val path =
                App.Companion.Directory.TICKETS_ATTACHMENT.value() + File.separator + Utils.extractFileNameFromUrl(
                    fileUrl!!
                )
            val attachment = File(requireContext().filesDir, path)
            if (attachment.exists()) {
                viewFile(attachment)
            } else {
                val bundle = Bundle()
                bundle.putString(App.URL, fileUrl)
                bundle.putString(App.DIR, App.Companion.Directory.TICKETS_ATTACHMENT.value())

                val loadingDialog = ProgressiveLoadingDialog()
                loadingDialog.setOnFileSavedListener(object : ItemCallback<String> {
                    override fun onItem(filePath: String, vararg args: Any) {
                        viewFile(File(filePath))
                    }
                })
                loadingDialog.arguments = bundle
                loadingDialog.show(childFragmentManager, null)
            }
        }
    }

    private fun viewFile(attachment: File) {
        try {
            val fileUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".provider",
                attachment
            )

            val mime = requireContext().contentResolver.getType(fileUri)

            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                getString(R.string.cannot_open_this_file),
                Toast.LENGTH_SHORT
            ).show()
        }
//        catch (e: FileUriExposedException) {
//        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ticketConversationCancelBtn -> {
                val dialog = AppDialog()
                val bundle = Bundle()
                bundle.putString(App.TITLE, getString(R.string.close))
                bundle.putString(App.TEXT, getString(R.string.close_ticket_desc))
                dialog.arguments = bundle
                dialog.setOnDialogBtnsClickedListener(AppDialog.DialogType.YES_CANCEL,
                    object : AppDialog.OnDialogCreated {

                        override fun onCancel() {
                        }

                        override fun onOk() {
                            mLoadingDialog = LoadingDialog.instance
                            mLoadingDialog.show(childFragmentManager, null)

                            mPresenter.closeTicket(mTicket.id)
                        }

                    })
                dialog.show(childFragmentManager, null)
            }

            R.id.ticketConversationReplyBtn -> {
                val bundle = Bundle()
                bundle.putInt(App.ID, mTicket.id)
                bundle.putSerializable(App.SELECTION_TYPE, NewTicketDialog.Type.PLATFORM_SUPPORT)

                val dialog = NewTicketDialog()
                dialog.arguments = bundle
                dialog.setOnTicketChatSavedListener(object : ItemCallback<TicketConversation> {
                    override fun onItem(conversation: TicketConversation, vararg args: Any) {
                        conversation.createdAt = System.currentTimeMillis() / 1000

                        if (!mBinding.ticketConversationCancelBtn.isVisible)
                            mBinding.ticketConversationCancelBtn.visibility = View.VISIBLE

                        val adapter = mBinding.ticketConversationRv.adapter as GroupieAdapter
                        adapter.add(UserItem(conversation))
                        if (conversation.attachment != null)
                            adapter.add(AttachmentItem(mConversations.size, false))
                        mConversations.add(conversation)
                    }
                })
                dialog.show(childFragmentManager, null)
            }

            R.id.ticketConversationHeaderContainer -> {
                val bundle = Bundle()
                bundle.putParcelable(App.COURSE, mTicket.course)

                val frag = CourseDetailsFrag()
                frag.arguments = bundle
                (activity as MainActivity).transact(frag)
            }
        }
    }

    fun onTicketClosed(response: BaseResponse) {
        if (context == null) return

        mLoadingDialog.dismiss()

        val title: String
        val type: ToastMaker.Type

        if (response.isSuccessful) {
            title = getString(R.string.success)
            type = ToastMaker.Type.SUCCESS
        } else {
            title = getString(R.string.error)
            type = ToastMaker.Type.ERROR
        }

        ToastMaker.show(requireContext(), title, response.message, type)

        if (response.isSuccessful) {
            parentFragment
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    fun onRequestFailed() {
        mLoadingDialog.dismiss()
    }
}