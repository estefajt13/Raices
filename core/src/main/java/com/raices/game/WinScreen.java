package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WinScreen implements Screen {

    private final RaicesMain game;
    private float timer = 0f;

    private BitmapFont font;
    private SpriteBatch batch;

    public WinScreen(RaicesMain game) {
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(2.5f);
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // Fondo verde dorado — llegaste a la cima
        Gdx.gl.glClearColor(0.15f, 0.25f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        batch.begin();
        font.draw(batch,
            "Nivel 1 completado",
            Gdx.graphics.getWidth() / 2f - 180f,
            Gdx.graphics.getHeight() / 2f + 40f
        );
        font.draw(batch,
            "Toca para continuar",
            Gdx.graphics.getWidth() / 2f - 180f,
            Gdx.graphics.getHeight() / 2f - 40f
        );
        batch.end();

        // Toca para volver al menú — por ahora hasta que haya nivel 2
        if (Gdx.input.justTouched() && timer > 0.5f) {
            GameState.reiniciar();
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
