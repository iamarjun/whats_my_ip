package com.example.whatsmyip

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent


class IPAddressProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {

//        val wifiMgr = context!!.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wifiInfo = wifiMgr.connectionInfo
//        val ip = wifiInfo.ipAddress
//        val ipAddress = Formatter.formatIpAddress(ip)
//
//        appWidgetIds!!.forEach { id ->
//            run {
//                val view = RemoteViews(context.packageName, R.layout.widget_whats_my_ip)
//                view.setTextViewText(R.id.widget_ip_address, ipAddress)
//                view.setTextColor(R.id.widget_ip_address, ContextCompat.getColor(context, R.color.white))
//                view.setTextViewTextSize(R.id.widget_ip_address, 2, 20F)
//
//                appWidgetManager!!.updateAppWidget(id, view);
//            }
//        }

        val intent = Intent(
            context!!.applicationContext,
            UpdateIPService::class.java
        )

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        context.startService(intent)
    }
}