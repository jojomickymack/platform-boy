package com.central.actors

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array
import com.central.GameObj

class ParallaxBackground(private val layers: Array<Texture>) : Actor() {

    private var scroll = 0f
    private val speedDifference = 200

    private var srcX = 0
    private var srcY = 0
    private var flipX = false
    private var flipY = false

    private var speed = 0f
    private var separation = 5

    init {
        layers.forEach {
            it.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.ClampToEdge)
        }

        width = GameObj.backgroundStg.width
        height = GameObj.backgroundStg.height
        scaleY = 1f
        scaleX = scaleY
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        scroll += speed
        for (i in 0 until layers.size) {
            srcX = (scroll + i.toFloat() * this.speedDifference.toFloat() * scroll).toInt()
            batch.draw(layers[i], x, y, originX, originY, width, height,
                    scaleX, scaleY, rotation, srcX, srcY,
                    layers[i].width, layers[i].height * 2 + separation * i, flipX, flipY
            )
        }
    }

    fun setSpeed(newSpeed: Float) {
        this.speed = newSpeed
    }

    fun setHeight(separation: Int) {
        this.separation = separation
    }
}