package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class LevelSelectScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont font;
    private BitmapFont fontGrande;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    // Dimensiones de los botones
    private static final float BOTON_ANCHO = 250f;
    private static final float BOTON_ALTO = 60f;
    private static final float BOTON_ESPACIO = 80f;

    // Posiciones de los botones (se calcularán en resize)
    private float[] botonX = new float[4];
    private float[] botonY = new float[4];

    // Colores
    private static final float COLOR_VERDE_OSCURO_R = 0.08f;
    private static final float COLOR_VERDE_OSCURO_G = 0.15f;
    private static final float COLOR_VERDE_OSCURO_B = 0.08f;

    private static final float COLOR_BOTON_R = 0.4f;
    private static final float COLOR_BOTON_G = 0.6f;
    private static final float COLOR_BOTON_B = 0.2f;

    private static final float COLOR_BOTON_BLOQUEADO_R = 0.3f;
    private static final float COLOR_BOTON_BLOQUEADO_G = 0.3f;
    private static final float COLOR_BOTON_BLOQUEADO_B = 0.3f;

    private static final float COLOR_BOTON_HOVER_R = 0.5f;
    private static final float COLOR_BOTON_HOVER_G = 0.7f;
    private static final float COLOR_BOTON_HOVER_B = 0.3f;

    public LevelSelectScreen(RaicesMain game) {
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(2.5f);
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        glyphLayout = new GlyphLayout();

        calcularPosicionesBotones();
    }

    private void calcularPosicionesBotones() {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        // Posicionar los 4 botones verticalmente centrados
        float startY = alto / 2f + BOTON_ESPACIO;
        for (int i = 0; i < 4; i++) {
            botonX[i] = centroX - BOTON_ANCHO / 2f;
            botonY[i] = startY - (i * BOTON_ESPACIO);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_OSCURO_R, COLOR_VERDE_OSCURO_G, COLOR_VERDE_OSCURO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar UI
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Título
        fontGrande.draw(batch, "Selecciona nivel",
            Gdx.graphics.getWidth() / 2f - 200f,
            Gdx.graphics.getHeight() - 80f);

        batch.end();

        // Dibujar botones con ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        float touchX = touchPos.x;
        float touchY = touchPos.y;

        for (int i = 0; i < 4; i++) {
            int nivel = i + 1;
            boolean desbloqueado = GameState.esNivelDesbloqueado(nivel);
            boolean hover = desbloqueado && estaSobreBoton(i, touchX, touchY);

            // Color del botón
            float r, g, b;
            if (!desbloqueado) {
                r = COLOR_BOTON_BLOQUEADO_R;
                g = COLOR_BOTON_BLOQUEADO_G;
                b = COLOR_BOTON_BLOQUEADO_B;
            } else if (hover) {
                r = COLOR_BOTON_HOVER_R;
                g = COLOR_BOTON_HOVER_G;
                b = COLOR_BOTON_HOVER_B;
            } else {
                r = COLOR_BOTON_R;
                g = COLOR_BOTON_G;
                b = COLOR_BOTON_B;
            }

            shapeRenderer.setColor(r, g, b, 1f);
            shapeRenderer.rect(botonX[i], botonY[i], BOTON_ANCHO, BOTON_ALTO);

            // Borde del botón
            shapeRenderer.setColor(0.2f, 0.4f, 0.1f, 1f);
            shapeRenderer.rect(botonX[i] - 2, botonY[i] - 2, BOTON_ANCHO + 4, 2);
            shapeRenderer.rect(botonX[i] - 2, botonY[i], 2, BOTON_ALTO);
            shapeRenderer.rect(botonX[i] + BOTON_ANCHO, botonY[i], 2, BOTON_ALTO);
            shapeRenderer.rect(botonX[i] - 2, botonY[i] + BOTON_ALTO, BOTON_ANCHO + 4, 2);
        }

        shapeRenderer.end();

        // Dibujar texto de los botones
        batch.begin();
        for (int i = 0; i < 4; i++) {
            int nivel = i + 1;
            boolean desbloqueado = GameState.esNivelDesbloqueado(nivel);

            String texto;
            if (desbloqueado) {
                texto = "Nivel " + nivel;
            } else {
                texto = "Nivel " + nivel + " (Bloqueado)";
            }

            glyphLayout.setText(font, texto);
            float textoX = botonX[i] + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
            float textoY = botonY[i] + BOTON_ALTO / 2f + font.getLineHeight() / 3f;

            font.draw(batch, texto, textoX, textoY);
        }
        batch.end();

        // Manejar input
        if (Gdx.input.justTouched()) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            for (int i = 0; i < 4; i++) {
                int nivel = i + 1;
                if (GameState.esNivelDesbloqueado(nivel) && estaSobreBoton(i, clickPos.x, clickPos.y)) {
                    GameState.nivel = nivel;
                    GameState.reiniciarNivel();
                    game.setScreen(new GameScreen(game));
                    return;
                }
            }
        }
    }

    private boolean estaSobreBoton(int indice, float x, float y) {
        return x >= botonX[indice] && x <= botonX[indice] + BOTON_ANCHO &&
               y >= botonY[indice] && y <= botonY[indice] + BOTON_ALTO;
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosicionesBotones();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        fontGrande.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}
