package com.central.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line
import com.badlogic.gdx.maps.objects.PolylineMapObject
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.central.Constants
import com.central.GameObj
import com.central.managers.grav
import ktx.graphics.use


class State

class PlayerStates {
    companion object States {
        val standing = State()
        val walking = State()
        val jumping = State()
        val riding = State()
    }
}

enum class LifeState {
    ALIVE, DEAD
}

class Player : Actor() {
    private val tex = Texture("adventurer_sheet.png")
    private var walkSheet = TextureRegion(tex, 0, 0, tex.width, tex.height)
    private var currentFrame: TextureRegion
    private val vel = Vector2(0f, 0f)
    private val h = 30f
    private val damping = Vector2(0.9f, 0.9f)

    private val maxVelocity = 5f
    private val jumpVelocity = 40f

    // avoiding name collision by having this variable be rect - that's used elsewhere
    var myRect = Rectangle()
    var scaledRect = Rectangle()

    var lifeState = LifeState.ALIVE
    val sprite = Sprite()

    var jumping = false
    var grounded = false
    var goLeft = false
    var goRight = false
    var facesRight = true

    var stateTime = 0f
    var state = PlayerStates.walking

    val regions = walkSheet.split(80, 96)[0]
    val stand = Animation(0F, regions[0])
    val walk = Animation(0.15f, regions[1], regions[2])
    val jump = Animation(0F, regions[3])

    val jumpSnd = Gdx.audio.newSound(Gdx.files.internal("jump.ogg"))
    val pickupSnd = Gdx.audio.newSound(Gdx.files.internal("pickup.ogg"))
    val hitSnd = Gdx.audio.newSound(Gdx.files.internal("hit.ogg"))
    val trnsprtSnd = Gdx.audio.newSound(Gdx.files.internal("transport.ogg"))

    init {
        this.sprite.setPosition(400f, 150f)
        this.walk.playMode = Animation.PlayMode.LOOP_PINGPONG
        this.currentFrame = this.walk.getKeyFrame(this.stateTime, true) as TextureRegion
        this.sprite.setSize(h, h)
    }

    // these are also in the Enemy class, probably just repeated it in both classes because I'm lazy
    private fun rectToPoly(r: Rectangle): Polygon = Polygon(floatArrayOf(r.x, r.y, r.x, r.y + r.height, r.x + r.width, r.y + r.height, r.x + r.width, r.y))

    private fun checkYCollision(p1: Vector2, p2: Vector2, r: Rectangle, vel: Vector2): Boolean = vel.y < 0 && Intersector.intersectSegmentPolygon(p1, p2, rectToPoly(r))

    // this was a cute little experiment, the player like - hops up the slope
    private fun bounceUpSlope(p1: Vector2, p2: Vector2, vel: Vector2, spr: Sprite, g: Boolean): Boolean {
        if (vel.x > 0 && vel.x < 0.5 || vel.x < 0 && vel.x > -0.5) return false
        if (g && p1.x < spr.x && p2.x > spr.x) {
            if (vel.x < 0 && p1.y > p2.y || vel.x > 0 && p1.y < p2.y) {
                vel.y += 20f
                return true
            }
        }
        return false
    }

    private fun findYOnSlope(p1: Vector2, p2: Vector2, r: Rectangle, vel: Vector2): Float {
        val upward = p1.y < p2.y
        if (p1.y == p2.y || upward && p1.x > r.x || !upward && p2.x < r.x + r.width) return r.y
        val opp = if (upward) p1.y - p2.y else p2.y - p1.y
        val adj = if (upward) p1.x - p2.x else p2.x - p1.x
        val myadj = if (upward) r.x - p1.x else r.x - p2.x
        return if (upward) p1.y + opp * myadj / adj else p2.y + opp * myadj / adj - r.height / 2
    }

    private fun handleInput() {
        if (this.goLeft) {
            if (this.grounded) this.state = PlayerStates.walking
            this.facesRight = false
            this.vel.x = -this.maxVelocity
        }
        else if (this.goRight) {
            if (this.grounded) this.state = PlayerStates.walking
            this.facesRight = true
            this.vel.x = this.maxVelocity
        }
        if (this.jumping && this.grounded) {
            this.state = PlayerStates.jumping
            this.grounded = false
            this.jumping = false
            this.vel.y += this.jumpVelocity
            jumpSnd.play()
        }
    }

