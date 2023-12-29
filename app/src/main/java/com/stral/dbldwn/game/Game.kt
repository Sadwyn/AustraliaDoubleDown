package com.stral.dbldwn.game

import com.stral.dbldwn.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class Game {
    var shouldStopGame = false
    val kangarooFlow = MutableStateFlow(Kangaroo(R.drawable.cang1, y=400))

    fun startGame(screenHeight: Int) {
        GlobalScope.launch {
            while (!shouldStopGame) {
                delay(1000)
                generateKangaroo(screenHeight)
            }
        }
    }

    private fun generateKangaroo(screenHeight: Int) {
        val kangaroo = Kangaroo()
        val randomYPos = Random.nextInt(0, screenHeight - 220)
        when (Random.nextInt(0, 3)) {
            0 -> kangaroo.imageRes = R.drawable.cang1
            1 -> kangaroo.imageRes = R.drawable.cang2
            2 -> kangaroo.imageRes = R.drawable.cang3
            3 -> kangaroo.imageRes = R.drawable.cang4
        }
        kangaroo.y = randomYPos
        kangarooFlow.value = kangaroo
    }
}