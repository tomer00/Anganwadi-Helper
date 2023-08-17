package com.tomer.anibadi.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.gson.Gson
import com.tomer.anibadi.adap.WidService
import com.tomer.anibadi.modal.WidgetMod

class NotRec : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val int = Intent(context, WidService::class.java)

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        createNotiChannel(manager)

        val mod = Gson().fromJson(intent.getStringExtra("data"), WidgetMod::class.java)

        int.putExtra("data", Gson().toJson(mod))
        val pendingIntent = PendingIntent.getService(
            context,
            0,
            int,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

//        val notification: Notification = NotificationCompat.Builder(context, "feedsChannel")
//            .setContentTitle(rechModal.phone)
//            .setContentText(rechModal.amount)
//            .setSmallIcon(R.drawable.iclogo)
//            .setLargeIcon(getBMp(mod.icon,context))
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationManager.IMPORTANCE_MAX)
//            .build()
//        manager.notify(123, notification)

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