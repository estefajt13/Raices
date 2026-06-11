package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class PerfilScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont fontTitulo;   // "Perfil"
    private BitmapFont fontDatos;    // nombre, puntaje, encabezado top
    private BitmapFont fontTop;      // lista top 5
    private BitmapFont fontBoton;    // botón volver
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private OrthographicCamera camera;
    private GlyphLayout layout;

    // ── Texturas ──────────────────────────────────────────────────────────────
    private Texture fondoTextura;
    private Texture botonBaseTextura;
    private Texture fotoTextura = null;

    private static final float FOTO_SIZE = 130f;
    private static final float BTN_W     = 220f;
    private static final float BTN_H     = 58f;

    private float btnVolverX, btnVolverY;

    public PerfilScreen(RaicesMain game) {
        this.game = game;

        fontTitulo = new BitmapFont(); fontTitulo.getData().setScale(3.0f);
        fontDatos  = new BitmapFont(); fontDatos.getData().setScale(2.2f);
        fontTop    = new BitmapFont(); fontTop.getData().setScale(1.9f);
        fontBoton  = new BitmapFont(); fontBoton.getData().setScale(2.2f);

        batch  = new SpriteBatch();
        shape  = new ShapeRenderer();
        layout = new GlyphLayout();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        fondoTextura     = new Texture(Gdx.files.internal("fondo_menu.png"));
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        if (GameState.fotoJugador != null) {
            Pixmap pm = new Pixmap(GameState.fotoJugador, 0, GameState.fotoJugador.length);
            fotoTextura = new Texture(pm);
            pm.dispose();
        }

        calcularPosiciones();
    }

    private void calcularPosiciones() {
        float cx = Gdx.graphics.getWidth() / 2f;
        btnVolverX = cx - BTN_W / 2f;
        btnVolverY = 55f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float W  = Gdx.graphics.getWidth();
        float H  = Gdx.graphics.getHeight();
        float cx = W / 2f;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ── Fondo ─────────────────────────────────────────────────────────────
        batch.draw(fondoTextura, 0, 0, W, H);

        // ── Título ────────────────────────────────────────────────────────────
        layout.setText(fontTitulo, "Perfil");
        fontTitulo.draw(batch, "Perfil", cx - layout.width / 2f, H - 60f);

        // ── Foto — espacio generoso bajo el título ────────────────────────────
        float fotoX = cx - FOTO_SIZE / 2f;
        float fotoY = H - 60f - FOTO_SIZE - 80f;

        if (fotoTextura != null) {
            batch.draw(fotoTextura, fotoX, fotoY, FOTO_SIZE, FOTO_SIZE);
        }

        // ── Nombre de usuario ─────────────────────────────────────────────────
        String nombre = GameState.nombreJugador.isEmpty() ? "Invitado" : GameState.nombreJugador;
        String textoNombre = "Usuario: " + nombre;
        layout.setText(fontDatos, textoNombre);
        fontDatos.draw(batch, textoNombre, cx - layout.width / 2f, fotoY - 35f);

        // ── Mejor puntaje ─────────────────────────────────────────────────────
        int puntajeGuardado = -1;
        if (GameState.db != null && GameState.jugadorId >= 0) {
            for (RegistroJugador r : GameState.db.getTopJugadores(50)) {
                if (r.id == GameState.jugadorId) { puntajeGuardado = r.puntaje; break; }
            }
        }
        String puntajeTexto = puntajeGuardado >= 0
            ? "Mejor puntaje: " + puntajeGuardado
            : "Mejor puntaje: --";
        layout.setText(fontDatos, puntajeTexto);
        fontDatos.draw(batch, puntajeTexto, cx - layout.width / 2f, fotoY - 80f);

        // ── Top 5 — separado con más espacio ──────────────────────────────────
        float sepY = fotoY - 150f;

        if (GameState.db != null) {
            List<RegistroJugador> top = GameState.db.getTopJugadores(5);

            layout.setText(fontDatos, "-- Top 5 --");
            fontDatos.draw(batch, "-- Top 5 --", cx - layout.width / 2f, sepY);

            float y = sepY - 45f;
            int pos = 1;
            for (RegistroJugador r : top) {
                String linea = pos + ".  " + r.nombre + "   " + r.puntaje + " pts";
                layout.setText(fontTop, linea);
                fontTop.draw(batch, linea, cx - layout.width / 2f, y);
                y -= 38f;
                pos++;
            }
        }

        // ── Botón Volver ──────────────────────────────────────────────────────
        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(botonBaseTextura, btnVolverX, btnVolverY, BTN_W, BTN_H);

        layout.setText(fontBoton, "Volver");
        fontBoton.draw(batch, "Volver",
            btnVolverX + BTN_W / 2f - layout.width / 2f,
            btnVolverY + BTN_H / 2f + fontBoton.getLineHeight() / 3f);

        batch.end();

        // ── Input ─────────────────────────────────────────────────────────────
        if (Gdx.input.justTouched()) {
            Vector3 tp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(tp);
            if (tp.x >= btnVolverX && tp.x <= btnVolverX + BTN_W &&
                tp.y >= btnVolverY && tp.y <= btnVolverY + BTN_H) {
                game.setScreen(new MainMenuScreen(game));
            }
        }
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
        fontTitulo.dispose();
        fontDatos.dispose();
        fontTop.dispose();
        fontBoton.dispose();
        batch.dispose();
        shape.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
        if (fotoTextura != null) fotoTextura.dispose();
    }
}
