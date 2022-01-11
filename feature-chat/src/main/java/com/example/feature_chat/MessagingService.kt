package com.example.feature_chat

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.core_util.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(s: String) {
        super.onNewToken(s)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val id = remoteMessage.data[Constants.KEY_SENDER_ID]
        val name = remoteMessage.data[Constants.KEY_SENDER_NAME]
        val token = remoteMessage.data[Constants.KEY_FCM_TOKEN]

        val notificationId = Random().nextInt()
        val channelId = "chat_message"

        val intent = Intent(this, ChatActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, channelId)
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24)
        builder.setContentTitle(remoteMessage.data[Constants.KEY_SENDER_NAME])
        builder.setContentText(remoteMessage.data[Constants.KEY_MESSAGE])
        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(
                remoteMessage.data[Constants.KEY_MESSAGE]
            )
        )
        builder.priority = NotificationCompat.PRIORITY_HIGH
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName: CharSequence = "Chat Message"
            val channelDescription =
                "This notification channel is used for chat message notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            channel.description = channelDescription
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(notificationId, builder.build())
    }
}