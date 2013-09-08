package com.gamesbykevin.jezzball.manager;

import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.jezzball.balls.BallManager;
import com.gamesbykevin.jezzball.board.Board;
import com.gamesbykevin.jezzball.main.Engine;
import com.gamesbykevin.jezzball.main.Resources;
import com.gamesbykevin.jezzball.main.Resources.PlayerImage;
import com.gamesbykevin.jezzball.menu.CustomMenu;
import com.gamesbykevin.jezzball.menu.CustomMenu.LayerKey;
import com.gamesbykevin.jezzball.menu.CustomMenu.OptionKey;
import com.gamesbykevin.jezzball.player.Player;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public class Manager 
{
    //object that manages all of the balls
    private BallManager ballManager;
    
    //player containing lives etx...
    private Player player;
    
    //game board that has all of the boundaries
    private Board board;
    
    //the game itself will be played inside here
    private Rectangle container;
    
    //the visual display how close we are till level complete
    private Rectangle progressBar;
    
    //the current level
    private int level = 0;
    
    public static final int[] LEVEL_START = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    
    //how many lives do we get per level
    private final int livesIndex;
    
    //rate of speed
    private final int speedIndex;
    
    //ball size
    private final int sizeIndex;
    
    //the speed at which we will attempt to capture the boundary
    private final int captureSpeedIndex;
    
    //the list of all the timers we are to use
    private final TimerCollection timers;
    
    /**
     * Keys identifying each timer
     */
    private enum Key
    {
        Countdown, Free, NextLevel
    }
    
    //amont of time to add to timer per ball
    private static final long TIME_PER_BALL = TimerCollection.toNanoSeconds(45000L);
    
    //is the game timed or not
    public enum Mode
    {
        Free, Timed
    }
    
    //this will determine if the game has ended
    private boolean gameover = false;
    
    public Manager(Engine engine) throws Exception
    {
        //create new timer list
        this.timers = new TimerCollection(engine.getMain().getTimeDeductionPerUpdate());
        
        //get the size and speed
        this.sizeIndex         = engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.BallSize);
        this.speedIndex        = engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.BallSpeed);
        this.captureSpeedIndex = engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.CaptureSpeed);
        this.livesIndex        = engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives);
        
        final int levelStartIndex = engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.LevelStart);
        
        //set the start level accordingly, we subtract 1 because nextLevel() will increase the level
        level = LEVEL_START[levelStartIndex] - 1;
        
        switch (Mode.values()[engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Mode)])
        {
            case Free:
                this.timers.add(Key.Free);
                break;
                
            case Timed:
                this.timers.add(Key.Countdown, 0);
                break;
        }
        
        //once level is complete 5 seconds until next level
        this.timers.add(Key.NextLevel, TimerCollection.toNanoSeconds(5000L));
        
        //the container the game will be inside
        this.container = new Rectangle(0, 100, 500, 400);
        
        //create a new list of balls
        ballManager = new BallManager();
        
        //the board where game play will occur
        board = new Board();
        
        //enable cheat if turned on
        ballManager.setCheatEnabled(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Cheat) == CustomMenu.CHEAT_ENABLED);
        
        //new instance of player
        player = new Player(engine.getResources().getPlayerImage(PlayerImage.Horizontal), engine.getResources().getPlayerImage(PlayerImage.Vertical));
        
        //setup variables for the current level
        nextLevel(engine.getResources());
    }
    
    /**
     * Setup the appropriate variables for the next level
     * @throws Exception 
     */
    private void nextLevel(final Resources resources) throws Exception
    {
        level++;
        
        //reset all timers
        timers.reset();
        
        //if the countdown timer exists the limited time will be TIME_PER_BALL * current level
        if (timers.getTimer(Key.Countdown) != null)
            timers.setRemaining(Key.Countdown, TIME_PER_BALL * level);
        
        //reset the number of lives
        player.setLives(this.livesIndex);
        
        //set the capture speed
        player.setSpeed(this.captureSpeedIndex);
        
        //make sure player isn't still capturing
        if (player.hasCapture())
            player.switchCapture();
        
        //reset the list of balls to include the correct total inside the given container
        ballManager.reset(container, level, sizeIndex, speedIndex);
        
        //reset board, must reset ballManager first
        board.reset(container, ballManager.getBalls());
        
        //set random background Image
        board.setImage(resources.getGameBackground());
    }
    
    public BallManager getBallManager()
    {
        return this.ballManager;
    }
    
    /**
     * Free up resources
     */
    public void dispose()
    {
        
    }
    
    private void checkGameOver()
    {
        //if the game isn't over check if it should be
        if (!gameover)
        {
            if (!player.hasLives())
                gameover = true;
            
            if (timers.getTimer(Key.Countdown) != null && timers.getTimer(Key.Countdown).hasTimePassed())
                gameover = true;
        }
    }
    
    public void update(Engine engine) throws Exception
    {
        if (!gameover)
        {
            //check if the game has ended
            checkGameOver();
            
            //if game is now over play sound effect
            if (gameover)
                engine.getResources().getGameAudio(Resources.GameAudio.GameOver).play();
        }
        
        //if the game is over no updating is needed
        if (gameover)
            return;
        
        //if we haven't reached the goal yet update the appropriate elements
        if (!board.hasGoal())
        {
            //make sure timer exists before updating/checking
            if (timers.getTimer(Key.Countdown) != null)
            {
                timers.update(Key.Countdown);

                //this will prevent the countdown from going negative
                if (timers.getTimer(Key.Countdown).hasTimePassed())
                    timers.getTimer(Key.Countdown).setRemaining(0);
            }

            //make sure timer exists before updating/checking
            if (timers.getTimer(Key.Free) != null)
                timers.update(Key.Free);
            
            //update the locations of all the balls and keep the boundaries in mind
            ballManager.update(engine.getResources(), board, player);

            //update the mouse location etc..
            player.update(engine.getMouse(), board, ballManager.getBalls(), engine.getResources());
            
            //if we have now reached the goal play win sound
            if (board.hasGoal())
                engine.getResources().getGameAudio(Resources.GameAudio.Win).play();
        }
        else
        {
            timers.update(Key.NextLevel);
            
            //this will prevent the countdown from going negative
            if (timers.getTimer(Key.NextLevel).hasTimePassed())
            {
                timers.getTimer(Key.NextLevel).setRemaining(0);
                this.nextLevel(engine.getResources());
            }
        }
    }
    
    public void render(Graphics graphics)
    {
        //first we draw the board with the boundaries
        board.render(graphics);
        
        //draw these elements if the goal has not been reached
        if (!board.hasGoal())
        {
            //then we draw all of the balls
            ballManager.render(graphics);

            //then we draw the player
            player.render(graphics);
        }
        
        //draw game information
        this.renderInfo(graphics);
    }
    
    /**
     * Draw the game information here
     * The following will be drawn
     * 
     * 1. Progress towards goal
     * 2. Level #
     * 3. # of balls
     * 4. Lives remaining
     * 5. Cheat enabled
     * 6. Time remaining/passed
     * 7. Countdown till next level
     * @param graphics 
     */
    private void renderInfo(Graphics graphics)
    {
        if (this.progressBar == null)
            this.progressBar = new Rectangle(10, 10, 200, graphics.getFontMetrics().getHeight());
        
        int x, y;
        String desc;
        
        //draw progress
        graphics.setColor(Color.red);
        graphics.fillRect(progressBar.x, progressBar.y, (int)(progressBar.width * board.getProgress()), progressBar.height);
        
        //draw goal
        x = progressBar.x + (int)(progressBar.width * board.getGoal());
        graphics.setColor(Color.GREEN);
        graphics.drawLine(x, progressBar.y, x, progressBar.y + progressBar.height);
        
        //draw progress bar
        graphics.setColor(Color.BLUE);
        graphics.drawRect(progressBar.x, progressBar.y, progressBar.width, progressBar.height);
        
        graphics.setColor(Color.WHITE);
        graphics.drawString("Goal", progressBar.x + progressBar.width + 5, progressBar.y + progressBar.height);
        
        //draw Level #
        x = progressBar.x;
        y = progressBar.y + (progressBar.height * 2) + 5;
        desc = "Level = " + level + ", ";
        graphics.setColor(Color.WHITE);
        graphics.drawString(desc, x, y);
        
        //draw ball count
        x += graphics.getFontMetrics().stringWidth(desc);
        desc = "Balls = " + ballManager.getCount()+ ", ";
        graphics.drawString(desc, x, y);
        
        //draw lives remaining
        x += graphics.getFontMetrics().stringWidth(desc);
        desc = "Lives = " + player.getLives();
        graphics.drawString(desc, x, y);
        
        //display we are cheating if enabled
        if (ballManager.hasCheatEnabled())
        {
            x += graphics.getFontMetrics().stringWidth(desc);
            desc = ", Cheat Enabled";
            graphics.setColor(Color.GREEN);
            graphics.drawString(desc, x, y);
        }
        
        //draw timer
        x = progressBar.x;
        y = progressBar.y + (progressBar.height * 3) + 5;
        desc = "Time: ";
        
        if (timers.getTimer(Key.Countdown) != null)
        {
            desc += timers.getTimer(Key.Countdown).getDescRemaining(TimerCollection.FORMAT_6);
        }
        else
        {
            desc += timers.getTimer(Key.Free).getDescPassed(TimerCollection.FORMAT_6);
        }
        
        graphics.setColor(Color.WHITE);
        graphics.drawString(desc, x, y);
        
        if (gameover)
        {
            //draw gameover message
            y = progressBar.y + (progressBar.height * 4) + 5;
            desc = "Game Over!! Press \"Esc\" to access menu";
            graphics.setColor(Color.RED);
            graphics.drawString(desc, x, y);
        }
        
        //we have reached our goal so display the countdown till next level
        if (board.hasGoal())
        {
            y = progressBar.y + (progressBar.height * 4) + 5;
            desc = "Next level begins in " + timers.getTimer(Key.NextLevel).getDescRemaining(TimerCollection.FORMAT_7);
            
            graphics.setColor(Color.WHITE);
            graphics.drawString(desc, x, y);
        }
    }
}