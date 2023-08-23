package com.tomer.anibadi.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.tomer.anibadi.R

class NotRec : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotiChannel(manager)


        val notification: Notification = NotificationCompat.Builder(context, "feedsChannel")
            .setContentTitle(intent.getStringExtra("name") ?: "Anganwadi")
            .setContentText("Update the Children details...")
            .setSmallIcon(R.drawable.ic_c0_6m)
            .setPriority(NotificationManager.IMPORTANCE_MAX)
            .build()
        manager.notify(notification.toString().hashCode(), notification)

    }

    private fun createNotiChannel(man: NotificationManager) {
        if (man.getNotificationChannel("feedsChannel") != null) return
        val channel = NotificationChannel("feedsChannel", "Pending", NotificationManager.IMPORTANCE_HIGH)
        channel.description = "This channel is for new feed Notifications"
        channel.enableLights(true)
        channel.lightColor = Color.BLUE
        channel.enableVibration(true)
        man.createNotificationChannel(channel)
    }

    private fun getBMp(id: Int, context: Context): Bitmap {
        return BitmapFactory.decodeResource(
            context.resources,
            id
        )
    }
}