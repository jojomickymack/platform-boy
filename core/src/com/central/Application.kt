package com.central

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import ktx.app.KtxGame
import ktx.assets.toInternalFile
import ktx.async.enableKtxCoroutines

class Application : KtxGame<Screen>() {

    override fun create() {
        enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)

        val game = Game(this)
        addScreen(game)
        setScreen<Game>()
    }
}
