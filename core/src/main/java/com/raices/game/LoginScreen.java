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

public class LoginScreen implements Screen, ImagePickerListener {

    private final RaicesMain game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private ShapeRenderer shape;
    private BitmapFont fontNormal, fontPequena;
    private GlyphLayout layout;

    // ── Imagen del título ────────────────────────────────────────────────────
    private Texture texturaTitulo;
    private static final float TITULO_ANCHO = 460f;
    private static final float TITULO_ALTO  = 138f;
    // ── Fondo ────────────────────────────────────────────────────────────────
    private Texture fondoTextura;

    // ── Textura base de botones ────────────────────────────────────────────
    private Texture botonBaseTextura;

    // ── Campo de texto (simulado con input) ─────────────────────────────────
    private String nombre = "";
    private boolean campoActivo = false;

    // ── Foto ────────────────────────────────────────────────────────────────
    private byte[] fotoBytes  = null;
    private Texture fotoTextura = null;

    // ── Botones ─────────────────────────────────────────────────────────────
    private static final float BTN_W = 280f;
    private static final float BTN_H = 60f;

    private float btnFotoX,     btnFotoY;
    private float btnCamaraX,   btnCamaraY;
    private float btnRegistrarseX, btnRegistrarseY;
    private float btnIniciarSesionX, btnIniciarSesionY;
    private float campoX,       campoY,  campoW = 500f, campoH = 55f;
    private float fotoPreviewX, fotoPreviewY;
    private static final float FOTO_SIZE = 120f;

    // ── Colores ─────────────────────────────────────────────────────────────
    private static final float[] C_FONDO  = {0.08f, 0.15f, 0.08f};
    private static final float[] C_BOTON  = {0.4f,  0.6f,  0.2f };
    private static final float[] C_HOVER  = {0.5f,  0.7f,  0.3f };
    private static final float[] C_CAMPO  = {0.15f, 0.22f, 0.12f};
    private static final float[] C_BORDE  = {0.2f,  0.4f,  0.1f };

    // Error / mensaje
    private String mensaje = "";

    public LoginScreen(RaicesMain game) {
        this.game = game;
        game.setCallbackLogin(this);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch  = new SpriteBatch();
        shape  = new ShapeRenderer();
        layout = new GlyphLayout();

        // Se elimina fontTitulo — ya no se usa para el título
        fontNormal = new BitmapFont();  fontNormal.getData().setScale(2.0f);
        fontPequena= new BitmapFont();  fontPequena.getData().setScale(1.8f);

        // Cargar imagen del título
        texturaTitulo = new Texture(Gdx.files.internal("titulo_raices.png"));

        // Cargar fondo
        fondoTextura = new Texture(Gdx.files.internal("fondo_menu.png"));

        // Cargar textura base de botones
        botonBaseTextura = new Texture(Gdx.files.internal("boton_base.png"));

        calcularLayout();
    }

