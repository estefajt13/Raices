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

public class MainMenuScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont font;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    // ── Imagen del título ────────────────────────────────────────────────────
    private Texture texturaTitulo;
    private static final float TITULO_ANCHO = 500f;
    private static final float TITULO_ALTO  = 150f;
    private static final float TITULO_MARGEN_TOP = 40f; // espacio desde el borde superior

    // ── Fondo ────────────────────────────────────────────────────────────────
    private Texture fondoTextura;

    // ── Textura base de botones ────────────────────────────────────────────
    private Texture botonBaseTextura;

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

        // Se elimina fontGrande — ya no se usa para el título
        font = new BitmapFont();
        font.getData().setScale(2.0f);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        glyphLayout = new GlyphLayout();

        // Cargar imagen del título
        texturaTitulo = new Texture(Gdx.files.internal("titulo_raices.png"));

        // Cargar fondo
        fondoTextura = new Texture(Gdx.files.internal("fondo_menu.png"));

        // Cargar textura base de botones
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        calcularPosicionesBotones();
    }

    private void calcularPosicionesBotones() {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        // Botón Jugar - centrado
        botonJugarX = centroX - BOTON_ANCHO / 2f;
        botonJugarY = alto / 2f - 40f;

        // Botón Perfil - debajo del primero
        botonPuntajesX = centroX - BOTON_ANCHO / 2f;
        botonPuntajesY = botonJugarY - BOTON_ESPACIO;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(COLOR_VERDE_OSCURO_R, COLOR_VERDE_OSCURO_G, COLOR_VERDE_OSCURO_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ── Fondo ────────────────────────────────────────────────────────────
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // ── Imagen del título (reemplaza fontGrande.draw "RAÍCES") ───────────
        float tituloX = Gdx.graphics.getWidth() / 2f - TITULO_ANCHO / 2f;
        float tituloY = Gdx.graphics.getHeight() - TITULO_ALTO - TITULO_MARGEN_TOP;
        batch.draw(texturaTitulo, tituloX, tituloY, TITULO_ANCHO, TITULO_ALTO);

        // ── Botones como texturas ────────────────────────────────────────────
        batch.draw(botonBaseTextura, botonJugarX, botonJugarY, BOTON_ANCHO, BOTON_ALTO);
        batch.draw(botonBaseTextura, botonPuntajesX, botonPuntajesY, BOTON_ANCHO, BOTON_ALTO);

        // ── Texto de los botones ─────────────────────────────────────────────

        String textoJugar = "Jugar";
        glyphLayout.setText(font, textoJugar);
        float textoJugarX = botonJugarX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoJugarY = botonJugarY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoJugar, textoJugarX, textoJugarY);

        String textoPerfil = "Perfil";
        glyphLayout.setText(font, textoPerfil);
        float textoPerfilX = botonPuntajesX + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textoPerfilY = botonPuntajesY + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, textoPerfil, textoPerfilX, textoPerfilY);

        batch.end();

        // ── Input ────────────────────────────────────────────────────────────
        if (Gdx.input.justTouched()) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            if (estaSobreBotonJugar(clickPos.x, clickPos.y)) {
                game.setScreen(new LevelSelectScreen(game));
                return;
            }

            if (estaSobreBotonPuntajes(clickPos.x, clickPos.y)) {
                game.setScreen(new PerfilScreen(game));
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

    @Override public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosicionesBotones();
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        shapeRenderer.dispose();
        texturaTitulo.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
    }
}
