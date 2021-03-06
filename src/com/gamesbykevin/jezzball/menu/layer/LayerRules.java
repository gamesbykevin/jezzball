package com.gamesbykevin.jezzball.menu.layer;

import com.gamesbykevin.jezzball.main.Engine;

public interface LayerRules 
{
    /**
     * Setup Layer including options if they exist
     * 
     * @param engine 
     */
    public void setup(final Engine engine) throws Exception;
}