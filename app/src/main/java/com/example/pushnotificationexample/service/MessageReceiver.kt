package com.example.pushnotificationexample.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.pushnotificationexample.MainActivity
import com.example.pushnotificationexample.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessageReceiver : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        //handle when receive notification via data event
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("TTT", "onMessageReceived: ${remoteMessage.data}")
            showNotification(remoteMessage.data["title"], remoteMessage.data["message"])
        }

        //handle when receive notification
        if (remoteMessage.notification != null) {
            Log.d("TTT", "onMessageReceived: ${remoteMessage.notification}")
            showNotification(
                remoteMessage.notification!!.title, remoteMessage.notification!!
                    .body
            )
        }
    }

    override fun onNewToken(p0: String) {
        Log.d("TTT", "onNewToken: $p0")
    }

    private fun getCustomDesign(title: String?, message: String?): RemoteViews? {
        val remoteViews = RemoteViews(applicationContext.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher)
        return remoteViews
    }

    private fun showNotification(title: String?, message: String?) {
        val intent = Intent(this, MainActivity::class.java)
        val channelId = "web_app_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val uri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setSound(uri)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        builder =
            builder.setContent(getCustomDesign(title, message))
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "web_app", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setSound(uri, null)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(0, builder.build())
    }
}