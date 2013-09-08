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
    
    //The different ball sizes and their associated value
    public enum BallSize
    {
        Medium(16),
        Large(32),
        Small(8);

        private final int size;

        private BallSize(final int size) 
        {
            this.size = size;
        }

        public int getValue()
        {
            return this.size;
        }
    }
    
    
    //The different ball speeds and their associated value
    public enum BallSpeed
    {
        Medium(1.25),
        Fast(2.5),
        Fastest(3),
        Slowest(.25),
        Slow(.75);

        private final double speed;

        private BallSpeed(final double speed) 
        {
            this.speed = speed;
        }

        public double getValue()
        {
            return this.speed;
        }
    }
    
    public BallManager()
    {
        //create a new list of balls
        balls = new ArrayList<>();
    }
    
    /**
     * Create a new list of balls each at their own random location inside the container
     * @param resources Object that will give us a random ball image
     * @param container Area the balls will start inside
     * @param count The number of balls to add
     * @throws Exception 
     */
    public void reset(final Rectangle container, final int count, final int sizeIndex, final int speedIndex) throws Exception
    {
        //set the size for the balls
        final double size = BallSize.values()[sizeIndex].getValue();
        
        //set the speed for the balls
        final double speed = BallSpeed.values()[speedIndex].getValue();
        
        if (speed >= size)
            throw new Exception("The speed of the balls can't be greater than the size");
        
        //clear list
        balls.clear();
        
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
            
            //add ball to list
            balls.add(ball);
        }
    }
    
    public List<Ball> getBalls()
    {
        return this.balls;
    }
    
    public int getCount()
    {
        return getBalls().size();
    }
    
    public boolean hasCheatEnabled()
    {
        return this.cheatEnabled;
    }
    
    public void setCheatEnabled(final boolean cheatEnabled)
    {
        this.cheatEnabled = cheatEnabled;
    }
    
    public void update(final Resources resources, final Board board, final Player player)
    {
        //no balls so nothing to update or the user is cheating
        if (balls == null || hasCheatEnabled())
            return;
        
        for (Ball ball : balls)
        {
            //get a random game ball image
            if (ball.getImage() == null)
                ball.setImage(resources.getGameBall());
            
            //if the player is trying to capture and the ball hit the player capture boundary
            if (player.hasCapture() && player.getCaptureBoundary().intersects(ball.getRectangle()))
            {
                //make sure we aren't cheating
                if (!hasCheatEnabled())
                {
                    //lose 1 life
                    player.loseLife();
                    
                    //play hit sound effect
                    resources.getGameAudio(Resources.GameAudio.Hit).play();
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