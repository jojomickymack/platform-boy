package com.central.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.central.Constants
import com.central.GameObj
import com.central.actors.Player
import com.central.actors.ParallaxBackground
import ktx.actors.plusAssign

const val grav = 2f

class GameManager {
    var player = Player()

    var textureStrings = (1..5).map { "parallax/img$it.png" }.toTypedArray()

    var parallaxBackground = ParallaxBackground(GameObj.textures)

    init {
        println("initializing the gm")
        textureStrings.forEach { GameObj.textures = (GameObj.textures + Texture(Gdx.files.internal(it))).toMutableList() }
        parallaxBackground = ParallaxBackground(GameObj.textures)

        with(GameObj) {
            backgroundStg += parallaxBackground

            mm.spawnEnemies()
            stg += player
            hudStg += osgp
        }

        GameObj.music.isLooping = true
        GameObj.music.play()
    }

    fun renderGame(delta: Float) {

        with(GameObj) {
            hudStg.act(delta)

            backgroundStg.act(delta)
            backgroundStg.draw()

            im.handleInput()

            stg.act(delta)
            stg.draw()

            mr.setView(cam)
            mr.render()

            sr.projectionMatrix = cam.combined.scl(Constants.unitScale)
            mm.drawShapes(delta)

            cam.position.x = player.sprite.x * Constants.unitScale
            cam.position.y = player.sprite.y * Constants.unitScale
            cam.update()

            //GameObj.hudStg.batch.projectionMatrix = GameObj.hud.combined
            hudStg.draw()
            hudStg.batch.begin()
            tm.displayMessage(hudStg.batch)
            hudStg.batch.end()
        }
    }

}