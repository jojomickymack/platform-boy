package com.central

import com.badlogic.gdx.Screen
import ktx.app.KtxGame

class Application : KtxGame<Screen>() {

    override fun create() {
        val game = Game(this)
        addScreen(game)
        setScreen<Game>()
    }
}
