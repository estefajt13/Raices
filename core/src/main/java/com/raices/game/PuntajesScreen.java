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

public class PuntajesScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont font;
    private BitmapFont fontGrande;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    // Dimensiones del botón
    private static final float BOTON_ANCHO = 250f;
    private static final float BOTON_ALTO = 60f;

    // Posición del botón
    private float botonX, botonY;

    // Colores
    private static final float COLOR_VERDE_OSCURO_R = 0.08f;
    private static final float COLOR_VERDE_OSCURO_G = 0.15f;
    private static final float COLOR_VERDE_OSCURO_B = 0.08f;

    private static final float COLOR_BOTON_R = 0.4f;
    private static final float COLOR_BOTON_G = 0.6f;
    private static final float COLOR_BOTON_B = 0.2f;

    private static final float COLOR_BOTON_HOVER_R = 0.5f;
    private static final float COLOR_BOTON_HOVER_G = 0.7f;
    private static final float COLOR_BOTON_HOVER_B = 0.3f;

    public PuntajesScreen(RaicesMain game) {
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

        calcularPosicionBoton();
    }

    private void calcularPosicionBoton() {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        botonX = centroX - BOTON_ANCHO / 2f;
        botonY = 100f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_OSCURO_R, COLOR_VERDE_OSCURO_G, COLOR_VERDE_OSCURO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar textos
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Título
        glyphLayout.setText(fontGrande, "Puntajes");
        fontGrande.draw(batch, "Puntajes",
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() - 150f);

        // Mensaje de "próximamente"
        String mensaje = "Próximamente";
        glyphLayout.setText(font, mensaje);
        font.draw(batch, mensaje,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f + 50f);

        // Explicación
        String explicacion = "Tabla de puntajes";
        glyphLayout.setText(font, explicacion);
        font.draw(batch, explicacion,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f);

        String explicacion2 = "se implementará con SQLite";
        glyphLayout.setText(font, explicacion2);
        font.draw(batch, explicacion2,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f - 30f);

        // Puntaje actual (temporal)
        String puntajeActual = "Puntaje actual: " + GameState.puntaje;
        glyphLayout.setText(font, puntajeActual);
        font.draw(batch, puntajeActual,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f - 80f);

        batch.end();

        // Dibujar botón con ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        float touchX = touchPos.x;
        float touchY = touchPos.y;

        boolean hover = estaSobreBoton(touchX, touchY);
        float r = hover ? COLOR_BOTON_HOVER_R : COLOR_BOTON_R;
        float g = hover ? COLOR_BOTON_HOVER_G : COLOR_BOTON_G;
        float b = hover ? COLOR_BOTON_HOVER_B : COLOR_BOTON_B;

        shapeRenderer.setColor(r, g, b, 1f);
        shapeRenderer.rect(botonX, botonY, BOTON_ANCHO, BOTON_ALTO);

        // Borde del botón
        shapeRenderer.setColor(0.2f, 0.4f, 0.1f, 1f);
        shapeRenderer.rect(botonX - 2, botonY - 2, BOTON_ANCHO + 4, 2);
        shapeRenderer.rect(botonX - 2, botonY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonX + BOTON_ANCHO, botonY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonX - 2, botonY + BOTON_ALTO, BOTON_ANCHO + 4, 2);

        shapeRenderer.end();

        // Texto del botón
        batch.begin();
        String textoBoton = "Volver";
        glyphLayout.setText(font, textoBoton);
        float textoBotonX = botonX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoBotonY = botonY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoBoton, textoBotonX, textoBotonY);
        batch.end();

        // Manejar input
        if (Gdx.input.justTouched()) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            if (estaSobreBoton(clickPos.x, clickPos.y)) {
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }
    }

    private boolean estaSobreBoton(float x, float y) {
        return x >= botonX && x <= botonX + BOTON_ANCHO &&
               y >= botonY && y <= botonY + BOTON_ALTO;
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosicionBoton();
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
