package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class MainMenuScreen implements Screen {

    private final RaicesMain game;

    public MainMenuScreen(RaicesMain game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        // Pinta la pantalla de verde oscuro (el bosque de Aethon)
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Al tocar la pantalla pasa al juego
        if (Gdx.input.justTouched()) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
