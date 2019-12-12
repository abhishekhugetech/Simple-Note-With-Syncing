package com.abhishek.notesyncing.util

import android.content.Context
import android.net.ConnectivityManager


val Context.isNetworkAvailable: Boolean
    get() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetworkInfo
        return network != null && network.isConnected
    }