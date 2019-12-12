package com.abhishek.notesyncing.util

import android.app.NotificationManager
import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import com.abhishek.notesyncing.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


object NotificationUtil {

    private val CHANNEL_ID = "notes_syncing_app"
    private val notificationId = 1001

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val description = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context){
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_cloud_circle_black_24dp)
            .setContentTitle("Syncing Notes")
            .setContentText("We are syncing all the notes that you have created earlier.")
            .setProgress(100,50,true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }

    fun showNotificationCompleted(context: Context){
        createNotificationChannel(context)
        val builder = NotificationCompat.Builder(context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_tick_inside_circle_g)
            .setContentTitle("Syncing Notes Completed")
            .setContentText("All your previous notes are synced.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }
    }
}