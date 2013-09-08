package com.gamesbykevin.jezzball.menu.layer;

import com.gamesbykevin.framework.menu.Layer;
import com.gamesbykevin.framework.menu.Option;
import com.gamesbykevin.jezzball.main.Engine;
import com.gamesbykevin.jezzball.main.Resources;
import com.gamesbykevin.jezzball.menu.CustomMenu;

public class OptionsInGame extends Layer implements LayerRules
{
    public OptionsInGame(final Engine engine) throws Exception
    {
        super(Layer.Type.NONE, engine.getMain().getScreen());
        
        super.setTitle("Options");
        super.setForce(false);
        super.setPause(true);
        
        setup(engine);
    }
    
    @Override
    public void setup(final Engine engine) throws Exception
    {
        //setup options here
        Option tmp;
        
        tmp = new Option(CustomMenu.LayerKey.StartGame);
        tmp.add("Resume", null);
        super.add(CustomMenu.OptionKey.Resume, tmp);
        
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
        
        tmp = new Option(CustomMenu.LayerKey.NewGameConfirm);
        tmp.add("New Game", null);
        super.add(CustomMenu.OptionKey.NewGame, tmp);

        tmp = new Option(CustomMenu.LayerKey.ExitGameConfirm);
        tmp.add("Exit Game", null);
        super.add(CustomMenu.OptionKey.ExitGame, tmp);
    }
}