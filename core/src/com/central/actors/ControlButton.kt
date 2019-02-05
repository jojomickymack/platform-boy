package com.central.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.Actor

class ControlButton(tex: Texture): Actor() {
    val sprite = Sprite(tex)

    override fun draw(batch: Batch, parentAlpha: Float) {
        this.sprite.draw(batch)
    }
}