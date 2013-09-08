package com.gamesbykevin.jezzball.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.TimerCollection;

import com.gamesbykevin.jezzball.balls.BallManager.*;
import com.gamesbykevin.jezzball.main.Engine;
import com.gamesbykevin.jezzball.main.Resources;
import com.gamesbykevin.jezzball.manager.Manager;
import com.gamesbykevin.jezzball.menu.CustomMenu;
import com.gamesbykevin.jezzball.player.Player;
import com.gamesbykevin.jezzball.player.Player.CaptureSpeed;

public class Options extends Layer implements LayerRules
{
    public Options(final Engine engine) throws Exception
    {
        super(Layer.Type.SCROLL_HORIZONTAL_WEST_REPEAT, engine.getMain().getScreen());
        
        super.setTitle("Options");
        super.setImage(engine.getResources().getMenuImage(Resources.MenuImage.TitleBackground));
        super.setTimer(new Timer(TimerCollection.toNanoSeconds(5000L)));
        super.setForce(false);
        super.setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option("Mode: ");
        for (Manager.Mode mode : Manager.Mode.values())
        {
            tmp.add(mode.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.Mode, tmp);
        
        tmp = new Option("Ball Size: ");
        for (BallSize size : BallSize.values())
        {
            tmp.add(size.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.BallSize, tmp);
        
        tmp = new Option("Ball Speed: ");
        for (BallSpeed speed : BallSpeed.values())
        {
            tmp.add(speed.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.BallSpeed, tmp);
        
        tmp = new Option("Capture Speed: ");
        for (CaptureSpeed speed : CaptureSpeed.values())
        {
            tmp.add(speed.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.CaptureSpeed, tmp);
        
        tmp = new Option("Lives Per Level: ");
        for (Integer count : Player.START_LIVES)
        {
            tmp.add(count.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.Lives, tmp);
        
        tmp = new Option("Level Start: ");
        for (Integer level : Manager.LEVEL_START)
        {
            tmp.add(level.toString(), engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        }
        super.add(CustomMenu.OptionKey.LevelStart, tmp);

        tmp = new Option("Cheat: ");
        tmp.add("Off", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("On",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.Cheat, tmp);
        
        tmp = new Option("Sound: ");
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.Sound, tmp);
        
        tmp = new Option("FullScreen: ");
        tmp.add("Off",engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        tmp.add("On", engine.getResources().getMenuAudio(Resources.MenuAudio.MenuChange));
        super.add(CustomMenu.OptionKey.FullScreen, tmp);
        
        tmp = new Option(CustomMenu.LayerKey.MainTitle);
        tmp.add("Go Back", null);
        super.add(CustomMenu.OptionKey.GoBack, tmp);
    }
}