package com.example.whatsmyip

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.IBinder
import android.text.format.Formatter
import android.widget.RemoteViews
import androidx.core.content.ContextCompat


class UpdateIPService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

        val allWidgetIds = intent!!.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        val ipAddress = Formatter.formatIpAddress(ip)

        allWidgetIds!!.forEach { id ->
            run {
                val view = RemoteViews(applicationContext.packageName, R.layout.widget_whats_my_ip)
                view.setTextViewText(R.id.widget_ip_address, ipAddress)
                view.setTextColor(R.id.widget_ip_address, ContextCompat.getColor(applicationContext, R.color.white))
                view.setTextViewTextSize(R.id.widget_ip_address, 2, 20F)

                val clickIntent = Intent(
                    applicationContext,
                    IPAddressProvider::class.java
                )

                clickIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                clickIntent.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    allWidgetIds
                )

                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext, 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                view.setOnClickPendingIntent(R.id.widget_ip_address, pendingIntent)

                appWidgetManager!!.updateAppWidget(id, view);
            }
        }

        stopSelf()

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}