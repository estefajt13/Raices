package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class PauseScreen implements Screen {

    private final RaicesMain game;
    private final Screen pantallaAnterior;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private OrthographicCamera camera;

    private Texture fondoTextura;
    private Texture botonBaseTextura;

    private static final float BTN_W   = 250f;
    private static final float BTN_H   = 60f;
    private static final float BTN_PAD = 80f;

    private float btnReintentarX, btnReintentarY;
    private float btnNivelesX,    btnNivelesY;
    private float btnMenuX,       btnMenuY;

    public PauseScreen(RaicesMain game, Screen pantallaAnterior) {
        this.game             = game;
        this.pantallaAnterior = pantallaAnterior;

        MusicManager.pausar();

        batch  = new SpriteBatch();
        font   = new BitmapFont();
        font.getData().setScale(2.0f);
        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        fondoTextura    = new Texture(Gdx.files.internal("fondo_menu.png"));
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        calcularPosiciones();
    }

    private void calcularPosiciones() {
        float cx = Gdx.graphics.getWidth()  / 2f;
        float cy = Gdx.graphics.getHeight() / 2f;

        btnReintentarX = cx - BTN_W / 2f;
        btnReintentarY = cy + BTN_PAD;

        btnNivelesX = cx - BTN_W / 2f;
        btnNivelesY = cy;

        btnMenuX = cx - BTN_W / 2f;
        btnMenuY = cy - BTN_PAD;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fondo
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Título
        font.getData().setScale(3.0f);
        layout.setText(font, "PAUSA");
        font.draw(batch, "PAUSA",
            Gdx.graphics.getWidth() / 2f - layout.width / 2f,
            btnReintentarY + BTN_H + 80f);
        font.getData().setScale(2.0f);

        // Botones
        batch.draw(botonBaseTextura, btnReintentarX, btnReintentarY, BTN_W, BTN_H);
        batch.draw(botonBaseTextura, btnNivelesX,    btnNivelesY,    BTN_W, BTN_H);
        batch.draw(botonBaseTextura, btnMenuX,       btnMenuY,       BTN_W, BTN_H);

        // Textos
        drawCenteredText("Volver al nivel",     btnReintentarX, btnReintentarY);
        drawCenteredText("Ver niveles",    btnNivelesX,    btnNivelesY);
        drawCenteredText("Menú principal", btnMenuX,       btnMenuY);

        batch.end();

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 click = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(click);

            // DESPUÉS
            if (sobreBoton(click.x, click.y, btnReintentarX, btnReintentarY)) {
                MusicManager.reanudar();
                game.setScreen(pantallaAnterior);      // ← vuelve exactamente donde se pausó
            } else if (sobreBoton(click.x, click.y, btnNivelesX, btnNivelesY)) {
                MusicManager.detener();
                game.setScreen(new LevelSelectScreen(game));
            } else if (sobreBoton(click.x, click.y, btnMenuX, btnMenuY)) {
                MusicManager.detener();
                game.setScreen(new MainMenuScreen(game));
            }
        }
    }

    private void drawCenteredText(String texto, float x, float y) {
        layout.setText(font, texto);
        float tx = x + BTN_W / 2f - layout.width  / 2f;
        float ty = y + BTN_H / 2f + font.getLineHeight() / 3f;
        font.draw(batch, texto, tx, ty);
    }

    private boolean sobreBoton(float x, float y, float bx, float by) {
        return x >= bx && x <= bx + BTN_W &&
            y >= by && y <= by + BTN_H;
    }

    @Override public void show() {}
    @Override public void resize(int w, int h) {
        camera.setToOrtho(false, w, h);
        calcularPosiciones();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
    }
}
