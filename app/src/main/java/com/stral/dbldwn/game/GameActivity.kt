package com.stral.dbldwn.game

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.stral.dbldwn.MenuDialogListener
import com.stral.dbldwn.R


class GameActivity : AppCompatActivity(), GameInterface, MenuDialogListener {
    private var gameState: GameState? = null
    private var layout: FrameLayout? = null
    private lateinit var mp: MediaPlayer
    private val kangaroos = mutableListOf<ImageView>()
    private val objectAnimators = mutableListOf<ObjectAnimator>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        layout = findViewById(R.id.kangarooField)
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        alertDialog.setTitle("Privacy policy")
        alertDialog.setMessage(getString(R.string.popup_message))
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, which ->
            dialog.cancel()
            onShowMenu()
        }
        mp = MediaPlayer.create(this, R.raw.click_sound)
        val alert: AlertDialog = alertDialog.create()
        alert.show()
        gameState = GameState(this)
    }

    override fun onKangarooGenerated(kangaroo: Kangaroo) {
        Handler(Looper.getMainLooper()).post {

            val layoutParams = FrameLayout.LayoutParams(200, 200)
            val kangarooView = ImageView(this)
                .apply {
                    x = 0f
                    y = kangaroo.y.toFloat()
                    setBackgroundResource(kangaroo.imageRes)
                }

            kangarooView.layoutParams = layoutParams
            layout?.addView(kangarooView)
            kangaroos.add(kangarooView)
            val animation =
                ObjectAnimator.ofFloat(kangarooView, "translationX", layout?.width?.toFloat()!!)
            objectAnimators.add(animation)
            animation.duration = 2000
            animation.addUpdateListener {
                if (kangarooView.x >= layout?.width?.toFloat()!! - 220) {
                    for (k in kangaroos) {
                        k.visibility = View.GONE
                        layout?.removeAllViews()
                    }
                    objectAnimators.forEach {
                        it.removeAllListeners()
                        it.cancel()
                    }
                    gameState?.finishGame()
                }
            }
            animation.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {

                }

                override fun onAnimationCancel(animation: Animator) {
                    kangarooView.visibility = View.GONE
                    gameState?.let {
                        it.score++
                    }
                    layout?.removeView(kangarooView)
                }

                override fun onAnimationRepeat(animation: Animator) {
                    /* no-op*/
                }
            })
            kangarooView.setOnClickListener {
                animation.cancel()
                try {
                    if (mp.isPlaying) {
                        mp.stop()
                        mp.release()
                        mp = MediaPlayer.create(this, R.raw.click_sound)
                    }
                    mp.start()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            animation.start()

        }
    }

    override fun onShowMenu() {
        val dialogFragment = MenuDialog(this, gameState!!.score)
        if (!dialogFragment.isAdded) {
            dialogFragment.show(supportFragmentManager, "MenuDialog")
        }
    }

    override fun onStartClick() {
        gameState = GameState(this)
        gameState?.initGameState(layout?.height!!)
    }

    override fun onRulesClick() {
        val dialog = AlertDialog.Builder(this, R.style.MyDialogTheme)
            .setTitle(getString(R.string.game_rules_title))
            .setMessage(getString(R.string.game_rules_message))
            .setPositiveButton(
                "Got it"
            ) { dialog, which -> dialog.dismiss() }
            .create()
        dialog.show()
    }

    override fun onExitClick() {
        gameState?.exit()
    }
}