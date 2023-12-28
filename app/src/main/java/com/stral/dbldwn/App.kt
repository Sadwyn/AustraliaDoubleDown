package com.stral.dbldwn

import android.R
import android.app.Application
import android.os.AsyncTask
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
        Firebase.analytics.appInstanceId.addOnCompleteListener {
            appInstanceId = it.result
        }
        Executors.newSingleThreadExecutor().execute {
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
                //todo rework retrieving advertising id, now it retrieves after flow completes
                appUUID = adInfo.id
            } catch (e: Exception) {

            }
        }
    }

    companion object {
        var appInstanceId: String? = null
        var appUUID: String? = null
    }
}