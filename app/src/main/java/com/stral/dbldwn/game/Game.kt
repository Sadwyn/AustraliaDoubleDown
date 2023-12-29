package com.stral.dbldwn.game

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class Game {
    var shouldStopGame = false
    val kangarooFlow = MutableStateFlow(Kangaroo())

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
        kangaroo.y = randomYPos
        kangarooFlow.value = kangaroo
    }
}