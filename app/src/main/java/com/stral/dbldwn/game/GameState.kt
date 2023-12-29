package com.stral.dbldwn.game

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class GameState(private val gameInterface: GameInterface) {
    var game = Game()
    var job : Job? = null
    var score = 0
    fun initGameState(screenHeight: Int) {
        game = Game()
        game.startGame(screenHeight)
        job = GlobalScope.launch {
            game.kangarooFlow.collect {
                gameInterface.onKangarooGenerated(it)
            }
        }
    }

    fun finishGame() {
        if (!game.shouldStopGame) {
            gameInterface.onShowMenu()
            job?.cancel()
            game.shouldStopGame = true
        }
    }

    fun exit() {
        exitProcess(0)
    }

}
