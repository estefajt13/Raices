package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class WinScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont font;
    private BitmapFont fontGrande;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private GlyphLayout glyphLayout;

    private Texture fondoTextura;
    private Texture botonBaseTextura;

    private static final float BOTON_ANCHO = 250f;
    private static final float BOTON_ALTO  = 60f;

    private float botonX, botonY;
    private float timer = 0f;

    public WinScreen(RaicesMain game) {
        this.game = game;

        font = new BitmapFont();
        font.getData().setScale(2.0f);
        fontGrande = new BitmapFont();
        fontGrande.getData().setScale(3.5f);

        batch       = new SpriteBatch();
        camera      = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        glyphLayout = new GlyphLayout();

        fondoTextura     = new Texture(Gdx.files.internal("fondo_menu.png"));
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        GameState.completarNivel(GameState.nivel);
        calcularPosicionBoton();
    }

    private void calcularPosicionBoton() {
        botonX = Gdx.graphics.getWidth()  / 2f - BOTON_ANCHO / 2f;
        botonY = Gdx.graphics.getHeight() / 2f - 80f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        timer += delta;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // Fondo
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Título
        String textoTitulo = "Nivel " + GameState.nivel + " completado";
        glyphLayout.setText(fontGrande, textoTitulo);
        fontGrande.draw(batch, textoTitulo,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() - 150f);

        // Info
        String textoPuntaje = "Puntaje: " + GameState.puntaje;
        glyphLayout.setText(font, textoPuntaje);
        font.draw(batch, textoPuntaje,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f + 50f);

        String textoVidas = "Vidas restantes: " + GameState.vidas;
        glyphLayout.setText(font, textoVidas);
        font.draw(batch, textoVidas,
            Gdx.graphics.getWidth() / 2f - glyphLayout.width / 2f,
            Gdx.graphics.getHeight() / 2f + 10f);

        // Botón
        int siguienteNivel = GameState.getNivelSiguiente(GameState.nivel);
        String textoBoton  = siguienteNivel > 0 ? "Siguiente nivel" : "Menú principal";

        batch.draw(botonBaseTextura, botonX, botonY, BOTON_ANCHO, BOTON_ALTO);

        glyphLayout.setText(font, textoBoton);
        font.draw(batch, textoBoton,
            botonX + BOTON_ANCHO / 2f - glyphLayout.width / 2f,
            botonY + BOTON_ALTO  / 2f + font.getLineHeight() / 3f);

        batch.end();

        // Input
        if (Gdx.input.justTouched() && timer > 0.5f) {
            Vector3 click = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(click);

            if (estaSobreBoton(click.x, click.y)) {
                int siguiente = GameState.getNivelSiguiente(GameState.nivel);
                if (siguiente > 0) {
                    GameState.nivel = siguiente;
                    GameState.reiniciarNivel();
                    game.setScreen(new GameScreen(game));
                } else {
                    GameState.reiniciar();
                    game.setScreen(new MainMenuScreen(game));
                }
            }
        }
    }

    private boolean estaSobreBoton(float x, float y) {
        return x >= botonX && x <= botonX + BOTON_ANCHO &&
            y >= botonY && y <= botonY + BOTON_ALTO;
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        calcularPosicionBoton();
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        font.dispose();
        fontGrande.dispose();
        batch.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
    }
}