    private void calcularLayout() {
        float cx  = Gdx.graphics.getWidth()  / 2f;
        float cy  = Gdx.graphics.getHeight() / 2f;
        float alto = Gdx.graphics.getHeight();

        // Imagen del título — cerca del borde superior
        // (se posiciona en render() usando alto - TITULO_ALTO - margen)

        // Preview foto — ligeramente arriba del centro
        fotoPreviewX = cx - FOTO_SIZE / 2f;
        fotoPreviewY = cy + 40f;

        // Campo nombre — debajo de la foto
        campoX = cx - campoW / 2f;
        campoY = fotoPreviewY - campoH - 60f;

        // Botón galería
        btnFotoX = cx - BTN_W - 10f;
        btnFotoY = campoY - BTN_H - 20f;

        // Botón cámara
        btnCamaraX = cx + 10f;
        btnCamaraY = btnFotoY;

        // Botón Registrarse - izquierda, abajo
        btnRegistrarseX = cx - BTN_W - 10f;
        btnRegistrarseY = btnFotoY - BTN_H - 30f;

        // Botón Iniciar sesión - derecha, abajo
        btnIniciarSesionX = cx + 10f;
        btnIniciarSesionY = btnRegistrarseY;

        // Garantizar margen inferior mínimo
        float bottomMargin = 60f;
        if (btnRegistrarseY < bottomMargin) {
            float shift = bottomMargin - btnRegistrarseY;
            fotoPreviewY += shift;
            campoY += shift;
            btnFotoY += shift;
            btnCamaraY += shift;
            btnRegistrarseY += shift;
            btnIniciarSesionY += shift;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(C_FONDO[0], C_FONDO[1], C_FONDO[2], 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ── Texto y texturas ─────────────────────────────────────────────────
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // ── Fondo ────────────────────────────────────────────────────────────
        batch.draw(fondoTextura, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // ── Imagen del título (reemplaza fontTitulo.draw "RAÍCES") ───────────
        float tituloX = Gdx.graphics.getWidth() / 2f - TITULO_ANCHO / 2f;
        float tituloY = Gdx.graphics.getHeight() - TITULO_ALTO - 40f;
        batch.draw(texturaTitulo, tituloX, tituloY, TITULO_ANCHO, TITULO_ALTO);

        // Label campo
        fontPequena.draw(batch, "Tu nombre:",
            campoX, campoY + campoH + 28f);

        // Texto dentro del campo
        String textoMostrado = nombre.isEmpty() ? "Escribe aquí..." : nombre + (campoActivo ? "|" : "");
        fontNormal.setColor(nombre.isEmpty() ? 0.5f : 1f, nombre.isEmpty() ? 0.5f : 1f,
            nombre.isEmpty() ? 0.5f : 1f, 1f);
        fontNormal.draw(batch, textoMostrado, campoX + 12f, campoY + campoH - 14f);
        fontNormal.setColor(1f, 1f, 1f, 1f);

        // Foto preview
        if (fotoTextura != null) {
            batch.draw(fotoTextura, fotoPreviewX, fotoPreviewY, FOTO_SIZE, FOTO_SIZE);
        }

        // Label foto
        String labelFoto = fotoBytes == null ? "Sin foto" : "Foto lista";
        layout.setText(fontPequena, labelFoto);
        fontPequena.draw(batch, labelFoto,
            Gdx.graphics.getWidth() / 2f - layout.width / 2f,
            fotoPreviewY - 6f);

        // Mensaje de error / confirmación
        if (!mensaje.isEmpty()) {
            fontPequena.setColor(1f, 0.4f, 0.4f, 1f);
            layout.setText(fontPequena, mensaje);
            fontPequena.draw(batch, mensaje,
                Gdx.graphics.getWidth() / 2f - layout.width / 2f,
                btnRegistrarseY - 20f);
            fontPequena.setColor(1f, 1f, 1f, 1f);
        }

        batch.end();

        // ── Campos y bordes ──────────────────────────────────────────────────
        shape.setProjectionMatrix(camera.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);

        // Preview foto (fondo gris si no hay foto)
        if (fotoTextura == null) {
            shape.setColor(0.25f, 0.3f, 0.2f, 1f);
            shape.rect(fotoPreviewX, fotoPreviewY, FOTO_SIZE, FOTO_SIZE);
        }

        // Campo nombre
        shape.setColor(C_CAMPO[0], C_CAMPO[1], C_CAMPO[2], 1f);
        shape.rect(campoX, campoY, campoW, campoH);

        // Borde campo
        float bc = campoActivo ? 0.6f : 0.3f;
        shape.setColor(0f, bc, 0f, 1f);
        shape.rect(campoX - 2, campoY - 2, campoW + 4, 2);
        shape.rect(campoX - 2, campoY,     2, campoH);
        shape.rect(campoX + campoW, campoY, 2, campoH);
        shape.rect(campoX - 2, campoY + campoH, campoW + 4, 2);

        shape.end();

        // ── Botones como texturas ────────────────────────────────────────────
        batch.begin();

        batch.draw(botonBaseTextura, btnFotoX,           btnFotoY,           BTN_W, BTN_H);
        batch.draw(botonBaseTextura, btnCamaraX,         btnCamaraY,         BTN_W, BTN_H);
        batch.draw(botonBaseTextura, btnRegistrarseX,    btnRegistrarseY,    BTN_W, BTN_H);
        batch.draw(botonBaseTextura, btnIniciarSesionX,  btnIniciarSesionY,  BTN_W, BTN_H);

        // ── Texto sobre botones ──────────────────────────────────────────────
        dibujarTextoCentrado(fontNormal, "Galería",        btnFotoX,           btnFotoY,           BTN_W, BTN_H);
        dibujarTextoCentrado(fontNormal, "Cámara",         btnCamaraX,         btnCamaraY,         BTN_W, BTN_H);
        dibujarTextoCentrado(fontNormal, "Registrarse",    btnRegistrarseX,    btnRegistrarseY,    BTN_W, BTN_H);
        dibujarTextoCentrado(fontNormal, "Iniciar sesión", btnIniciarSesionX,  btnIniciarSesionY,  BTN_W, BTN_H);

        batch.end();

        // ── Input ────────────────────────────────────────────────────────────
        Vector3 tp = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tp);
        manejarInput(tp.x, tp.y);
    }

    private void dibujarTextoCentrado(BitmapFont f, String texto,
                                       float bx, float by, float bw, float bh) {
        layout.setText(f, texto);
        f.draw(batch, texto,
            bx + bw/2f - layout.width/2f,
            by + bh/2f + f.getLineHeight()/3f);
    }

    private void manejarInput(float tx, float ty) {
        if (!Gdx.input.justTouched()) return;

        // Toque en campo nombre → abre teclado nativo
        if (tx >= campoX && tx <= campoX+campoW && ty >= campoY && ty <= campoY+campoH) {
            campoActivo = true;
            Gdx.input.getTextInput(new com.badlogic.gdx.Input.TextInputListener() {
                @Override public void input(String text)  {
                    Gdx.app.postRunnable(() -> { nombre = text.trim(); campoActivo = false; mensaje = ""; });
                }
                @Override public void canceled() {
                    Gdx.app.postRunnable(() -> { campoActivo = false; });
                }
            }, "Ingresa tu nombre", nombre, "Ej: Lumi123");
            return;
        }

        // Galería
        if (tx >= btnFotoX && tx <= btnFotoX+BTN_W && ty >= btnFotoY && ty <= btnFotoY+BTN_H) {
            game.abrirGaleria();
            return;
        }

        // Cámara
        if (tx >= btnCamaraX && tx <= btnCamaraX+BTN_W && ty >= btnCamaraY && ty <= btnCamaraY+BTN_H) {
            game.abrirCamara();
            return;
        }

        // Registrarse
        if (tx >= btnRegistrarseX && tx <= btnRegistrarseX+BTN_W && ty >= btnRegistrarseY && ty <= btnRegistrarseY+BTN_H) {
            registrarse();
            return;
        }

        // Iniciar sesión
        if (tx >= btnIniciarSesionX && tx <= btnIniciarSesionX+BTN_W && ty >= btnIniciarSesionY && ty <= btnIniciarSesionY+BTN_H) {
            iniciarSesion();
            return;
        }
    }

    private void registrarse() {
        if (nombre.isEmpty() || nombre.trim().isEmpty()) {
            mensaje = "Ingresa un nombre para continuar";
            return;
        }
        if (nombre.length() < 3) {
            mensaje = "El nombre debe tener al menos 3 caracteres";
            return;
        }

        if (GameState.db != null) {
            java.util.List<RegistroJugador> todos = GameState.db.getTopJugadores(Integer.MAX_VALUE);
            for (RegistroJugador r : todos) {
                if (r.nombre.equalsIgnoreCase(nombre)) {
                    mensaje = "El nombre '" + nombre + "' ya existe. Usa 'Iniciar sesión' o elige otro.";
                    return;
                }
            }
        }

        long id = GameState.guardarJugadorEnBD(nombre, fotoBytes);
        if (id < 0) {
            mensaje = "Error al crear el usuario. Intenta de nuevo.";
            return;
        }
        mensaje = "✓ Usuario creado: " + nombre;
        GameState.setJugador(id, nombre, fotoBytes);
        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuScreen(game)));
    }

