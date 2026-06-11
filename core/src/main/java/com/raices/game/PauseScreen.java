package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class PauseScreen implements Screen {

    private final RaicesMain game;
    private final Screen pantallaAnterior;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;

    // Botones de texto con fondo de textura
    private Texture fondoBoton;
    private Rectangle btnReintentar;
    private Rectangle btnNiveles;
    private Rectangle btnMenu;

    private static final float BTN_W  = 400f;
    private static final float BTN_H  = 80f;
    private static final float BTN_PAD = 24f;

    public PauseScreen(RaicesMain game, Screen pantallaAnterior) {
        this.game = game;
        this.pantallaAnterior = pantallaAnterior;

        MusicManager.pausar();

        batch  = new SpriteBatch();
        font   = new BitmapFont();
        font.getData().setScale(3f);
        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        btnReintentar = new Rectangle(cx - BTN_W/2, cy + BTN_H + BTN_PAD,     BTN_W, BTN_H);
        btnNiveles    = new Rectangle(cx - BTN_W/2, cy,                        BTN_W, BTN_H);
        btnMenu       = new Rectangle(cx - BTN_W/2, cy - BTN_H - BTN_PAD,     BTN_W, BTN_H);

        // Fondo simple para botones (pixmap marrón)
        com.badlogic.gdx.graphics.Pixmap pm = new com.badlogic.gdx.graphics.Pixmap(1, 1,
            com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pm.setColor(0.35f, 0.20f, 0.08f, 0.9f);
        pm.fill();
        fondoBoton = new Texture(pm);
        pm.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0.6f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Título
        font.getData().setScale(4f);
        layout.setText(font, "PAUSA");
        font.draw(batch, "PAUSA",
            Gdx.graphics.getWidth() / 2f - layout.width / 2f,
            btnReintentar.y + BTN_H + 80f
        );

        // Botones
        font.getData().setScale(2.5f);
        dibujarBoton("Reintentar",    btnReintentar);
        dibujarBoton("Ver niveles",   btnNiveles);
        dibujarBoton("Menu principal",btnMenu);

        batch.end();

        // Touch
        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX();
            float ty = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (btnReintentar.contains(tx, ty)) {
                MusicManager.reanudar();
                game.setScreen(new GameScreen(game));
            } else if (btnNiveles.contains(tx, ty)) {
                MusicManager.detener();
                game.setScreen(new LevelSelectScreen(game));
            } else if (btnMenu.contains(tx, ty)) {
                MusicManager.detener();
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    private void dibujarBoton(String texto, Rectangle r) {
        batch.draw(fondoBoton, r.x, r.y, r.width, r.height);
        layout.setText(font, texto);
        font.draw(batch, texto,
            r.x + r.width / 2f - layout.width / 2f,
            r.y + r.height / 2f + layout.height / 2f
        );
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondoBoton.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
