package com.raices.game;

import com.badlogic.gdx.Game;

public class RaicesMain extends Game {

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }
}
