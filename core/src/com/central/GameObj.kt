package com.central

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.central.managers.*

object Constants {
    val unitScale = 1 / 64f
}

object GameObj {
    var width = Gdx.graphics.height.toFloat()
    var height = Gdx.graphics.width.toFloat()

    var mm = MapManager()

    val sb = SpriteBatch()
    val cam = OrthographicCamera(width, height)
    val hud = OrthographicCamera(width, height)
    val view = FitViewport(800f, 480f, cam)
    val hudView = FitViewport(800f, 480f, hud)
    val backgroundView = FitViewport(800f, 480f, hud)
    val stg = Stage(view, sb)
    val hudStg = Stage(hudView, sb)
    val backgroundStg = Stage(backgroundView, sb)
    val sr = ShapeRenderer()

    var gm = GameManager()
    var tm = TextManager()
    var im = InputManager()
    var gpm = GamepadManager()

    var score = 0
    var lives = 5

    val mapProps = mm.map.properties
    val mapWidth = mapProps.get("width").toString().toFloat()
    val mapHeight = mapProps.get("height").toString().toFloat()


    init {
        cam.setToOrtho(false, 12f, 8f)
        tm.initialize(hudStg.width, hudStg.height)
    }
}