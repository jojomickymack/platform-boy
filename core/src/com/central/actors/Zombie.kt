package com.central.actors

import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.central.Constants
import com.central.GameObj
import com.central.managers.grav

class Zombie(x: Float, y: Float, width: Float, height: Float) : Enemy() {
    internal var tex = GameObj.zombieSheetTex
    internal var walkSheet = TextureRegion(tex, 0, 0, tex.width, tex.height)
    internal var direction = Direction.LEFT //denotes skeleton's direction
    internal var walkAnimation: Animation<TextureRegion>          // animation instance
    internal var currentFrame: TextureRegion           // current animation frame
    internal var stateTime = 0f

    init {
        sprite = Sprite()
        sprite.setPosition(x, y)
        vel = Vector2(SKELETON_VELOCITY, 0f)
        myRect = Rectangle()
        scaledRect = Rectangle()
        //split the sprite-sheet into different textures
        val tmp = walkSheet.split(walkSheet.regionWidth / ANIMATION_FRAME_SIZE, walkSheet.regionHeight)
        // convert 2D array to 1D
        val walkFrames = tmp[0]

        // create a new animation sequence with the walk frames and time period of 0.04 seconds
        walkAnimation = Animation(0.15f, * walkFrames)

        // set the animation to loop
        walkAnimation.playMode = PlayMode.LOOP_PINGPONG
        // get initial frame
        currentFrame = walkAnimation.getKeyFrame(stateTime, true) as TextureRegion

        sprite.setSize(width, height)
    }

    internal enum class Direction {
        LEFT,
        RIGHT
    }

    override fun act(delta: Float) {
        if (GameObj.gm.player.lifeState === LifeState.ALIVE) {
            //senseAndFollow()
        }
        //senseAndFollow()

        // change the direction based on velocity
        direction = if (vel.x < 0) Direction.LEFT
        else Direction.RIGHT

        when (direction) {
            Direction.RIGHT -> sprite.setFlip(true, false)
            else -> sprite.setFlip(false, false)
        }

        stateTime += delta
        currentFrame = walkAnimation.getKeyFrame(stateTime, true) as TextureRegion

        sprite.setRegion(currentFrame)

        GameObj.mm.myRamps.forEach {
            if (it is PolylineMapObject) {
                val myVerts = it.polyline.transformedVertices
                for (i in 0..myVerts.size - 4 step 4) {
                    if (checkYCollision(Vector2(myVerts[i], myVerts[i + 1]), Vector2(myVerts[i + 2], myVerts[i + 3]), this.myRect, this.vel)) {
                        this.vel.y = 0f
                        this.grounded = true
                        this.sprite.y = findYOnSlope(Vector2(myVerts[i], myVerts[i + 1]), Vector2(myVerts[i + 2], myVerts[i + 3]), this.myRect, this.vel)
                    }
                }
            }
        }

/*
        GameObj.mm.myFloors.forEach {
            if (it is PolylineMapObject) {
                val myVerts = it.polyline.transformedVertices
                for (i in 0..myVerts.size - 4 step 4) {
                    if (checkYCollision(Vector2(myVerts[i], myVerts[i + 1]), Vector2(myVerts[i + 2], myVerts[i + 3]))) {
                        this.vel.y = 0f
                        this.grounded = true
                        checkUpwardSlope(Vector2(myVerts[i], myVerts[i + 1]), Vector2(myVerts[i + 2], myVerts[i + 3]))
                    }
                }
            }
        }
        GameObj.mm.myWalls.forEach {
            if (it is RectangleMapObject) {
                val myShape = it.rectangle
                if (checkXCollision(myShape)) {
                    this.vel.x = -this.vel.x
                }
            }
        }
*/

        this.vel.y -= grav
        this.myRect.set(sprite.x, sprite.y, sprite.width, sprite.height)
        this.scaledRect.set(this.sprite.x * Constants.unitScale + this.vel.x * Constants.unitScale, this.sprite.y * Constants.unitScale + this.vel.y * Constants.unitScale, this.sprite.height * Constants.unitScale, this.sprite.height * Constants.unitScale)

        var myTiles = GameObj.mm.getVertNeighbourTiles(this.vel, this.scaledRect, GameObj.mm.tileLayer)

        myTiles.forEach {
            if (this.scaledRect.overlaps(it)) {
                if (this.vel.y > 0) {
                    this.myRect.y = it.y / Constants.unitScale - this.scaledRect.height / Constants.unitScale
                    this.scaledRect.y = it.y - this.scaledRect.height

                } else if (this.vel.y < 0) {
                    this.myRect.y = it.y / Constants.unitScale + it.height / Constants.unitScale
                    this.scaledRect.y = it.y + it.height
                    this.grounded = true
                }
                this.vel.y = 0f
            }
        }

        myTiles = GameObj.mm.getHorizNeighbourTiles(this.vel, this.scaledRect, GameObj.mm.tileLayer)

        myTiles.forEach {
            if (this.scaledRect.overlaps(it)) {
                this.vel.x = -this.vel.x
                this.direction = Direction.LEFT
            }
        }

        sprite.x = sprite.x + vel.x
        sprite.y = sprite.y + vel.y
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        when (direction) {
            Direction.RIGHT -> batch.draw(currentFrame, this.scaledRect.x, this.scaledRect.y, this.scaledRect.width, this.scaledRect.height)
            else -> batch.draw(currentFrame, this.scaledRect.x + this.scaledRect.width, this.scaledRect.y, -this.scaledRect.width, this.scaledRect.height)
        }

        batch.end()
        super.drawSquare()
        batch.begin()
    }

    fun senseAndFollow() {
        // get the distance between Bob and skeleton
        val difference = GameObj.gm.player.sprite.x - (sprite.x + sprite.width / 2)
        val yDifference = GameObj.gm.player.sprite.y - sprite.y

        //if the distance is between certain threshold, start chasing
        if (Math.abs(difference) <= HORIZ_SENSE_DISTANCE && yDifference < sprite.height && yDifference > 0) {

            val startX: Int
            val startY: Int
            val endX: Int
            val endY: Int

            // get the tiles between bob and the skeleton
            if (difference > 0) {
                endX = GameObj.gm.player.sprite.x.toInt()
                startX = sprite.x.toInt()
            } else {
                startX = GameObj.gm.player.sprite.x.toInt()
                endX = sprite.x.toInt()
            }
            startY = sprite.y.toInt()
            endY = (sprite.y + sprite.height).toInt()

            // get the tiles from map utilities
            val tiles = GameObj.mm.getTiles(startX, startY, endX, endY, GameObj.mm.tileLayer)

            if (tiles.size == 0) {
                // if bob is near and behind the skeleton switch directions
                if (direction == Direction.LEFT && difference > 0) {
                    vel.x *= -1f
                }

                if (direction == Direction.RIGHT && difference < 0) {
                    direction = Direction.LEFT
                    vel.x *= -1f
                }
            }
        }
    }

    companion object {

        private val RESIZE_FACTOR = 900f
        private val SKELETON_VELOCITY = 1f
        private val ANIMATION_FRAME_SIZE = 5
        private val HORIZ_SENSE_DISTANCE = 4f
    }
}