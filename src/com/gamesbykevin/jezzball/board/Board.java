package com.gamesbykevin.jezzball.board;

import com.gamesbykevin.framework.base.Sprite;

import com.gamesbykevin.jezzball.balls.Ball;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * The Board that will contain all of the boundaries
 * @author GOD
 */
public final class Board extends Sprite
{
    //all of the boundaries on the board
    private final List<Rectangle> boundaries;
    
    //the portion we need to be complete before moving to the next level
    private double goal;
    
    //the progress towards the goal
    private double progress;
    
    /**
     * Create a new board with the intial size to be the parameter screen
     * @param screen The size of the original container
     * @param goal Percentage of the board we need in order to complete
     */
    public Board(final Rectangle screen, final double goal)
    {
        //set our goal so we know when the board has been finished
        this.goal = goal;
        
        //create the list of boundaries
        boundaries = new ArrayList<>();
        
        //add the entire board window as the inital boundary
        boundaries.add(screen);
        
        //set location and dimensions for the background image
        super.setLocation(screen.x, screen.y);
        super.setDimensions(screen.width, screen.height);
    }
    
    /**
     * Count the number of remaining pixels and see if the 
     * fraction not covered is greater than or equal to the 
     * goal set.
     * 
     * @return Return true if the percent area shown is at least the goal value set
     */
    public boolean hasGoal()
    {
        final double totalArea = (super.getWidth() * super.getHeight());
        
        double area = 0;
        
        for (Rectangle boundary : getBoundaries())
        {
            area += (boundary.width * boundary.height);
        }
        
        //set the current progress
        progress = ((totalArea - area) / totalArea);
        
        return (progress >= goal);
    }
    
    public List<Rectangle> getBoundaries()
    {
        return this.boundaries;
    }
    
    /**
     * Separate the current boundary Located at start position.
     * Once the boundaries are separated they will be added to 
     * the boundaries List IF they contain balls.
     * 
     * @param start The start position
     * @param vertical Are you capturing vertically
     * @param balls The List of balls
     */
    public void setBoundaries(final Point start, final boolean vertical, final List<Ball> balls)
    {
        for (int i=0; i < boundaries.size(); i++)
        {
            //we found the boundary we want to split
            if (boundaries.get(i).contains(start))
            {
                Rectangle tmp = boundaries.get(i);
                Rectangle tmpSide;
                
                //split left and right
                if (vertical)
                {
                    //left
                    tmpSide = new Rectangle();
                    tmpSide.x = tmp.x;
                    tmpSide.y = tmp.y;
                    tmpSide.width = start.x - tmp.x;
                    tmpSide.height = tmp.height;
                    
                    //check if any balls are inside this boundary
                    for (Ball ball : balls)
                    {
                        //if boundary is inside we will keep it
                        if (tmpSide.contains(ball.getRectangle()))
                        {
                            boundaries.add(tmpSide);
                            break;
                        }
                    }
                    
                    //right
                    tmpSide = new Rectangle();
                    tmpSide.x = start.x;
                    tmpSide.y = tmp.y;
                    tmpSide.width = tmp.x + tmp.width - start.x;
                    tmpSide.height = tmp.height;
                    
                    //check if any balls are inside this boundary
                    for (Ball ball : balls)
                    {
                        //if boundary is inside we will keep it
                        if (tmpSide.contains(ball.getRectangle()))
                        {
                            boundaries.add(tmpSide);
                            break;
                        }
                    }
                }
                else
                {
                    //top
                    tmpSide = new Rectangle();
                    tmpSide.x = tmp.x;
                    tmpSide.y = tmp.y;
                    tmpSide.width = tmp.width;
                    tmpSide.height = start.y - tmp.y;
                    
                    //check if any balls are inside this boundary
                    for (Ball ball : balls)
                    {
                        //if boundary is inside we will keep it
                        if (tmpSide.contains(ball.getRectangle()))
                        {
                            boundaries.add(tmpSide);
                            break;
                        }
                    }
                    
                    //bottom
                    tmpSide = new Rectangle();
                    tmpSide.x = tmp.x;
                    tmpSide.y = start.y;
                    tmpSide.width = tmp.width;
                    tmpSide.height = tmp.y + tmp.height - start.y;
                    
                    //check if any balls are inside this boundary
                    for (Ball ball : balls)
                    {
                        //if boundary is inside we will keep it
                        if (tmpSide.contains(ball.getRectangle()))
                        {
                            boundaries.add(tmpSide);
                            break;
                        }
                    }
                }
                
                //remove the boundary from the List
                boundaries.remove(i);
                
                break;
            }
        }
    }
    
    @Override
    public void update()
    {
        
    }

    /**
     * Draw the background image and then the boundaries on top
     * @param graphics 
     */
    public void render(final Graphics graphics)
    {
        final double x = super.getX();
        final double y = super.getY();
        final double w = super.getWidth();
        final double h = super.getHeight();
        
        int height = super.getImage().getHeight(null);
        
        if (height < super.getHeight())
        {
            super.setY((getHeight() / 2) - (super.getImage().getHeight(null) / 2));
            super.setWidth(getImage().getWidth(null));
            super.setHeight(getImage().getHeight(null));
        }
        
        //draw background image
        super.draw(graphics);
        
        //reset location and dimensions
        super.setLocation(x, y);
        super.setDimensions(w, h);
        
        //fill each boundary
        for (Rectangle boundary : boundaries)
        {
            //cover up boundaries in black
            graphics.setColor(Color.BLACK);
            graphics.fillRect(boundary.x, boundary.y, boundary.width, boundary.height);
            
            //outline in white
            graphics.setColor(Color.WHITE);
            graphics.drawRect(boundary.x, boundary.y, boundary.width, boundary.height);
        }
    }
}