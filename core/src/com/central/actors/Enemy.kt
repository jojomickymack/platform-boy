package com.central.actors

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Intersector
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.central.GameObj
import ktx.graphics.use

abstract class Enemy : Actor() {

    internal var sprite = Sprite() // enemy sprite
    internal var vel = Vector2() // velocity of the enemy
    internal var myRect = Rectangle() // rectangle object to detect collisions
    internal var scaledRect = Rectangle()
    internal var grounded = false

    enum class State {
        ALIVE,
        DEAD
    } // represents whether the enemy is active or not

    var state = State.ALIVE

    private fun rectToPoly(r: Rectangle): Polygon = Polygon(floatArrayOf(r.x, r.y, r.x, r.y + r.height, r.x + r.width, r.y + r.height, r.x + r.width, r.y))

    protected fun checkYCollision(p1: Vector2, p2: Vector2, r: Rectangle, vel: Vector2): Boolean = vel.y < 0 && Intersector.intersectSegmentPolygon(p1, p2, rectToPoly(r))

    protected fun findYOnSlope(p1: Vector2, p2: Vector2, r: Rectangle, vel: Vector2): Float {
        val upward = p1.y < p2.y
        if (p1.y == p2.y || upward && p1.x > r.x || !upward && p2.x < r.x + r.width) return r.y
        val opp = if (upward) p1.y - p2.y else p2.y - p1.y
        val adj = if (upward) p1.x - p2.x else p2.x - p1.x
        val myadj = if (upward) r.x - p1.x else r.x - p2.x
        return if (upward) p1.y + opp * myadj / adj else p2.y + opp * myadj / adj - r.height / 2
    }

    fun drawSquare() {
        with(GameObj.sr) {
            use(ShapeType.Line) {
                rect(sprite.x, sprite.y, sprite.width, sprite.height)
            }
        }
    }
}
