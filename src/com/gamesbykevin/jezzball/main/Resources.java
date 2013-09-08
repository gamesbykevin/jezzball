package com.gamesbykevin.jezzball.main;

import com.gamesbykevin.framework.resources.*;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.text.MessageFormat;
import java.util.LinkedHashMap;

/**
 * This class will load all resources in the collection and provide a way to access them
 * @author GOD
 */
public class Resources 
{   
    //this will contain all resources
    private LinkedHashMap<Object, Manager> everyResource;
    
    //collections of resources
    private enum Type
    {
        MenuImage, MenuAudio, GameFont, GameBalls, PlayerImage, GameBackgrounds
    }
    
    //root directory of all resources
    public static final String RESOURCE_DIR = "resources/"; 
    
    public enum MenuAudio
    {
        MenuChange
    }
    
    public enum PlayerImage
    {
        Horizontal, Vertical
    }
    
    public enum MenuImage
    {
        TitleScreen, Credits, AppletFocus, TitleBackground, Mouse, MouseDrag, 
        Controls1, Controls2, 
        Instructions1, Instructions2, Instructions3
    }
    
    public enum GameFont
    {
        Dialog
    }
    
    public enum GameBackgrounds
    {
        B0, B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, 
        B11, B12, B13, B14, B15, B16, B17, B18, B19, B20, 
        B21, B22, B23, B24, B25, B26, B27, B28, B29, B30,
        B31, B32, B33, B34, B35
    }
    
    public enum GameBalls
    {
        Ball0, Ball1, Ball2, Ball3, Ball4, Ball5, Ball6, Ball7, Ball8, Ball9, 
        Ball10, Ball11, Ball12, Ball13, Ball14, Ball15, Ball16, Ball17, Ball18, Ball19, 
        Ball20, Ball21, Ball22, Ball23
    }
    
    //indicates wether or not we are still loading resources
    private boolean loading = true;
    
    public Resources() throws Exception
    {
        everyResource = new LinkedHashMap<>();
        
        //load all player images
        add(Type.PlayerImage, (Object[])PlayerImage.values(), RESOURCE_DIR + "images/game/player/{0}.png", "Loading Player Images", Manager.Type.Image);
        
        //load all game backgrounds
        add(Type.GameBackgrounds, (Object[])GameBackgrounds.values(), RESOURCE_DIR + "images/game/backgrounds/{0}.jpg", "Loading Game Backgrounds", Manager.Type.Image);
        
        //load all game balls
        add(Type.GameBalls, (Object[])GameBalls.values(), RESOURCE_DIR + "images/game/balls/{0}.png", "Loading Game Balls", Manager.Type.Image);
        
        //load all menu images
        add(Type.MenuImage, (Object[])MenuImage.values(), RESOURCE_DIR + "images/menu/{0}.gif", "Loading Menu Image Resources", Manager.Type.Image);
        
        //load all game fonts
        add(Type.GameFont, (Object[])GameFont.values(), RESOURCE_DIR + "font/{0}.ttf", "Loading Game Font Resources", Manager.Type.Font);
        
        //load all menu audio
        add(Type.MenuAudio, (Object[])MenuAudio.values(), RESOURCE_DIR + "audio/menu/{0}.wav", "Loading Menu Audio Resources", Manager.Type.Audio);
    }
    
    //add a collection of resources audio/image/font/text
    private void add(final Object key, final Object[] eachResourceKey, final String directory, final String loadDesc, final Manager.Type resourceType) throws Exception
    {
        String[] locations = new String[eachResourceKey.length];
        for (int i=0; i < locations.length; i++)
        {
            locations[i] = MessageFormat.format(directory, i);
        }

        Manager resources = new Manager(Manager.LoadMethod.OnePerFrame, locations, eachResourceKey, resourceType);
        
        //only set the description once for this specific resource or else an exception will be thrown
        resources.setDescription(loadDesc);
        
        everyResource.put(key, resources);
    }
    
    public boolean isLoading()
    {
        return loading;
    }
    
    private Manager getResources(final Object key)
    {
        return everyResource.get(key);
    }
    
    public Font getGameFont(final Object key)
    {
        return getResources(Type.GameFont).getFont(key);
    }
    
    /**
     * Get random background
     * @return Image
     */
    public Image getGameBackground()
    {
        final int index = (int)(Math.random() * GameBackgrounds.values().length);
        
        return getResources(Type.GameBackgrounds).getImage(GameBackgrounds.values()[index]);
    }
    
    /**
     * Get random game ball
     * @return Image
     */
    public Image getGameBall()
    {
        final int index = (int)(Math.random() * GameBalls.values().length);
        
        return getResources(Type.GameBalls).getImage(GameBalls.values()[index]);
    }
    
    public Image getPlayerImage(final Object key)
    {
        return getResources(Type.PlayerImage).getImage(key);
    }
    
    public Image getMenuImage(final Object key)
    {
        return getResources(Type.MenuImage).getImage(key);
    }
    
    public Audio getMenuAudio(final Object key)
    {
        return getResources(Type.MenuAudio).getAudio(key);
    }
    
    /**
     * Stop all sound
     */
    public void stopAllSound()
    {
        getResources(Type.MenuAudio).stopAllAudio();
    }
    
    public void update(final Class source) throws Exception
    {
        Object[] keys = everyResource.keySet().toArray();
        
        for (Object key : keys)
        {
            Manager resources = getResources(key);
            
            if (!resources.isComplete())
            {
                //load the resources
                resources.update(source);
                return;
            }
        }
        
        //if this line is reached we are done loading every resource
        loading = false;
    }
    
    /**
     * Checks to see if audio is turned on
     * @return 
     */
    public boolean isAudioEnabled()
    {
        //if the menu audio is not enabled the remaining audio collections should not be as well
        return getResources(Type.MenuAudio).isAudioEnabled();
    }
    
    /**
     * Set the audio enabled.
     * All existing audio collections here will have the audio enabled value set.
     * 
     * @param boolean Is the audio enabled 
     */
    public void setAudioEnabled(boolean enabled)
    {
        getResources(Type.MenuAudio).setAudioEnabled(enabled);
        
        //all other existing audio collections should be disabled here as well
        
    }
    
    public void dispose()
    {
        for (Object key : everyResource.keySet().toArray())
        {
            Manager resources = getResources(key);
            
            if (resources != null)
                resources.dispose();
            
            resources = null;
            
            everyResource.put(key, null);
        }
        
        everyResource.clear();
        everyResource = null;
    }
    
    public Graphics draw(final Graphics graphics, final Rectangle screen)
    {
        if (!loading)
            return graphics;
        
        for (Object key : everyResource.keySet().toArray())
        {
            Manager resources = getResources(key);
            
            //if loading the resources is not complete yet, draw progress
            if (!resources.isComplete())
            {
                resources.render(graphics, screen);

                return graphics;
            }
        }
        
        return graphics;
    }
}