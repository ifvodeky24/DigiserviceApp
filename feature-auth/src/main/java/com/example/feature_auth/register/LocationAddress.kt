package com.example.feature_auth.register

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Message
import timber.log.Timber
import java.io.IOException
import java.util.*


class LocationAddress {

    fun getAddressFromLocation(
        latitude: Double,
        longitude: Double,
        context: Context,
        handler: Handler
    ){
        val thread = object : Thread() {
            override fun run() {
                val geoCoder = Geocoder(
                    context,
                    Locale.getDefault()
                )
                var result: String = null.toString()
                try {
                    val addressList = geoCoder.getFromLocation(
                        latitude, longitude, 1
                    )
                    if ((addressList != null && addressList.size > 0)) {
                        val address = addressList[0]
                        result = address.getAddressLine(0)
                    }
                } catch (e: IOException) {
                    Timber.e("Unable connect to GeoCoder $e")
                } finally {
                    val message = Message.obtain()
                    message.target = handler
                    message.what = 1
                    val bundle = Bundle()
                    bundle.putString("address", result)
                    message.data = bundle
                    message.sendToTarget()
                }
            }
        }
        thread.start()
    }
}