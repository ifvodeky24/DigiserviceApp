package com.example.feature_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_chat.databinding.FragmentChatListBinding
import com.example.feature_chat.models.ChatMessage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChatListFragment : Fragment() , RecentChatListener{

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding

    private lateinit var conversations: ArrayList<ChatMessage>
    private lateinit var conversationsAdapter: RecentConversationsAdapter
    private lateinit var database: FirebaseFirestore

    private lateinit var preferenceManager: PreferenceManager

    private var senderId = ""

    private val status by lazy { (activity as ChatActivity).status }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (status == "2"){
            findNavController().navigate(R.id.chatFragment)
            onDestroyView()
        } else {
            conversations = ArrayList()
            conversationsAdapter = RecentConversationsAdapter(conversations, this@ChatListFragment)
            binding?.conversationRecylerView?.adapter = conversationsAdapter
            database = FirebaseFirestore.getInstance()
            preferenceManager = PreferenceManager(requireActivity())
            listenConversations()

            senderId = preferenceManager.getString(Constants.KEY_SENDER_ID).toString()
            Timber.d("oom senderId ny $senderId")
        }
    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (error != null) {
                return@EventListener
            }
            if (value != null) {
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val senderId = documentChange.document.getString(Constants.KEY_SENDER_ID)
                        val receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        val chatMessage = ChatMessage()
                        chatMessage.senderId = senderId
                        chatMessage.receiverId = receiverId
                        if (this.senderId.equals(senderId)) {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_RECEIVER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_RECEIVER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        } else {
                            chatMessage.conversionImage =
                                documentChange.document.getString(Constants.KEY_SENDER_IMAGE)
                            chatMessage.conversionName =
                                documentChange.document.getString(Constants.KEY_SENDER_NAME)
                            chatMessage.conversionId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                        }
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessage.dateTime =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                                ?.let { getReadableDateTime(it) }
                        conversations.add(chatMessage)

                    } else if (documentChange.type == DocumentChange.Type.MODIFIED) {
                        for (i in conversations.indices) {
                            val senderId =
                                documentChange.document.getString(Constants.KEY_SENDER_ID)
                            val receiverId =
                                documentChange.document.getString(Constants.KEY_RECEIVER_ID)

                            if (conversations.get(i).senderId.equals(senderId)
                                && conversations.get(i).receiverId.equals(receiverId)
                            ) {
                                conversations.get(i).message =
                                    documentChange.document.getString(Constants.KEY_LAST_MESSAGE)
                                conversations.get(i).dateObject =
                                    documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                            }
                        }
                    }

                    Timber.d("jumlah conversation ${conversations.size}")
                }

                conversations.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                    obj2.dateObject!!.compareTo(
                        obj1.dateObject
                    )
                }

                conversationsAdapter.notifyDataSetChanged()
                binding?.conversationRecylerView?.smoothScrollToPosition(0)

                if (conversations.isEmpty()){
                    binding?.conversationRecylerView?.visibility = View.GONE
                    binding?.ivEmpty?.visibility = View.VISIBLE
                } else {
                    binding?.conversationRecylerView?.visibility = View.VISIBLE
                    binding?.ivEmpty?.visibility = View.GONE
                }
            }
        }

    private fun listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_SENDER_ID))
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_SENDER_ID))
            .addSnapshotListener(eventListener)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun OnClick(chatMessage: ChatMessage) {
        Timber.d("chat receiverId = ${chatMessage.conversionId}")
        preferenceManager.putString(Constants.KEY_RECEIVER_ID, chatMessage.conversionId)
        preferenceManager.putString(Constants.KEY_RECEIVER_NAME, chatMessage.conversionName)
        preferenceManager.putString(Constants.KEY_RECEIVER_PHOTO, chatMessage.conversionImage)
        findNavController().navigate(R.id.chatFragment)
    }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

}