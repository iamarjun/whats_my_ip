package com.example.whatsmyip

import android.app.Application
import com.example.network.NetworkStateHolder.registerConnectivityBroadcaster

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        registerConnectivityBroadcaster()
    }

}