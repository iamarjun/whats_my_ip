package com.example.whatsmyip

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.IBinder
import android.text.format.Formatter
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*


class UpdateIPService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val appWidgetManager = AppWidgetManager.getInstance(applicationContext)

        val allWidgetIds = intent!!.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)

//        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiInfo = wifiMgr.connectionInfo
//        val ip = wifiInfo.ipAddress
//        val ipAddress = Formatter.formatIpAddress(ip)

        allWidgetIds!!.forEach { id ->
            run {
                val view = RemoteViews(applicationContext.packageName, R.layout.widget_whats_my_ip)
                view.setTextViewText(R.id.widget_ip_address, detectNetwork())
                view.setTextColor(R.id.widget_ip_address, ContextCompat.getColor(applicationContext, R.color.white))
                view.setTextViewTextSize(R.id.widget_ip_address, 2, 18f)

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

    private fun detectNetwork(): String {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.allNetworkInfo

        for (netInfo in networkInfo) {

            if (netInfo.typeName.equals("WIFI", ignoreCase = true))

                if (netInfo.isConnected)

                    return getDeviceIPAddressForWifi()

            if (netInfo.typeName.equals("MOBILE", ignoreCase = true))

                if (netInfo.isConnected)

                    return getDeviceIPAddress(true)
        }

        return ""
    }

    @SuppressLint("DefaultLocale")
    private fun getDeviceIPAddress(useIPv4: Boolean): String {
        try {
            val networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (networkInterface in networkInterfaces) {
                val inetAddresses = Collections.list(networkInterface.inetAddresses)
                for (inetAddress in inetAddresses) {
                    if (!inetAddress.isLoopbackAddress) {
                        val sAddr = inetAddress.hostAddress.toUpperCase()
                        val isIPv4 = inetAddress is Inet4Address
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                val delim = sAddr.indexOf('%')
                                return if (delim < 0) sAddr else sAddr.substring(0, delim)
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return ""
    }

    private fun getDeviceIPAddressForWifi(): String {
        val wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        val ipAddress = Formatter.formatIpAddress(ip)

        return ipAddress
    }


}