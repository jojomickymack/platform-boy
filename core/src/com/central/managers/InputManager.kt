package com.central.managers

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.central.GameObj

class InputManager {
    var lPressed = false
    var rPressed = false
    var aPressed = false
    var bPressed = false

    init {
        GameObj.hudStg.addListener(object : InputListener() {
            override fun keyDown(event: InputEvent, keycode: Int): Boolean {
                if (keycode == Input.Keys.SPACE) {
                    aPressed = true
                }
                if (keycode == Input.Keys.LEFT || keycode == Input.Keys.A) {
                    lPressed = true
                    rPressed = false
                } else if (keycode == Input.Keys.RIGHT || keycode == Input.Keys.D) {
                    lPressed = false
                    rPressed = true
                }
                return false
            }

            override fun keyUp(event: InputEvent, keycode: Int): Boolean {
                when (keycode) {
                    Input.Keys.SPACE -> {
                        aPressed = false
                    }
                    Input.Keys.LEFT, Input.Keys.A -> {
                        lPressed = false
                    }
                    Input.Keys.RIGHT, Input.Keys.D -> {
                        rPressed = false
                    }
                }
                return false
            }
        })
    }

    fun handleInput() {
        if (aPressed) {
            GameObj.gm.player.jumping = GameObj.gm.player.grounded
        } else GameObj.gm.player.jumping = false
        if (lPressed) {
            GameObj.gm.player.goLeft = true
            GameObj.gm.player.goRight = false
        } else if (rPressed) {
            GameObj.gm.player.goLeft = false
            GameObj.gm.player.goRight = true
        } else if (!lPressed && !rPressed) {
            GameObj.gm.player.goLeft = false
            GameObj.gm.player.goRight = false
        }
    }
}