package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class GameOverScreen implements Screen {

    private final RaicesMain game;
    private float timer = 0f;

    public GameOverScreen(RaicesMain game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.02f, 0.02f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        // Toca la pantalla para reintentar
        if (Gdx.input.justTouched() && timer > 0.5f) {
            GameState.reiniciar();
            game.setScreen(new GameScreen(game));
        }
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
