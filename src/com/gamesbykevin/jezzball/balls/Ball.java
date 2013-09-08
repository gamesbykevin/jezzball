package com.gamesbykevin.jezzball.balls;

import com.gamesbykevin.framework.base.Sprite;

import java.awt.Graphics;

/**
 * The class that represents the bouncing ball
 * 
 * Possible additions for the future are the following for each ball
 * 1. Location
 * 2. Size (width/height)
 * 3. Velocity
 * 4. Image
 * 
 * @author GOD
 */
public class Ball extends Sprite
{
    @Override
    public void update()
    {
        super.update();
    }
    
    public void render(Graphics graphics)
    {
        //don't draw the ball if the image doesn't exist
        if (getImage() == null)
            return;
        
        super.draw(graphics);
    }
}