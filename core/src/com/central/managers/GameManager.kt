package com.central.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.utils.Array
import com.central.Constants
import com.central.GameObj
import com.central.actors.ControlButton
import com.central.actors.OnScreenGamepad
import com.central.actors.Player
import com.central.actors.ParallaxBackground
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const

const val grav = 2f

class GameManager {
    var player = Player()

    private var textures = Array<Texture>()

    val textureStrings = arrayOf("parallax/img1.png",
            "parallax/img2.png",
            "parallax/img3.png",
            "parallax/img4.png",
            "parallax/img5.png")

    var parallaxBackground = ParallaxBackground(textures)
    val osgp = OnScreenGamepad()

    val music = Gdx.audio.newMusic(Gdx.files.internal("theme.ogg"))

    init {
        textureStrings.forEach { textures.add(Texture(Gdx.files.internal(it))) }
        parallaxBackground = ParallaxBackground(textures)
        GameObj.backgroundStg.addActor(parallaxBackground)

        GameObj.mm.spawnEnemies()
        GameObj.stg.addActor(player)
        GameObj.hudStg.addActor(osgp)

        music.isLooping = true
        music.play()
    }

    fun renderGame(delta: Float) {
        GameObj.hudStg.act(delta)

        GameObj.backgroundStg.act(delta)
        GameObj.backgroundStg.draw()

        GameObj.im.handleInput()

        GameObj.stg.act(delta)
        GameObj.stg.draw()

        GameObj.mm.mr.setView(GameObj.cam)
        GameObj.mm.mr.render()

        GameObj.sr.projectionMatrix = GameObj.cam.combined.scl(Constants.unitScale)
        GameObj.mm.drawShapes(delta)

        GameObj.cam.position.x = player.sprite.x * Constants.unitScale
        GameObj.cam.position.y = player.sprite.y * Constants.unitScale
        GameObj.cam.update()

        //GameObj.hudStg.batch.projectionMatrix = GameObj.hud.combined
        GameObj.hudStg.draw()
        GameObj.hudStg.batch.begin()
        GameObj.tm.displayMessage(GameObj.hudStg.batch)
        GameObj.hudStg.batch.end()
    }

}