package com.central.managers

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.central.Constants
import com.central.actors.Zombie

import com.central.GameObj

class MapManager {
    val map = TmxMapLoader().load("map01.tmx")
    val mr = OrthogonalTiledMapRenderer(map, Constants.unitScale)

/*
    val floorLayer = map.layers.get("floors01") as MapLayer
    val myFloors = floorLayer.objects
    val shapeLayer = map.layers.get("shapes01") as MapLayer
    val myShapes = shapeLayer.objects
    val wallLayer = map.layers.get("walls01") as MapLayer
    val myWalls = wallLayer.objects
*/

    val tileLayer = map.layers.get("tiles01") as TiledMapTileLayer
    val collectablesLayer = map.layers.get("collectables") as TiledMapTileLayer
    val enemyLayer = map.layers.get("enemies") as MapLayer
    val rampLayer = map.layers.get("ramp01") as MapLayer
    val myRamps = rampLayer.objects
    val myEnemies = enemyLayer.objects

    // denotes a list of tiles which are likely to collide with the player
    private val tiles = Array<Rectangle>()

    init {

    }

    fun drawShapes(delta: Float) {
        myRamps.forEach {
            if (it is PolylineMapObject) {
                val myVerts = it.polyline.transformedVertices

                GameObj.sr.color = Color.PINK
                GameObj.sr.begin(ShapeRenderer.ShapeType.Line)
                GameObj.sr.polyline(myVerts)
                GameObj.sr.end()
            }
        }
    }

/*
        myShapes.forEach {
            if (it is PolygonMapObject) {
                val myVerts = it.polygon.transformedVertices

                GameObj.sr.color = Color.LIGHT_GRAY
                GameObj.sr.begin(ShapeRenderer.ShapeType.Filled)
                for (points in 0..myVerts.size - 6 step 6) {
                    GameObj.sr.triangle(myVerts[points], myVerts[points + 1], myVerts[points + 2],
                            myVerts[points + 3], myVerts[points + 4], myVerts[points + 5])
                }
                GameObj.sr.end()
            }
        }
        myFloors.forEach {
            if (it is PolylineMapObject) {
                val myVerts = it.polyline.transformedVertices

                GameObj.sr.color = Color.PINK
                GameObj.sr.begin(ShapeRenderer.ShapeType.Line)
                GameObj.sr.polyline(myVerts)
                GameObj.sr.end()
            }
        }
        myWalls.forEach {
            if (it is RectangleMapObject) {
                val myShape = it.rectangle
                GameObj.sr.color = Color.RED
                GameObj.sr.begin(ShapeRenderer.ShapeType.Line)
                GameObj.sr.rect(myShape.x, myShape.y, myShape.width, myShape.height)
                GameObj.sr.end()
            }
        }
    }
*/
    fun spawnEnemies() {
        myEnemies.forEach {
            if (it is TextureMapObject && it.name == "zombie") {
                val zombie = Zombie(it.x, it.y, it.textureRegion.regionWidth.toFloat(), it.textureRegion.regionHeight.toFloat())
                GameObj.stg.addActor(zombie)
            }
        }
    }

    // create a pool of rectangle objects for collision detection
    private val rectPool = object : Pool<Rectangle>() {
        override fun newObject(): Rectangle {
            return Rectangle()
        }
    }

    fun getTiles(startX: Int, startY: Int, endX: Int, endY: Int, tileLayer: TiledMapTileLayer): Array<Rectangle> {
        rectPool.freeAll(tiles)

        tiles.clear()

        for (y in startY..endY) {
            for (x in startX..endX) {
                val cell = tileLayer.getCell(x, y)
                if (cell != null) {
                    val rect = rectPool.obtain()
                    rect.set(x.toFloat(), y.toFloat(), 1f, 1f)
                    tiles.add(rect)
                }
            }
        }
        return tiles
    }

    fun getHorizNeighbourTiles(velocity: Vector2, rect: Rectangle, tileLayer: TiledMapTileLayer): Array<Rectangle> {
        val startX: Int
        val startY: Int
        val endX: Int
        val endY: Int
        // if the sprite is moving right, get the tiles to its right side
        if (velocity.x > 0) {
            endX = (rect.x + rect.width).toInt()
            startX = endX
        } else { // if the sprite is moving left, get the tiles to its left side
            endX = rect.x.toInt()
            startX = endX
        }
        startY = rect.y.toInt()
        endY = (rect.y + rect.height).toInt()

        return getTiles(startX, startY, endX, endY, tileLayer)
    }

    fun getVertNeighbourTiles(velocity: Vector2, rect: Rectangle, tileLayer: TiledMapTileLayer): Array<Rectangle> {
        val startX: Int
        val startY: Int
        val endX: Int
        val endY: Int
        // if sprite is moving up, get the tiles above it
        if (velocity.y > 0) {
            endY = (rect.y + rect.height).toInt()
            startY = endY
        } else { // if sprite is moving down, get the tiles below it
            endY = rect.y.toInt()
            startY = endY
        }
        startX = rect.x.toInt()
        endX = (rect.x + rect.width).toInt()

        return getTiles(startX, startY, endX, endY, tileLayer)
    }
}