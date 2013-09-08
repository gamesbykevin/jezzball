package com.gamesbykevin.jezzball.balls;

import com.gamesbykevin.jezzball.board.Board;
import com.gamesbykevin.jezzball.main.Resources;
import com.gamesbykevin.jezzball.player.Player;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

/**
 * Class containing all of the balls
 * @author GOD
 */
public class BallManager 
{
    //all of the balls on the screen
    private List<Ball> balls;
    
    //cheating will be freezing the movement of the balls
    private boolean cheatEnabled = false;
    
    //the different sizes allowed for the balls
    private static final double[] BALL_SIZES = {8, 16, 32, 64, 128, 256};
    
    //the different speeds allowed for the balls
    private static final double[] BALL_SPEED = {0, 1, 2, 3, 5, 10};
    
    /**
     * Create a new collection of balls within the container and of the specified dimensions
     * @param count The number of balls we want to add
     * @param container The container where the balls will lie within
     * @param minSize The minimum width/height of the balls
     * @param maxSize The maximum width/height of the balls
     * @param image The image of the ball
     */
    public BallManager(final Resources resources, final Rectangle container, final int count) throws Exception
    {
        //create a new list of balls
        balls = new ArrayList<>();
        
        //set the size for the balls
        final double size = BALL_SIZES[2];
        
        //set the speed for the balls
        final double speed = BALL_SPEED[2];
        
        if (speed >= size)
            throw new Exception("The speed of the balls can't be greater than the size");
        
        for (int i=0; i < count; i++)
        {
            //create new ball
            Ball ball = new Ball();
            
            //pick random x, y location for the ball
            final double x = container.x + (Math.random() * (container.width  - size));
            final double y = container.y + (Math.random() * (container.height - size));
            
            //set the ball coordinates
            ball.setX(x);
            ball.setY(y);
            
            //set a random direction
            final double velocityX = (Math.random() > .5) ? -speed : speed;
            final double velocityY = (Math.random() > .5) ? -speed : speed;
            
            //set the randomly picked velocity
            ball.setVelocity(velocityX, velocityY);
            
            //ball will have same width and height
            ball.setDimensions(size, size);
            
            //get a random game ball image and set it
            ball.setImage(resources.getGameBall());
            
            //add ball to list
            balls.add(ball);
        }
    }
    
    public List<Ball> getBalls()
    {
        return this.balls;
    }
    
    public boolean hasCheatEnabled()
    {
        return this.cheatEnabled;
    }
    
    public void setCheatEnabled(final boolean cheatEnabled)
    {
        this.cheatEnabled = cheatEnabled;
    }
    
    public void update(final Board board, final Player player)
    {
        //no balls so nothing to update or the user is cheating
        if (balls == null || hasCheatEnabled())
            return;
        
        for (Ball ball : balls)
        {
            //if the player is trying to capture and the ball hit the player capture boundary
            if (player.hasCapture() && player.getCaptureBoundary().intersects(ball.getRectangle()))
            {
                //make sure we aren't cheating
                if (!hasCheatEnabled())
                {
                    //lose 1 life
                    player.loseLife();
                }
                
                //no longer capturing
                player.switchCapture();
            }
            
            //store location first before we call update
            final double x = ball.getX();
            final double y = ball.getY();
            
            //find the container the ball currently is in
            Rectangle tmp = null;
            
            for (Rectangle boundary : board.getBoundaries())
            {
                //if the ball is inside we found the boundary
                if (boundary.contains(ball.getRectangle()))
                {
                    tmp = boundary;
                    break;
                }
            }
            
            //update location based on velocity
            ball.update();
            
            //if the ball is no longer inside the boundary reverse the velocity
            if (tmp != null && !tmp.contains(ball.getRectangle()))
            {
                //if the x location is out of bounds switch velocity x
                if (ball.getX() + ball.getWidth() >= tmp.x + tmp.width || ball.getX() <= tmp.x)
                    ball.setVelocityX(-ball.getVelocityX());
                
                //if the x location is out of bounds switch velocity x
                if (ball.getY() + ball.getHeight() >= tmp.y + tmp.height || ball.getY() <= tmp.y)
                    ball.setVelocityY(-ball.getVelocityY());
                
                //set the ball back to the previous position
                ball.setLocation(x, y);
            }
        }
    }
    
    public void render(Graphics graphics)
    {
        //no balls so nothing to update
        if (balls == null)
            return;
        
        for (Ball ball : balls)
        {
            ball.render(graphics);
        }
    }
}