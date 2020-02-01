package com.example.whatsmyip

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.*


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        refresh.setOnRefreshListener(this)

        detectNetwork()
    }

    override fun onRefresh() {
        detectNetwork()
    }

    private fun detectNetwork() {

        refresh.isRefreshing = false

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = cm.allNetworkInfo

        for (netInfo in networkInfo) {

            if (netInfo.typeName.equals("WIFI", ignoreCase = true))

                if (netInfo.isConnected) {
                    ip_address.text = "WiFi IP Address ${getDeviceIPAddressForWifi()}"
                }

            if (netInfo.typeName.equals("MOBILE", ignoreCase = true))

                if (netInfo.isConnected) {
                    ip_address.text = "Cellular Network IP Address ${getDeviceIPAddress(true)}"
                }
        }
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