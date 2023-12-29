package com.stral.dbldwn.game

import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.stral.dbldwn.MenuDialogListener
import com.stral.dbldwn.R


class MenuDialog(private val menuDialogListener: MenuDialogListener, private val score: Int) :
    DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.menu_dialog, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.scoreTextView).text = "Score: $score"
        view.findViewById<Button>(R.id.startBtn).setOnClickListener {
            dismiss()
            menuDialogListener.onStartClick()
        }
        view.findViewById<Button>(R.id.rulesBtn).setOnClickListener {
            menuDialogListener.onRulesClick()
        }
        view.findViewById<Button>(R.id.exitBtn).setOnClickListener {
            menuDialogListener.onExitClick()
        }
    }

    override fun onResume() {
        val window = dialog!!.window
        val size = Point()
        val display = window!!.windowManager.defaultDisplay
        display.getSize(size)
        window.setLayout((size.x * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        super.onResume()
    }
}