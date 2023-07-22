package com.example.myapplication.firebase


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.example.myapplication.activities.ChatActivity
import com.example.myapplication.utilities.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MessagingService: FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if(message.data.isNotEmpty()){

            val bundle = Bundle()
            val notificationId = Random.nextInt()
            val id = message.data[Constants.KEY_USER_ID].toString()
            val name = message.data[Constants.KEY_NAME].toString()
            val token = message.data[Constants.KEY_FCM_TOKEN].toString()

            bundle.putString(Constants.KEY_NAME, name)
            bundle.putString(Constants.KEY_USER_ID, id)
            bundle.putString(Constants.KEY_FCM_TOKEN, token)
            val channelId = "chat_message"


            val intent = Intent(this, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtras(bundle)
            intent.action = Intent.ACTION_PICK_ACTIVITY

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                val builder = NotificationCompat.Builder(this, channelId)
                builder.setSmallIcon(R.drawable.round_notifications_active_24)
                builder.setContentTitle(name)
                builder.setContentText(message.data[Constants.KEY_MESSAGE])
                builder.setContentIntent(pendingIntent)
                builder.setAutoCancel(true)
                builder.priority = NotificationCompat.PRIORITY_DEFAULT


                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    val channel = NotificationChannel(channelId, "Chat message", NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(channel)
                }

                val notificationManagerCompat = NotificationManagerCompat.from(this)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d("Messaging Service", "request Permission")
                    return
                } else Log.d("Messaging Service", "Permission Granted")
                notificationManagerCompat.notify(notificationId, builder.build())
            }
        }
    }

}