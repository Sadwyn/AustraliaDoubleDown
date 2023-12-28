package com.stral.dbldwn

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialog.setTitle("Rules")
        alertDialog.setMessage(getString(R.string.popup_message))
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog.cancel()
            //todo Start the Game
        }
        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }
}