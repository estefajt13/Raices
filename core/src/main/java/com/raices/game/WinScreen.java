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

public class WinScreen implements Screen {

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

    // Timer para evitar toques accidentales
    private float timer = 0f;

    // Colores
    private static final float COLOR_VERDE_DORADO_R = 0.15f;
    private static final float COLOR_VERDE_DORADO_G = 0.25f;
    private static final float COLOR_VERDE_DORADO_B = 0.05f;

    private static final float COLOR_BOTON_R = 0.3f;
    private static final float COLOR_BOTON_G = 0.5f;
    private static final float COLOR_BOTON_B = 0.1f;

    private static final float COLOR_BOTON_HOVER_R = 0.4f;
    private static final float COLOR_BOTON_HOVER_G = 0.6f;
    private static final float COLOR_BOTON_HOVER_B = 0.2f;

    public WinScreen(RaicesMain game) {
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

        // Completar el nivel actual
        GameState.completarNivel(GameState.nivel);

        calcularPosicionBoton();
    }

    private void calcularPosicionBoton() {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        botonX = centroX - BOTON_ANCHO / 2f;
        botonY = alto / 2f - 80f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_DORADO_R, COLOR_VERDE_DORADO_G, COLOR_VERDE_DORADO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        // Dibujar textos
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Título
        String textoTitulo = "Nivel " + GameState.nivel + " completado";
        glyphLayout.setText(fontGrande, textoTitulo);
        fontGrande.draw(batch, textoTitulo,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() - 150f);

        // Puntaje
        String textoPuntaje = "Puntaje: " + GameState.puntaje;
        glyphLayout.setText(font, textoPuntaje);
        font.draw(batch, textoPuntaje,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f + 50f);

        // Vidas restantes
        String textoVidas = "Vidas restantes: " + GameState.vidas;
        glyphLayout.setText(font, textoVidas);
        font.draw(batch, textoVidas,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f + 10f);

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
        int siguienteNivel = GameState.getNivelSiguiente(GameState.nivel);
        String textoBoton;
        if (siguienteNivel > 0) {
            textoBoton = "Siguiente nivel";
        } else {
            textoBoton = "Menú principal";
        }
        glyphLayout.setText(font, textoBoton);
        float textoBotonX = botonX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoBotonY = botonY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoBoton, textoBotonX, textoBotonY);
        batch.end();

        // Manejar input
        if (Gdx.input.justTouched() && timer > 0.5f) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            if (estaSobreBoton(clickPos.x, clickPos.y)) {
                int siguiente = GameState.getNivelSiguiente(GameState.nivel);
                if (siguiente > 0) {
                    // Ir al siguiente nivel
                    GameState.nivel = siguiente;
                    GameState.reiniciarNivel();
                    game.setScreen(new GameScreen(game));
                } else {
                    // Volver al menú principal (no hay más niveles)
                    GameState.reiniciar();
                    game.setScreen(new MainMenuScreen(game));
                }
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
