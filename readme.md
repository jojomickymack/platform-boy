# Platformer Boy Example With Angled Terrain

![platform_boy.gif](.github/platform_boy.gif?raw=true)

This is heavily influenced by the platformer example project in 'LibGDX Cross Platform Development Blueprints' by Indraneel 
Potnis.

[https://www.packtpub.com/game-development/libgdx-cross-platform-development-blueprints](https://www.packtpub.com/game-development/libgdx-cross-platform-development-blueprints)

It contains a single level tiled map game with collectables, enemies, points, and hitpoints. It has no ending and is a single 
scene. What I was trying to achieve was terrain that can angle upwards and downwards, and this serves as an example of how 
that can be done using tiled map polylines.

Before re-organizing this, I put the map in the MapManager and I put the texture for the Player's spritesheet in the Player class - this is 
not how you want to do things since loaded assets need to be disposed of when the game exits. While using AssetManager would've been a step in 
the right direction, there's another problem with circular dependencies (re-ordering the members of MyGameObj will lock the game up because 
things one object might need are initilized elsewhere).

Interesting that you can run into all these structural issues with even such a simple game - what I would do next time is avoid all of these 
'Managers' and have more logic in the Screen class itself. Notice that the GameManager doesn't really have a reason to exist, it should all be 
in the GameScreen class, which is pretty much empty.
