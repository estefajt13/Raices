package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class PerfilScreen implements Screen {

    private final RaicesMain game;
    private BitmapFont fontTitulo;
    private BitmapFont fontDatos;
    private BitmapFont fontTop;
    private BitmapFont fontBoton;
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private OrthographicCamera camera;
    private GlyphLayout layout;

    private Texture fondoTextura;
    private Texture botonBaseTextura;
    private Texture fotoTextura = null;

    private static final float FOTO_SIZE = 120f;
    private static final float BTN_W     = 200f;
    private static final float BTN_H     = 54f;

    private float btnVolverX,    btnVolverY;
    private float btnCompartirX, btnCompartirY;

    private boolean capturarSiguienteFrame = false;

    public PerfilScreen(RaicesMain game) {
        this.game = game;

        fontTitulo = new BitmapFont(); fontTitulo.getData().setScale(2.6f);
        fontDatos  = new BitmapFont(); fontDatos.getData().setScale(2.0f);
        fontTop    = new BitmapFont(); fontTop.getData().setScale(1.8f);
        fontBoton  = new BitmapFont(); fontBoton.getData().setScale(2.0f);

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
        // columna izquierda centrada en W*0.25
        float cxIzq = Gdx.graphics.getWidth() * 0.25f;
        btnCompartirX = cxIzq - BTN_W / 2f;
        btnCompartirY = 80f + BTN_H + 16f;
        btnVolverX    = cxIzq - BTN_W / 2f;
        btnVolverY    = 80f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.08f, 0.15f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float W   = Gdx.graphics.getWidth();
        float H   = Gdx.graphics.getHeight();

        // centros de cada columna
        float cxIzq = W * 0.25f;   // perfil + botones
        float cxDer = W * 0.72f;   // ranking

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ── Fondo ─────────────────────────────────────────────────────────
        batch.draw(fondoTextura, 0, 0, W, H);

        // ══════════════════════════════════════════════════════════════════
        //  COLUMNA IZQUIERDA — Perfil
        // ══════════════════════════════════════════════════════════════════

        // Título
        layout.setText(fontTitulo, "Perfil");
        fontTitulo.draw(batch, "Perfil",
            cxIzq - layout.width / 2f, H - 40f);

        // Foto
        float fotoX = cxIzq - FOTO_SIZE / 2f;
        float fotoY = H - 40f - layout.height - FOTO_SIZE - 30f;
        if (fotoTextura != null) {
            batch.draw(fotoTextura, fotoX, fotoY, FOTO_SIZE, FOTO_SIZE);
        }

        // Nombre
        String nombre = GameState.nombreJugador.isEmpty()
            ? "Invitado" : GameState.nombreJugador;
        String textoNombre = "Usuario: " + nombre;
        layout.setText(fontDatos, textoNombre);
        fontDatos.draw(batch, textoNombre,
            cxIzq - layout.width / 2f, fotoY - 28f);

        // Mejor puntaje
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
        fontDatos.draw(batch, puntajeTexto,
            cxIzq - layout.width / 2f, fotoY - 70f);

        // Botón Compartir
        batch.setColor(0.2f, 0.7f, 0.4f, 1f);
        batch.draw(botonBaseTextura, btnCompartirX, btnCompartirY, BTN_W, BTN_H);
        batch.setColor(1f, 1f, 1f, 1f);
        layout.setText(fontBoton, "Compartir");
        fontBoton.draw(batch, "Compartir",
            btnCompartirX + BTN_W / 2f - layout.width / 2f,
            btnCompartirY + BTN_H / 2f + fontBoton.getLineHeight() / 3f);

        // Botón Volver
        batch.draw(botonBaseTextura, btnVolverX, btnVolverY, BTN_W, BTN_H);
        layout.setText(fontBoton, "Volver");
        fontBoton.draw(batch, "Volver",
            btnVolverX + BTN_W / 2f - layout.width / 2f,
            btnVolverY + BTN_H / 2f + fontBoton.getLineHeight() / 3f);

        // ══════════════════════════════════════════════════════════════════
        //  DIVISOR vertical (línea decorativa)
        // ══════════════════════════════════════════════════════════════════
        batch.end();

        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0.3f, 0.6f, 0.3f, 1f);
        shape.rect(W * 0.49f, H * 0.05f, 2f, H * 0.90f);
        shape.end();

        batch.begin();

        // ══════════════════════════════════════════════════════════════════
        //  COLUMNA DERECHA — Clasificación
        // ══════════════════════════════════════════════════════════════════

        layout.setText(fontTitulo, "Clasificacion");
        fontTitulo.draw(batch, "Clasificacion",
            cxDer - layout.width / 2f, H - 40f);

        if (GameState.db != null) {
            List<RegistroJugador> top = GameState.db.getTopJugadores(5);

            layout.setText(fontDatos, "-- Top 5 --");
            fontDatos.draw(batch, "-- Top 5 --",
                cxDer - layout.width / 2f, H - 40f - 60f);

            float y = H - 40f - 60f - 50f;
            int pos = 1;
            for (RegistroJugador r : top) {
                // resalta al jugador actual
                if (r.id == GameState.jugadorId) {
                    fontTop.setColor(0.3f, 1f, 0.4f, 1f);  // verde brillante
                } else {
                    fontTop.setColor(1f, 1f, 1f, 1f);
                }
                String linea = pos + ".  " + r.nombre + "   " + r.puntaje + " pts";
                layout.setText(fontTop, linea);
                fontTop.draw(batch, linea, cxDer - layout.width / 2f, y);
                y -= 46f;
                pos++;
            }
            fontTop.setColor(1f, 1f, 1f, 1f); // reset color
        }

        batch.end();

        // ── Captura ───────────────────────────────────────────────────────
        if (capturarSiguienteFrame) {
            capturarSiguienteFrame = false;
            tomarYCompartirCaptura();
        }

        // ── Input ─────────────────────────────────────────────────────────
        if (Gdx.input.justTouched()) {
            Vector3 tp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(tp);

            if (tp.x >= btnVolverX && tp.x <= btnVolverX + BTN_W &&
                tp.y >= btnVolverY && tp.y <= btnVolverY + BTN_H) {
                game.setScreen(new MainMenuScreen(game));
            }

            if (tp.x >= btnCompartirX && tp.x <= btnCompartirX + BTN_W &&
                tp.y >= btnCompartirY && tp.y <= btnCompartirY + BTN_H) {
                capturarSiguienteFrame = true;
            }
        }
    }

    // ── Captura del framebuffer ────────────────────────────────────────────
    private void tomarYCompartirCaptura() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        Pixmap original = Pixmap.createFromFrameBuffer(0, 0, w, h);
        Pixmap correcto = voltearVertical(original);
        original.dispose();

        String nombreArchivo = "perfil_compartir.png";
        PixmapIO.writePNG(Gdx.files.local(nombreArchivo), correcto);
        correcto.dispose();

        if (game.getShareListener() != null) {
            String ruta = Gdx.files.local(nombreArchivo).file().getAbsolutePath();
            game.getShareListener().compartirPerfil(ruta);
        }
    }

    private Pixmap voltearVertical(Pixmap src) {
        int w = src.getWidth(), h = src.getHeight();
        Pixmap dst = new Pixmap(w, h, src.getFormat());
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                dst.drawPixel(x, y, src.getPixel(x, h - y - 1));
            }
        }
        return dst;
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
