package com.example.reto2grupo1.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class SocketService : Service() {

    private val NOTIFICATION_ID = 321
    private val CHANNEL_ID = "my_channel"

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(CHANNEL_ID,
            "Nombre del canal",
            NotificationManager.IMPORTANCE_LOW
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("obteniendo datos")
            .setContentText("intentando obtener mensajes")
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}