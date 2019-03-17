package com.central.managers

import com.badlogic.gdx.graphics.Color.PINK
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import com.central.actors.Zombie

import com.central.GameObj
import ktx.actors.plusAssign
import ktx.graphics.use

class MapManager {
    // these layers go with the commented out shape parsing logic below which is also commented out
/*
    val floorLayer = map.layers.get("floors01") as MapLayer
    val myFloors = floorLayer.objects
    val shapeLayer = map.layers.get("shapes01") as MapLayer
    val myShapes = shapeLayer.objects
    val wallLayer = map.layers.get("walls01") as MapLayer
    val myWalls = wallLayer.objects
*/

    val tileLayer = GameObj.map.layers.get("tiles01") as TiledMapTileLayer
    val collectablesLayer = GameObj.map.layers.get("collectables") as TiledMapTileLayer
    val enemyLayer = GameObj.map.layers.get("enemies") as MapLayer
    val rampLayer = GameObj.map.layers.get("ramp01") as MapLayer
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

                with(GameObj.sr) {
                    use(Line) {
                        color = PINK
                        polyline(myVerts)
                    }
                }
            }
        }
    }

    // the reason this is included but commented out is because it's so potentially useful.
    // If you draw shapes in the tilemap this will draw them into the game
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
        myEnemies.forEach { if (it is TextureMapObject && it.name == "zombie") GameObj.stg += Zombie(it.x, it.y, it.textureRegion.regionWidth.toFloat(), it.textureRegion.regionHeight.toFloat()) }
    }

    // create a pool of rectangle objects for collision detection
    private val rectPool = object : Pool<Rectangle>() {
        override fun newObject(): Rectangle = Rectangle()
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
        val startY = rect.y.toInt()
        val endY = (rect.y + rect.height).toInt()

        // if the sprite is moving right, get the tiles to its right side
        // if the sprite is moving left, get the tiles to its left side
        val startX = if (velocity.x > 0) (rect.x + rect.width).toInt() else rect.x.toInt()
        val endX = startX

        return getTiles(startX, startY, endX, endY, tileLayer)
    }

    fun getVertNeighbourTiles(velocity: Vector2, rect: Rectangle, tileLayer: TiledMapTileLayer): Array<Rectangle> {
        val startX = rect.x.toInt()
        val endX = (rect.x + rect.width).toInt()
        // if sprite is moving up, get the tiles above it
        // if sprite is moving down, get the tiles below it
        val startY = if (velocity.y > 0) (rect.y + rect.height).toInt() else rect.y.toInt()
        val endY = startY

        return getTiles(startX, startY, endX, endY, tileLayer)
    }
}