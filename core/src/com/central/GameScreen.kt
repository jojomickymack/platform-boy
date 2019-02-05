package com.central

import ktx.app.KtxScreen
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.utils.Array as GdxArray
import com.central.managers.InputManager

class Game(val application: Application) : KtxScreen {

    init {
        Gdx.input.inputProcessor = GameObj.hudStg
    }

    override fun render(delta: Float) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        GameObj.gm.renderGame(delta)
    }
}
