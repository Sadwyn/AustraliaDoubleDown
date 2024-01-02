package com.stral.dbldwn

import android.app.DownloadManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity


class NotAGameActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_not_agame)
        webView = findViewById(R.id.webView)
        val url = intent.getStringExtra("URL")
        val appInstanceId = intent.getStringExtra("APP_INSTANCE_ID")
        val advertisingId = intent.getStringExtra("ADVERTISING_ID")
        val androidDeepLink = intent.getStringExtra("ANDROID_DEEP_LINK")
        val facebookDeepLink = intent.getStringExtra("FACEBOOK_DEEP_LINK")


        val fullUrl = StringBuilder()
        fullUrl.append(url)
        var urlHasParam = false
         if (appInstanceId != null) {
             urlHasParam = true
             fullUrl.append("?appInstanceId=${appInstanceId}")
         }
         if (advertisingId != null) {
             fullUrl.append(if (!urlHasParam) "?" else "&")
             urlHasParam = true
             fullUrl.append("advertisingId=${advertisingId}")
         }
         if (androidDeepLink != null) {
             fullUrl.append(if (!urlHasParam) "?" else "&")
             urlHasParam = true
             fullUrl.append("androidDeepLink=${androidDeepLink}")
         }
         if (facebookDeepLink != null) {
             fullUrl.append(if (!urlHasParam) "?" else "&")
             fullUrl.append("facebookDeepLink=${facebookDeepLink}")
         }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(url!!)
                    return true
                }
                return false
            }

        }
        webView.settings.domStorageEnabled = true
        webView.settings.saveFormData = true
        webView.settings.allowContentAccess = true
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.setSupportZoom(true)
        webView.webViewClient = WebViewClient()
        webView.isClickable = true
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(
                Uri.parse(url)
            )
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "AustraliaDoubleDown_file"
            )
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        }
        webView.loadUrl(fullUrl.toString());
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}