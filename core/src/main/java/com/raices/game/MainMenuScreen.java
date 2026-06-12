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
    private static final float TITULO_MARGEN_TOP = 40f;

    // ── Fondo ────────────────────────────────────────────────────────────────
    private Texture fondoTextura;

    // ── Botón base ──────────────────────────────────────────────────────────
    private Texture botonBaseTextura;

    // Dimensiones
    private static final float BOTON_ANCHO = 250f;
    private static final float BOTON_ALTO = 60f;
    private static final float BOTON_ESPACIO = 80f;

    // Posiciones
    private float botonJugarX, botonJugarY;
    private float botonPerfilX, botonPerfilY;
    private float botonCerrarSesionX, botonCerrarSesionY;

    public MainMenuScreen(RaicesMain game) {
        this.game = game;

        font = new BitmapFont();
        font.getData().setScale(2.0f);

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        glyphLayout = new GlyphLayout();

        texturaTitulo = new Texture(Gdx.files.internal("titulo_raices.png"));
        fondoTextura = new Texture(Gdx.files.internal("fondo_menu.png"));
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        calcularPosicionesBotones();
    }

    private void calcularPosicionesBotones() {
        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();
        float centroX = ancho / 2f;

        // Jugar
        botonJugarX = centroX - BOTON_ANCHO / 2f;
        botonJugarY = alto / 2f;

        // Perfil
        botonPerfilX = centroX - BOTON_ANCHO / 2f;
        botonPerfilY = botonJugarY - BOTON_ESPACIO;

        // Cerrar sesión (nuevo)
        botonCerrarSesionX = centroX - BOTON_ANCHO / 2f;
        botonCerrarSesionY = botonPerfilY - BOTON_ESPACIO;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fondo
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Título
        float tituloX = Gdx.graphics.getWidth() / 2f - TITULO_ANCHO / 2f;
        float tituloY = Gdx.graphics.getHeight() - TITULO_ALTO - TITULO_MARGEN_TOP;
        batch.draw(texturaTitulo, tituloX, tituloY, TITULO_ANCHO, TITULO_ALTO);

        // Botones
        batch.draw(botonBaseTextura, botonJugarX, botonJugarY, BOTON_ANCHO, BOTON_ALTO);
        batch.draw(botonBaseTextura, botonPerfilX, botonPerfilY, BOTON_ANCHO, BOTON_ALTO);
        batch.draw(botonBaseTextura, botonCerrarSesionX, botonCerrarSesionY, BOTON_ANCHO, BOTON_ALTO);

        // Texto botones
        drawCenteredText("Jugar", botonJugarX, botonJugarY);
        drawCenteredText("Perfil", botonPerfilX, botonPerfilY);
        drawCenteredText("Cerrar sesión", botonCerrarSesionX, botonCerrarSesionY);

        batch.end();

        // Input
        if (Gdx.input.justTouched()) {
            Vector3 clickPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(clickPos);

            if (estaSobreBoton(clickPos.x, clickPos.y, botonJugarX, botonJugarY)) {
                game.setScreen(new LevelSelectScreen(game));
                return;
            }

            if (estaSobreBoton(clickPos.x, clickPos.y, botonPerfilX, botonPerfilY)) {
                game.setScreen(new PerfilScreen(game));
                return;
            }

            if (estaSobreBoton(clickPos.x, clickPos.y, botonCerrarSesionX, botonCerrarSesionY)) {
                cerrarSesion();
                return;
            }
        }
    }

    // ── Método reutilizable para texto centrado ─────────────────────────────
    private void drawCenteredText(String texto, float x, float y) {
        glyphLayout.setText(font, texto);
        float textX = x + BOTON_ANCHO / 2f - glyphLayout.width / 2f;
        float textY = y + BOTON_ALTO / 2f + font.getLineHeight() / 3f;
        font.draw(batch, texto, textX, textY);
    }

    private boolean estaSobreBoton(float x, float y, float bx, float by) {
        return x >= bx && x <= bx + BOTON_ANCHO &&
            y >= by && y <= by + BOTON_ALTO;
    }

    // ── Lógica de cerrar sesión ─────────────────────────────────────────────
    private void cerrarSesion() {

        // 🔴 Aquí conectas tu sistema real
        // Ejemplo:
        // PlayerRepository.getInstance().logout();
        // GameState.getInstance().clear();

        System.out.println("Sesión cerrada");

        // Redirigir a login
        game.setScreen(new LoginScreen(game));
    }

    @Override public void show() {}

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosicionesBotones();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

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
