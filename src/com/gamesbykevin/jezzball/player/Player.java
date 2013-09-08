package com.gamesbykevin.jezzball.player;

import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.input.Mouse;

import com.gamesbykevin.jezzball.balls.Ball;
import com.gamesbykevin.jezzball.board.Board;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

/**
 * This class manages the progress when splitting containers as well as the mouse position/display
 * @author GOD
 */
public class Player extends Sprite
{
    //number of lives the player has left
    private int lives;
    
    //horizontal and vertical images
    private final Image horizontal, vertical;
    
    //dimension of capture image
    private static final int DIMENSION = 64;
    
    //are we currently capturing
    private boolean capture = false;
    
    //the speed to capture
    private static final int[] CAPTURE_SPEED = {1, 2, 5};
    
    //the location where we started capturing
    private Point start;
    
    //when capturing what is the dimension
    private static final int CAPTURE_DIMENSION = 8;
    
    //the 2 ends of the capture
    private Point side1, side2;
    
    //the boundary that is the border we are capturing
    private Rectangle captureBoundary;
    
    public Player(final int lives, final Image horizontal, final Image vertical)
    {
        //the number of lives left
        this.lives = lives;
        
        //set the appropriate images
        this.horizontal = horizontal;
        this.vertical = vertical;
        
        //set the appropriate dimensions
        setDimensions(DIMENSION, DIMENSION);
        
        //set the appropriate image
        switchImage();
    }
    
    /**
     * Take 1 life away
     */
    public void loseLife()
    {
        this.lives--;
    }
    
    /**
     * Do we have at least 1 life
     * @return boolean
     */
    public boolean hasLives()
    {
        return (getLives() > 0);
    }
    
    /**
     * Get the number of lives
     * @return int
     */
    public int getLives()
    {
        return this.lives;
    }
    
    /**
     * Are we capturing a boundary
     * @return 
     */
    public boolean hasCapture()
    {
        return this.capture;
    }
    
    /**
     * If capture is enabled then turn off and vice versa
     */
    public void switchCapture()
    {
        this.capture = !this.capture;
    }
    
    /**
     * Which way are we facing
     * @return boolean
     */
    private boolean hasHorizontal()
    {
        if (getImage() == null || getImage() == horizontal)
            return true;
        
        return false;
    }
    
    private void switchImage()
    {
        if (hasHorizontal())
        {
            super.setImage(vertical);
        }
        else
        {
            super.setImage(horizontal);
        }
    }
    
    public void update(final Mouse mouse, final Board board, final List<Ball> balls)
    {
        //if we are capturing
        if (hasCapture())
        {
            //get the boundary so we know when we have reached the end
            Rectangle tmp = null;
            
            //there will always be a boundary returned
            for (Rectangle boundary : board.getBoundaries())
            {
                if (boundary.contains(start))
                {
                    tmp = boundary;
                    break;
                }
            }
            
            if (super.hasVelocityX())
            {
                //left side
                this.side1.x += -super.getVelocityX();
                
                //right side
                this.side2.x += super.getVelocityX();
                
                //stop sides from moving if they are out of bounds
                if (!tmp.contains(this.side1))
                    this.side1.x = tmp.x;
                if (!tmp.contains(this.side2))
                    this.side2.x = tmp.x + tmp.width;
                
                //both sides have reached the end
                if (side1.x == tmp.x && side2.x == tmp.x + tmp.width)
                {
                    switchCapture();
                    board.setBoundaries(start, false, balls);
                }
            }
            
            if (super.hasVelocityY())
            {
                //north side
                this.side1.y += -super.getVelocityY();
                
                //south side
                this.side2.y += super.getVelocityY();
                
                //stop sides from moving if they are out of bounds
                if (!tmp.contains(this.side1))
                    this.side1.y = tmp.y;
                if (!tmp.contains(this.side2))
                    this.side2.y = tmp.y + tmp.height;
                
                //both sides have reached the end
                if (side1.y == tmp.y && side2.y == tmp.y + tmp.height)
                {
                    switchCapture();
                    board.setBoundaries(start, true, balls);
                }
            }
        }
        
        if (mouse.isMousePressed() && mouse.hitLeftButton())
        {
            //make sure we aren't capturing already
            if (!hasCapture())
            {
                //turn capture on
                switchCapture();
                
                //offset coordinates
                super.setX(super.getX() - (super.getWidth()  / 2));
                super.setY(super.getY() - (super.getHeight() / 2));
        
                //set the start location of the capture
                this.start = super.getCenter();
                
                //set coordinates back
                super.setX(super.getX() + (super.getWidth()  / 2));
                super.setY(super.getY() + (super.getHeight() / 2));
                
                //the boundary that contains the start position
                Rectangle tmp = null;
                
                //if the start position is not found in a boundary do not continue
                for (Rectangle boundary : board.getBoundaries())
                {
                    //have we found a boundary that contains the start position
                    if (boundary.contains(start))
                    {
                        tmp = boundary;
                        break;
                    }
                }
                
                //the location was not found inside a boundary
                if (tmp == null)
                {
                    //turn capture off
                    switchCapture();
                    
                    //do not continue since we didn't find a boundary
                    return;
                }
                
                
                //set the locations of the side
                this.side1 = new Point(start);
                this.side2 = new Point(start);
                
                //reset velocity
                resetVelocity();
                
                if (hasHorizontal())
                {
                    setVelocityX(CAPTURE_SPEED[1]);
                }
                else
                {
                    setVelocityY(CAPTURE_SPEED[1]);
                }
            }
        }
        
        //switch player image
        if (mouse.isMousePressed() && mouse.hitRightButton())
            switchImage();
        
        //update location
        if (mouse.hasMouseMoved() || mouse.isMouseDragged())
            super.setLocation(mouse.getLocation());
        
        //reset mouse events and mouse buttons hit
        mouse.reset();
    }
    
    public Rectangle getCaptureBoundary()
    {
        //create new instance if not instantiated yet
        if (captureBoundary == null)
            captureBoundary = new Rectangle();
        
        //capturing horizontally
        if (this.side1.x != start.x)
        {
            captureBoundary.x      = side1.x;
            captureBoundary.y      = start.y - (CAPTURE_DIMENSION / 2);
            captureBoundary.width  = side2.x - side1.x;
            captureBoundary.height = CAPTURE_DIMENSION;
        }
        else
        {
            //capturing vertically
            captureBoundary.x      = start.x - (CAPTURE_DIMENSION / 2);
            captureBoundary.y      = side1.y;
            captureBoundary.width  = CAPTURE_DIMENSION;
            captureBoundary.height = side2.y - side1.y;
        }
        
        return captureBoundary;
    }
    
    public void render(Graphics graphics)
    {
        //if capturing draw progress
        if (hasCapture())
        {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(getCaptureBoundary().x, getCaptureBoundary().y, getCaptureBoundary().width, getCaptureBoundary().height);
            graphics.setColor(Color.BLACK);
            graphics.drawRect(getCaptureBoundary().x, getCaptureBoundary().y, getCaptureBoundary().width, getCaptureBoundary().height);
        }
        
        //offset coordinates
        super.setX(super.getX() - (super.getWidth()  / 2));
        super.setY(super.getY() - (super.getHeight() / 2));
        
        //draw image
        super.draw(graphics);
        
        //set coordinates back
        super.setX(super.getX() + (super.getWidth()  / 2));
        super.setY(super.getY() + (super.getHeight() / 2));
    }
}