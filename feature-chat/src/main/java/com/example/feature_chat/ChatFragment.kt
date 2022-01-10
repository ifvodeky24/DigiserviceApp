package com.example.feature_chat

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_chat.databinding.FragmentChatBinding
import com.example.feature_chat.models.ChatMessage
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding

    private var receiverId = ""
    private var senderId = ""
    private var isReceiverAvailable = false
    private var receiverPhoto = ""
    private var receiverName = ""
    private var conversionId: String? = null

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var chatMessages: ArrayList<ChatMessage>
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var database: FirebaseFirestore

    private val status by lazy { (activity as ChatActivity).status }
    private val productName by lazy { (activity as ChatActivity).productName }
    private val productImage by lazy { (activity as ChatActivity).productImage }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding?.root!!
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
            senderId,
            receiverName
        )
        binding?.chatRecyclerView?.adapter = chatAdapter
        database = FirebaseFirestore.getInstance()

        Timber.d("cek receiverId $receiverId")
        Timber.d("cek senderId $senderId")
        Timber.d("cek receiverPhoto $receiverPhoto")

        binding?.textName?.text = receiverName

        if (status == "3") {
            binding?.cvProduct?.visibility = View.VISIBLE
            binding?.tvProductName?.text = productName
            Glide.with(requireActivity())
                .load(APP_PRODUCT_IMAGES_URL + productImage)
                .fitCenter()
                .into(binding!!.ivProduct)
        } else {
            binding?.cvProduct?.visibility = View.GONE
        }

        setListeners()
        listenMessages()

        if (status == "2" || status == "3") {
            binding?.imageBack?.setOnClickListener {
                navigateToHomeActivity(true)
            }

            requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        navigateToHomeActivity(true)
                    }
                }
            )
        }
    }

    private fun setListeners() {
        binding?.imageBack?.setOnClickListener { view -> findNavController().navigateUp() }
        binding?.layoutSend?.setOnClickListener { view -> sendMessage() }
    }

    private fun sendMessage() {
        val message = HashMap<String, Any?>()
        message[Constants.KEY_SENDER_ID] = senderId
        message[Constants.KEY_RECEIVER_ID] = receiverId
        message[Constants.KEY_MESSAGE] = binding?.inputMessage?.text.toString()
        message[Constants.KEY_TIMESTAMP] = Date()

        if (conversionId != null) {
            updateConversion(binding?.inputMessage?.text.toString())
        } else {
            val conversion = HashMap<String, Any>()
            conversion.put(Constants.KEY_SENDER_ID, senderId)
            conversion.put(
                Constants.KEY_SENDER_NAME,
                preferenceManager.getString(Constants.KEY_NAME).toString()
            )
            conversion.put(
                Constants.KEY_SENDER_IMAGE,
                preferenceManager.getString(Constants.KEY_IMAGE).toString()
            )
            conversion.put(Constants.KEY_RECEIVER_ID, receiverId)
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverName)
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverPhoto)
            conversion.put(Constants.KEY_LAST_MESSAGE, binding?.inputMessage?.text.toString())
            conversion.put(Constants.KEY_TIMESTAMP, Date())
            conversion.put(Constants.KEY_TIMESTAMP, Date())
            addConversion(conversion)
        }
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message)
        binding?.inputMessage?.text = null
    }

    private fun listenAvailabilityOfReceiver() {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                    receiverId
                ).addSnapshotListener(requireActivity()) { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (value != null) {
                        if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                            val availability = value.getLong(Constants.KEY_AVAILABILITY)!!.toInt()
                            isReceiverAvailable = availability == 1
                        }
                    }

                    if (isReceiverAvailable) {
                        binding?.textAvailable?.visibility = View.VISIBLE
                    } else {
                        binding?.textAvailable?.visibility = View.GONE
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, 2000)
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
                    binding?.chatRecyclerView?.smoothScrollToPosition(chatMessages.size - 1)
                }
                binding?.chatRecyclerView?.visibility = View.VISIBLE
            }
            binding?.progressBar?.visibility = View.GONE

            if (conversionId == null) {
                checkForConversion()
            }
        }

    private fun getReadableDateTime(date: Date): String {
        return SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date)
    }

    private fun addConversion(conversion: HashMap<String, Any>) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .add(conversion)
            .addOnSuccessListener { documentReference: DocumentReference ->
                conversionId = documentReference.id
            }
    }

    private fun updateConversion(message: String) {
        val documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .document(conversionId.toString())
        documentReference.update(
            Constants.KEY_LAST_MESSAGE, message,
            Constants.KEY_TIMESTAMP, Date()
        )
    }

    private fun checkForConversion() {
        if (chatMessages.size != 0) {
            checkForConversionRemotely(
                senderId,
                receiverId
            )
            checkForConversionRemotely(
                receiverId,
                senderId
            )
        }
    }

    private fun checkForConversionRemotely(senderId: String, receiverId: String) {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
            .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
            .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
            .get()
            .addOnCompleteListener(conversionOnCompleteListener)
    }

    private val conversionOnCompleteListener =
        OnCompleteListener { task: Task<QuerySnapshot?> ->
            if (task.isSuccessful && task.result != null && task.result!!
                    .documents.size > 0
            ) {
                val documentSnapshot = task.result!!.documents[0]

                conversionId = documentSnapshot.id
                Timber.d("buuusdsdsdsd $conversionId")
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        try {
            listenAvailabilityOfReceiver()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}