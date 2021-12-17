package com.example.core_util

object Constants {

    object View {
        val Style= arrayOf("Regular", "Medium", "MediumItalic", "Bold", "BoldItalic", "Italic")
        val Name= arrayOf("fonts/GoogleSans-")
        val Input= arrayOf(R.drawable.input_primary, R.drawable.input_secondary, R.drawable.input_secondary)

        const val Type: String =".ttf"
    }
    //region General
    const val EMPTY_STRING = ""
    const val SPACE_STRING = " "
    const val ZERO = "0"
    const val ONE = "1"
    const val TWO = "2"
    const val THREE = "3"
    const val FOUR = "4"
    const val FIVE = "5"

    const val REQUEST_CODE__FIRST_GALLERY = 1011
    const val  REQUEST_CODE__SEC_GALLERY = 1012
    const val  REQUEST_CODE__THIRD_GALLERY = 1013
    const val  REQUEST_CODE__FOURTH_GALLERY = 1014
    const val  REQUEST_CODE__FIFTH_GALLERY = 1015
    const val  REQUEST_CODE__PERMISSION_CODE = 1016

    const val RESULT_OK = -1

    const val maxHeight = 1024f
    const val maxWidth = 1024f

    const val CHECHNICIAN = "teknisi"
    const val CUSTOMER = "pelanggan"


    const val ID = "id"
    const val KEY_COLLECTION_USERS = "users"
    const val KEY_NAME = "name"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_PREFERENCE_NAME = "chatAppPreference"
    const val KEY_IS_SIGNED_IN = "isSignedIn"
    const val KEY_USER_ID = "userId"
    const val KEY_IMAGE = "image"
    const val KEY_FCM_TOKEN = "fcmToken"
    const val KEY_USER = "user"
    const val KEY_COLLECTION_CHAT = "chat"
    const val KEY_SENDER_ID = "senderId"
    const val KEY_RECEIVER_NAME = "receiverName"
    const val KEY_RECEIVER_PHOTO = "receiverPhoto"
    const val KEY_RECEIVER_ID = "receiverId"
    const val KEY_MESSAGE = "message"
    const val KEY_TIMESTAMP = "timestamp"
    const val KEY_COLLECTION_CONVERSATIONS = "conversations"
    const val KEY_SENDER_NAME = "senderName"
    const val KEY_SENDER_IMAGE = "senderImage"
    const val KEY_RECEIVER_IMAGE = "receiverImage"
    const val KEY_LAST_MESSAGE = "lastMessage"
}