package com.stral.dbldwn

import android.R
import android.app.Application
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executors

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}