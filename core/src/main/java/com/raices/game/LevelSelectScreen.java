package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class LevelSelectScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont fontBoton;      // fuente grande para los niveles
    private BitmapFont fontGrande;     // título
    private BitmapFont fontVolver;     // botón volver
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    // ── Texturas ─────────────────────────────────────────────────────────────
    private Texture fondoTextura;
    private Texture botonBaseTextura;

    // Dimensiones botones de nivel
    private static final float BOTON_ANCHO   = 280f;
    private static final float BOTON_ALTO    = 65f;
    private static final float BOTON_ESPACIO = 90f;

    // Dimensiones botón Volver
    private static final float BTN_VOLVER_W = 200f;
    private static final float BTN_VOLVER_H = 55f;
    private float btnVolverX, btnVolverY;

    // Posiciones botones de nivel
    private float[] botonX = new float[4];
    private float[] botonY = new float[4];

    private static final float COLOR_VERDE_OSCURO_R = 0.08f;
    private static final float COLOR_VERDE_OSCURO_G = 0.15f;
    private static final float COLOR_VERDE_OSCURO_B = 0.08f;

    private static final float ALPHA_BLOQUEADO = 0.4f;

    public LevelSelectScreen(RaicesMain game) {
        this.game = game;

        // Fuente grande para texto dentro de los botones de nivel
        fontBoton = new BitmapFont();
        fontBoton.getData().setScale(1.9f);

        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(2.5f);

        fontVolver = new BitmapFont();
        fontVolver.getData().setScale(2.0f);

        batch          = new SpriteBatch();
        shapeRenderer  = new ShapeRenderer();
        glyphLayout    = new GlyphLayout();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        fondoTextura     = new Texture(Gdx.files.internal("fondo_menu.png"));
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        calcularPosiciones();
    }

    private void calcularPosiciones() {
        float ancho   = Gdx.graphics.getWidth();
        float alto    = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        // Botones de nivel — centrados verticalmente
        float startY = alto / 2f + (BOTON_ESPACIO * 1.5f);
        for (int i = 0; i < 4; i++) {
            botonX[i] = centroX - BOTON_ANCHO / 2f;
            botonY[i] = startY - (i * BOTON_ESPACIO);
        }

        // Botón Volver — centrado, debajo del último nivel con pequeño espacio
        btnVolverX = centroX - BTN_VOLVER_W / 2f;
        btnVolverY = botonY[3] - BTN_VOLVER_H - 25f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_OSCURO_R, COLOR_VERDE_OSCURO_G, COLOR_VERDE_OSCURO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ── Fondo ─────────────────────────────────────────────────────────────
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // ── Título ────────────────────────────────────────────────────────────
        glyphLayout.setText(fontGrande, "Selecciona nivel");
        fontGrande.draw(batch, "Selecciona nivel",
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() - 80f);

        // ── Botones de nivel ──────────────────────────────────────────────────
        for (int i = 0; i < 4; i++) {
            boolean desbloqueado = GameState.esNivelDesbloqueado(i + 1);
            batch.setColor(desbloqueado ? 1f : 0.45f,
                desbloqueado ? 1f : 0.45f,
                desbloqueado ? 1f : 0.45f,
                desbloqueado ? 1f : ALPHA_BLOQUEADO);
            batch.draw(botonBaseTextura, botonX[i], botonY[i], BOTON_ANCHO, BOTON_ALTO);
        }
        batch.setColor(1f, 1f, 1f, 1f);

        // ── Texto de los botones de nivel (fuente grande) ─────────────────────
        for (int i = 0; i < 4; i++) {
            int nivel = i + 1;
            boolean desbloqueado = GameState.esNivelDesbloqueado(nivel);
            String texto = desbloqueado ? "Nivel " + nivel : "Nivel " + nivel + " (Bloqueado)";

            glyphLayout.setText(fontBoton, texto);
            fontBoton.draw(batch, texto,
                botonX[i] + BOTON_ANCHO / 2f - glyphLayout.width / 2f,
                botonY[i] + BOTON_ALTO / 2f + fontBoton.getLineHeight() / 3f);
        }

        // ── Botón Volver ──────────────────────────────────────────────────────
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(botonBaseTextura, btnVolverX, btnVolverY, BTN_VOLVER_W, BTN_VOLVER_H);

        glyphLayout.setText(fontVolver, " Volver");
        fontVolver.draw(batch, "Volver",
            btnVolverX + BTN_VOLVER_W / 2f - glyphLayout.width / 2f,
            btnVolverY + BTN_VOLVER_H / 2f + fontVolver.getLineHeight() / 3f);

        batch.end();

        // ── Input ─────────────────────────────────────────────────────────────
        if (Gdx.input.justTouched()) {
            Vector3 tp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(tp);

            // Botones de nivel
            for (int i = 0; i < 4; i++) {
                int nivel = i + 1;
                if (GameState.esNivelDesbloqueado(nivel) && estaSobreBoton(i, tp.x, tp.y)) {
                    GameState.nivel = nivel;
                    GameState.reiniciarNivel();
                    game.setScreen(new GameScreen(game));
                    return;
                }
            }

            // Botón Volver
            if (tp.x >= btnVolverX && tp.x <= btnVolverX + BTN_VOLVER_W &&
                tp.y >= btnVolverY && tp.y <= btnVolverY + BTN_VOLVER_H) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }
    }

    private boolean estaSobreBoton(int indice, float x, float y) {
        return x >= botonX[indice] && x <= botonX[indice] + BOTON_ANCHO &&
            y >= botonY[indice] && y <= botonY[indice] + BOTON_ALTO;
    }

    @Override public void show()   {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosiciones();
    }

    @Override
    public void dispose() {
        fontBoton.dispose();
        fontGrande.dispose();
        fontVolver.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
    }
}
