package com.central.managers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.central.GameObj

class TextManager {
    var font = BitmapFont(Gdx.files.internal("fonts/dos.fnt"), Gdx.files.internal("fonts/dos.png"), false)
    // viewport width and height
    var width = 0f
    var height = 0f
    // var loadingFont: BitmapFont // we draw the text to the loading screen using this variable

    fun initialize(width: Float, height: Float) {
        this.width = width
        this.height = height
        //set the font color to red
        font.color = Color.RED
        //scale the font size according to screen width
        font.data.setScale(width / 1000f)
    }

    fun displayMessage(batch: Batch) {
        var layout = GlyphLayout()
        layout.setText(this.font, "Score: " + GameObj.score)
        val fontWidth = layout.width // contains the width of the current set text

        //top the score display at top right corner
        font.draw(batch, "Score: " + GameObj.score, width - fontWidth - width / 15f, height * 0.98f)
        // show the number of lives at top left corner
        font.draw(batch, "Lives: " + GameObj.lives, width * 0.01f, height * 0.98f)
    }
}
