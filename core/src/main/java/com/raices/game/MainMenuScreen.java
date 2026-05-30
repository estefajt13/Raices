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

public class MainMenuScreen implements Screen {

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

    // Posiciones de los botones
    private float botonJugarX, botonJugarY;
    private float botonPuntajesX, botonPuntajesY;

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

    public MainMenuScreen(RaicesMain game) {
        this.game = game;
        font = new BitmapFont();
        font.getData().setScale(1.5f);
        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(3f);
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

        // Botón Jugar - centrado
        botonJugarX = centroX - BOTON_ANCHO / 2f;
        botonJugarY = alto / 2f - 40f;

        // Botón Puntajes - debajo del primero
        botonPuntajesX = centroX - BOTON_ANCHO / 2f;
        botonPuntajesY = botonJugarY - BOTON_ESPACIO;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_OSCURO_R, COLOR_VERDE_OSCURO_G, COLOR_VERDE_OSCURO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Dibujar título
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        fontGrande.draw(batch, "RAÍCES",
            Gdx.graphics.getWidth() / 2f - 120f,
            Gdx.graphics.getHeight() - 120f);
        batch.end();

        // Dibujar botones con ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);
        float touchX = touchPos.x;
        float touchY = touchPos.y;

        // Botón Jugar
        boolean hoverJugar = estaSobreBotonJugar(touchX, touchY);
        float r1 = hoverJugar ? COLOR_BOTON_HOVER_R : COLOR_BOTON_R;
        float g1 = hoverJugar ? COLOR_BOTON_HOVER_G : COLOR_BOTON_G;
        float b1 = hoverJugar ? COLOR_BOTON_HOVER_B : COLOR_BOTON_B;

        shapeRenderer.setColor(r1, g1, b1, 1f);
        shapeRenderer.rect(botonJugarX, botonJugarY, BOTON_ANCHO, BOTON_ALTO);

        // Borde botón Jugar
        shapeRenderer.setColor(0.2f, 0.4f, 0.1f, 1f);
        shapeRenderer.rect(botonJugarX - 2, botonJugarY - 2, BOTON_ANCHO + 4, 2);
        shapeRenderer.rect(botonJugarX - 2, botonJugarY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonJugarX + BOTON_ANCHO, botonJugarY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonJugarX - 2, botonJugarY + BOTON_ALTO, BOTON_ANCHO + 4, 2);

        // Botón Puntajes
        boolean hoverPuntajes = estaSobreBotonPuntajes(touchX, touchY);
        float r2 = hoverPuntajes ? COLOR_BOTON_HOVER_R : COLOR_BOTON_R;
        float g2 = hoverPuntajes ? COLOR_BOTON_HOVER_G : COLOR_BOTON_G;
        float b2 = hoverPuntajes ? COLOR_BOTON_HOVER_B : COLOR_BOTON_B;

        shapeRenderer.setColor(r2, g2, b2, 1f);
        shapeRenderer.rect(botonPuntajesX, botonPuntajesY, BOTON_ANCHO, BOTON_ALTO);

        // Borde botón Puntajes
        shapeRenderer.setColor(0.2f, 0.4f, 0.1f, 1f);
        shapeRenderer.rect(botonPuntajesX - 2, botonPuntajesY - 2, BOTON_ANCHO + 4, 2);
        shapeRenderer.rect(botonPuntajesX - 2, botonPuntajesY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonPuntajesX + BOTON_ANCHO, botonPuntajesY, 2, BOTON_ALTO);
        shapeRenderer.rect(botonPuntajesX - 2, botonPuntajesY + BOTON_ALTO, BOTON_ANCHO + 4, 2);

        shapeRenderer.end();

        // Dibujar texto de los botones
        batch.begin();
        String textoJugar = "Jugar";
        glyphLayout.setText(font, textoJugar);
        float textoJugarX = botonJugarX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoJugarY = botonJugarY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoJugar, textoJugarX, textoJugarY);

        String textoPuntajes = "Puntajes";
        glyphLayout.setText(font, textoPuntajes);
        float textoPuntajesX = botonPuntajesX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoPuntajesY = botonPuntajesY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoPuntajes, textoPuntajesX, textoPuntajesY);
        batch.end();

        // Manejar input
        if (Gdx.input.justTouched()) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            if (estaSobreBotonJugar(clickPos.x, clickPos.y)) {
                game.setScreen(new LevelSelectScreen(game));
                return;
            }

            if (estaSobreBotonPuntajes(clickPos.x, clickPos.y)) {
                // Por ahora muestra un mensaje temporal - pendiente de implementar con SQLite
                // Podríamos crear una pantalla de puntajes vacía o con mensaje
                game.setScreen(new PuntajesScreen(game));
                return;
            }
        }
    }

    private boolean estaSobreBotonJugar(float x, float y) {
        return x >= botonJugarX && x <= botonJugarX + BOTON_ANCHO &&
               y >= botonJugarY && y <= botonJugarY + BOTON_ALTO;
    }

    private boolean estaSobreBotonPuntajes(float x, float y) {
        return x >= botonPuntajesX && x <= botonPuntajesX + BOTON_ANCHO &&
               y >= botonPuntajesY && y <= botonPuntajesY + BOTON_ALTO;
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
