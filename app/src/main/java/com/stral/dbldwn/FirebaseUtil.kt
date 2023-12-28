package com.stral.dbldwn

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object FirebaseUtil {

    fun fetchConfig(activity: Activity, result: (FirebaseRemoteConfig?) -> Unit) {
        val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d("TAG", "Config params updated: $updated")
                    Toast.makeText(
                        activity,
                        "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT,
                    ).show()
                    result(remoteConfig)
                } else {
                    Toast.makeText(
                        activity,
                        "No internet connection",
                        Toast.LENGTH_SHORT,
                    ).show()
                    result.invoke(null)
                }
            }
    }

}