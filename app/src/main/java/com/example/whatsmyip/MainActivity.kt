package com.example.whatsmyip

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.network.Event
import com.example.network.NetworkEvents
import com.example.network.NetworkState
import com.example.network.NetworkStateHolder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.validator.routines.InetAddressValidator


class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private var previousSate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        savedInstanceState?.let {
            previousSate = it.getBoolean("LOST_CONNECTION")
        }

        refresh.setOnRefreshListener(this)

        wifi_off_icon.visibility = if (!NetworkStateHolder.isConnected) View.VISIBLE else View.GONE

        NetworkEvents.observe(this, Observer {
            if (it is Event.ConnectivityEvent)
                handleConnectivityChange(it.state)
        })

    }

    override fun onRefresh() {
        handleConnectivityChange(NetworkStateHolder)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("LOST_CONNECTION", previousSate)
        super.onSaveInstanceState(outState)
    }

    private fun handleConnectivityChange(networkState: NetworkState) {
        refresh.isRefreshing = false

        if (networkState.isConnected) {
            showSnackBar(textView, "The network is back !")
            wifi_off_icon.visibility = View.GONE

            networkState.linkProperties?.linkAddresses?.let {
                for (address in it)
                    if (InetAddressValidator.getInstance().isValidInet4Address(address.toString().substringBefore("/")))
                        ip_address.text = address.toString().substringBefore("/")

            }
        }

        if (!networkState.isConnected) {
            showSnackBar(textView, "No Network !")
            wifi_off_icon.visibility = View.VISIBLE
        }

        previousSate = networkState.isConnected
    }

    override fun onResume() {
        super.onResume()
        handleConnectivityChange(NetworkStateHolder)
    }

    private fun showSnackBar(view: View, text: String) {
        val snackbar = Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("Close") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}