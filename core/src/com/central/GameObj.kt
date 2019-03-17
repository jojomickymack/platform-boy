package com.central

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.FitViewport
import com.central.actors.OnScreenGamepad
import com.central.managers.*

object Constants {
    val unitScale = 1 / 64f
}

object GameObj {
    var width = Gdx.graphics.height.toFloat()
    var height = Gdx.graphics.width.toFloat()

    val map = TmxMapLoader().load("map01.tmx")
    val mr = OrthogonalTiledMapRenderer(map, Constants.unitScale)

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
    var mm = MapManager()

    val music = Gdx.audio.newMusic(Gdx.files.internal("theme.ogg"))

    var textures = mutableListOf<Texture>()
    val adventurerSheetTex = Texture("adventurer_sheet.png")
    val zombieSheetTex = Texture(Gdx.files.internal("zombie.png"))

    val osgp = OnScreenGamepad()

    var gm = GameManager()
    var tm = TextManager()
    var im = InputManager()
    var gpm = GamepadManager()

    var score = 0
    var lives = 5

    val mapProps = map.properties
    val mapWidth = mapProps.get("width").toString().toFloat()
    val mapHeight = mapProps.get("height").toString().toFloat()

    init {
        cam.setToOrtho(false, 12f, 8f)
        tm.initialize(hudStg.width, hudStg.height)
    }

    fun dispose() {
        map.dispose()
        mr.dispose()
        sb.dispose()
        stg.dispose()
        hudStg.dispose()
        backgroundStg.dispose()
        sr.dispose()
        osgp.dispose()
        music.dispose()

        textures.forEach { it.dispose() }
        adventurerSheetTex.dispose()
        zombieSheetTex.dispose()
    }
}