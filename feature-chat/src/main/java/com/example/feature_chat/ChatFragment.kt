package com.example.feature_chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_chat.databinding.FragmentChatBinding
import com.example.feature_chat.models.ChatMessage
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private var receiverId = ""
    private var senderId = ""
    private var receiverPhoto = ""
    private var receiverName = ""

    private lateinit var preferenceManager : PreferenceManager
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var database: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager = PreferenceManager(requireActivity())

        receiverId = preferenceManager.getString(Constants.KEY_RECEIVER_ID).toString()
        senderId = preferenceManager.getString(Constants.KEY_SENDER_ID).toString()
        receiverPhoto = preferenceManager.getString(Constants.KEY_RECEIVER_PHOTO).toString()
        receiverName = preferenceManager.getString(Constants.KEY_RECEIVER_NAME).toString()

        chatMessages = ArrayList()
        chatAdapter = ChatAdapter(
            receiverPhoto,
            chatMessages,
            senderId
        )
        binding.chatRecyclerView.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()

        Timber.d("cek receiverId $receiverId")
        Timber.d("cek senderId $senderId")
        Timber.d("cek receiverPhoto $receiverPhoto")

        binding.textName.text = receiverName

        setListeners()
        listenMessages()
    }

    private fun setListeners() {
        binding.imageBack.setOnClickListener { view -> findNavController().navigateUp() }
        binding.layoutSend.setOnClickListener { view -> sendMessage() }
    }

    private fun sendMessage() {
        val message = HashMap<String, Any?>()
        message[Constants.KEY_SENDER_ID] = senderId
        message[Constants.KEY_RECEIVER_ID] = receiverId
        message[Constants.KEY_MESSAGE] = binding.inputMessage.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding.inputMessage.text = null
    }

    private fun listenMessages() {
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(
                Constants.KEY_SENDER_ID,
                senderId
            )
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .addSnapshotListener(eventListener)
        database.collection(Constants.KEY_COLLECTION_CHAT)
            .whereEqualTo(Constants.KEY_SENDER_ID, receiverId)
            .whereEqualTo(
                Constants.KEY_RECEIVER_ID,
                senderId
            )
            .addSnapshotListener(eventListener)
    }

    private val eventListener =
        EventListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
            if (value != null) {
                val count = chatMessages.size
                for (documentChange in value.documentChanges) {
                    if (documentChange.type == DocumentChange.Type.ADDED) {
                        val chatMessage = ChatMessage()
                        chatMessage.senderId =
                            documentChange.document.getString(Constants.KEY_SENDER_ID)
                        chatMessage.receiverId =
                            documentChange.document.getString(Constants.KEY_RECEIVER_ID)
                        chatMessage.message =
                            documentChange.document.getString(Constants.KEY_MESSAGE)
                        chatMessage.dateTime = getReadableDateTime(
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)!!
                        )
                        chatMessage.dateObject =
                            documentChange.document.getDate(Constants.KEY_TIMESTAMP)
                        chatMessages.add(chatMessage)
                    }
                }
                chatMessages.sortWith { obj1: ChatMessage, obj2: ChatMessage ->
                    obj1.dateObject!!.compareTo(
                        obj2.dateObject
                    )
                }
                if (count == 0) {
                    chatAdapter.notifyDataSetChanged()
                } else {
                    chatAdapter.notifyItemRangeInserted(chatMessages.size, chatMessages.size)
                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
                }
                binding.chatRecyclerView.visibility = View.VISIBLE
            }
            binding.progressBar.visibility = View.GONE
        }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}