    override fun act(delta: Float) {
        this.currentFrame = when (state) {
            PlayerStates.standing -> this.stand.getKeyFrame(this.stateTime)
            PlayerStates.walking -> this.walk.getKeyFrame(this.stateTime)
            PlayerStates.jumping -> this.jump.getKeyFrame(this.stateTime)
            PlayerStates.riding -> this.stand.getKeyFrame(this.stateTime)
            else -> this.stand.getKeyFrame(this.stateTime)
        }

        this.sprite.setRegion(this.currentFrame)

        val lastX = this.sprite.x

        var deltaTime = delta
        if (deltaTime == 0f) return

        if (deltaTime > 0.1f)
            deltaTime = 0.1f

        this.stateTime += deltaTime

        handleInput()

        if (Math.abs(this.vel.x) < 1) {
            this.vel.x = 0f
            if (this.grounded) this.state = PlayerStates.standing
        }

        this.vel.y -= grav

        // iterating over every single one of ramp polylines and checking for collisions, probably fine to do that
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

        this.myRect.set(this.sprite.x, this.sprite.y + this.vel.y, this.sprite.height, this.sprite.height)
        this.scaledRect.set(this.sprite.x * Constants.unitScale, this.sprite.y * Constants.unitScale + this.vel.y * Constants.unitScale, this.sprite.height * Constants.unitScale, this.sprite.height * Constants.unitScale)

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
                return@forEach
            }
        }

        this.myRect.set(this.sprite.x + this.vel.x, this.sprite.y, this.sprite.height, this.sprite.height)
        this.scaledRect.set(this.sprite.x * Constants.unitScale + this.vel.x * Constants.unitScale, this.sprite.y * Constants.unitScale, this.sprite.height * Constants.unitScale, this.sprite.height * Constants.unitScale)

        myTiles = GameObj.mm.getHorizNeighbourTiles(this.vel, this.scaledRect, GameObj.mm.tileLayer)

        myTiles.forEach {
            if (this.scaledRect.overlaps(it)) {
                this.myRect.x -= this.vel.x
                this.scaledRect.x -= this.vel.x * Constants.unitScale
                this.state = PlayerStates.standing
                this.vel.x = 0f
                return@forEach
            }
        }

        this.vel.scl(this.damping)

        this.sprite.setPosition(this.sprite.x + this.vel.x, this.sprite.y + this.vel.y)
        checkCollectibleHit()
        checkEnemiesHit()
        checkOutOfBounds()

        val xDiff = this.sprite.x - lastX

        // here's what makes the porallax background shift based on the player's position
        GameObj.gm.parallaxBackground.setSpeed(xDiff * 0.001f)
        GameObj.gm.parallaxBackground.setHeight(-sprite.y.toInt() / 2)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (this.facesRight) batch.draw(this.currentFrame, this.scaledRect.x, this.scaledRect.y, this.scaledRect.width, this.scaledRect.height)
        else batch.draw(this.currentFrame, this.scaledRect.x + this.scaledRect.width, this.scaledRect.y, -this.scaledRect.width, this.scaledRect.height)

        batch.end()

        with(GameObj.sr) {
            use(Line) {
                rect(myRect.x, myRect.y, myRect.width, myRect.height)
            }
        }
        batch.begin()
    }

    fun checkCollectibleHit() {
        this.myRect.set(sprite.x, sprite.y, sprite.width, sprite.height)
        this.scaledRect.set(sprite.x * Constants.unitScale, sprite.y * Constants.unitScale, sprite.width * Constants.unitScale, sprite.height * Constants.unitScale)

        // get the tiles from map utilities
        val tiles = GameObj.mm.getHorizNeighbourTiles(this.vel, this.scaledRect, GameObj.mm.collectablesLayer)

        // get the collectibles layer
        val layer = GameObj.mm.collectablesLayer

        //if bob collides with any tile while walking right, check the points and update score
        tiles.forEach {
            if (scaledRect.overlaps(it)) {
                val tilePoperties = layer.getCell(it.x.toInt(), it.y.toInt()).tile.properties
                val itemPoints = Integer.parseInt(tilePoperties.get("points").toString())
                GameObj.score += itemPoints
                layer.setCell(it.x.toInt(), it.y.toInt(), null)
                pickupSnd.play()
            }
        }
    }

    fun checkEnemiesHit() {
        this.myRect.set(this.sprite.x, this.sprite.y, this.sprite.width, this.sprite.height)

        // check whether bob collides with the enemies
        GameObj.stg.actors.forEach {
            if (it is Zombie)
            if (it.state == Enemy.State.ALIVE && it.myRect.overlaps(this.myRect)) {
                if (GameObj.lives > 0) {
                    GameObj.lives--
                    this.sprite.setPosition(400f, 150f)
                    hitSnd.play()
                }
            }
        }
    }

    fun checkOutOfBounds() {
        if (this.myRect.y < -1000) {
            GameObj.lives--
            this.sprite.setPosition(400f, 150f)
            trnsprtSnd.play()
        }
    }
}