    private void iniciarSesion() {
        if (nombre.isEmpty() || nombre.trim().isEmpty()) {
            mensaje = "Ingresa tu nombre para iniciar sesión";
            return;
        }
        if (nombre.length() < 3) {
            mensaje = "El nombre debe tener al menos 3 caracteres";
            return;
        }

        RegistroJugador usuarioExistente = null;

        if (GameState.db != null) {
            java.util.List<RegistroJugador> todos = GameState.db.getTopJugadores(Integer.MAX_VALUE);
            for (RegistroJugador r : todos) {
                if (r.nombre.equalsIgnoreCase(nombre)) {
                    usuarioExistente = r;
                    break;
                }
            }
        }

        if (usuarioExistente == null) {
            mensaje = "Usuario no encontrado. Usa 'Registrarse' para crear uno nuevo.";
            return;
        }

        mensaje = "✓ Bienvenido, " + nombre + "!";
        GameState.setJugador(usuarioExistente.id, usuarioExistente.nombre, usuarioExistente.foto);

        if (usuarioExistente.foto != null && fotoTextura == null) {
            try {
                Pixmap pm = new Pixmap(usuarioExistente.foto, 0, usuarioExistente.foto.length);
                fotoTextura = new Texture(pm);
                pm.dispose();
                fotoBytes = usuarioExistente.foto;
            } catch (Exception e) {
                Gdx.app.log("LoginScreen", "Error loading saved photo");
            }
        }

        Gdx.app.postRunnable(() -> game.setScreen(new MainMenuScreen(game)));
    }

    // ── Callback del intent (foto recibida desde AndroidLauncher) ────────────
    @Override
    public void onImagePicked(byte[] imageBytes) {
        fotoBytes = imageBytes;
        mensaje   = "";

        if (fotoTextura != null) fotoTextura.dispose();

        Pixmap pm = new Pixmap(imageBytes, 0, imageBytes.length);
        fotoTextura = new Texture(pm);
        pm.dispose();
    }

    @Override public void abrirGaleria() {}
    @Override public void abrirCamara()  {}

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
        fontNormal.dispose();
        fontPequena.dispose();
        texturaTitulo.dispose();
        fondoTextura.dispose();
        botonBaseTextura.dispose();
        if (fotoTextura != null) fotoTextura.dispose();
    }

    @Override public void show()   { game.setCallbackLogin(this); }
    @Override public void hide()   {}
    @Override public void resize(int w, int h) {
        camera.setToOrtho(false, w, h);
        calcularLayout();
    }
    @Override public void pause()  {}
    @Override public void resume() {}
}
