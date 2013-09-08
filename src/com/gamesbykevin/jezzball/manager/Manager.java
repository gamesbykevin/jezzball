package com.gamesbykevin.jezzball.manager;

import com.gamesbykevin.jezzball.balls.BallManager;
import com.gamesbykevin.jezzball.board.Board;
import com.gamesbykevin.jezzball.main.Engine;
import com.gamesbykevin.jezzball.main.Resources.PlayerImage;
import com.gamesbykevin.jezzball.menu.CustomMenu;
import com.gamesbykevin.jezzball.menu.CustomMenu.LayerKey;
import com.gamesbykevin.jezzball.menu.CustomMenu.OptionKey;
import com.gamesbykevin.jezzball.player.Player;

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
    
    //the current level
    private int level = 1;
    
    public Manager(Engine engine) throws Exception
    {
        //the container the game will be inside
        this.container = new Rectangle(0, 0, 500, 400);
        
        //for now the board will be the size of the screen
        board = new Board(container, 0.75);
        
        //set background Image
        board.setImage(engine.getResources().getGameBackground());
        
        //create a new list of balls
        ballManager = new BallManager(engine.getResources(), container, 3);
        
        //enable cheat if turned on
        ballManager.setCheatEnabled(engine.getMenu().getOptionSelectionIndex(LayerKey.Options, OptionKey.Cheat) == CustomMenu.CHEAT_ENABLED);
        
        //new instance of player
        player = new Player(1, engine.getResources().getPlayerImage(PlayerImage.Horizontal), engine.getResources().getPlayerImage(PlayerImage.Vertical));
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
    
    public void update(Engine engine)
    {
        //update the locations of all the balls and keep the boundaries in mind
        ballManager.update(board, player);
        
        //update the mouse location etc..
        player.update(engine.getMouse(), board, ballManager.getBalls());
        
        board.hasGoal();
    }
    
    public void render(Graphics graphics)
    {
        //first we draw the board with the boundaries
        board.render(graphics);
        
        //then we draw all of the balls
        ballManager.render(graphics);
        
        //then we draw the player
        player.render(graphics);
    }
}