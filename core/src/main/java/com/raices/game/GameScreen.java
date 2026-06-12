package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;

public class GameScreen implements Screen {

    private final RaicesMain game;

    private OrthographicCamera camera;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Body lumiBody;
    private BranchManager branchManager;
    private boolean enSuelo = false;
    private float anchoPantallaMetros;
    private float alturaMinimaVisible;

    private float yAlSalirSuperficie = 0f;
    private static final float ALTURA_CAIDA_DANIO = 4.5f;

    private SpriteBatch hudBatch;

    private Texture corazonesTextura;
    private TextureRegion corazonLleno;
    private TextureRegion corazonVacio;
    private static final float CORAZON_SIZE = 60f;
    private static final float CORAZON_PAD  = 6f;

    private Texture musicaTextura;
    private TextureRegion btnMusicaOn;
    private TextureRegion btnMusicaMute;
    private Texture pausaTextura;
    private static final float BTN_HUD_SIZE = 100f;
    private static final float BTN_HUD_PAD  = 12f;
    private Rectangle rectMusica;
    private Rectangle rectPausa;

    private Texture lumiSheet;
    private Texture lumiIdleSheet;
    private Texture lumiJumpSheet;
    private Texture lumiHurtTexture;
    private Animation<TextureRegion> lumiAnimDerecha;
    private Animation<TextureRegion> lumiAnimIzquierda;
    private Animation<TextureRegion> lumiAnimIdle;
    private Animation<TextureRegion> lumiAnimSaltoDer;
    private Animation<TextureRegion> lumiAnimSaltoIzq;
    private TextureRegion lumiHurtRegion;
    private float tiempoAnim  = 0f;
    private float tiempoSalto = 0f;
    private boolean mirandoDerecha = true;
    private boolean moviendose     = false;
    private SpriteBatch gameBatch;

    private Texture fondoTextura;
    private float alturaFondoMetros;
    private static final float FONDO_OFFSET_Y = 14f;

    private Texture ramaTextura;
    private NinePatch ramaPatch;
    private static final float ALTURA_RAMA_VISUAL = 2f;

    private float tiempoToque = 0f;
    private boolean tocando   = false;
    private static final float LIMITE_TAP = 0.2f;
    private static final float PPM        = 64f;
    private static final float LUMI_OFFSET_Y = 0.5f;

    private static final float SUELO_Y      = -0.7f;
    private static final float LUMI_SPAWN_Y = -0.6f;
    private static final float CAIDA_Y      = -0.1f;

    private float cooldownCuervo = 0f;
    private static final float TIEMPO_COOLDOWN = 2.0f;
    private boolean irAGameOver = false;
    private boolean irAVictoria = false;

    private float tiempoHurt = 0f;
    private static final float DURACION_HURT = 0.8f;

    private boolean golpePendiente    = false;
    private float   golpeDirX         = 0f;
    private boolean gameOverPendiente = false;

    // ── Hojas ────────────────────────────────────────────────────────────────
    private Texture hojaTextura;
    private static final int NUM_HOJAS = 30;
    private float[] hojaX, hojaY, hojaVX, hojaVY;
    private float[] hojaRot, hojaRotVel, hojaAncho, hojaAlto, hojaFase;
    private float[][] hojaColor;

    public GameScreen(RaicesMain game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,
            Gdx.graphics.getWidth()  / PPM,
            Gdx.graphics.getHeight() / PPM);

        anchoPantallaMetros = Gdx.graphics.getWidth() / PPM;

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Body ramaBody = null;
                if (bodyA == lumiBody) ramaBody = bodyB;
                else if (bodyB == lumiBody) ramaBody = bodyA;

                if (ramaBody != null) {
                    if ("pared".equals(ramaBody.getUserData())) return;

                    enSuelo = true;

                    int indice = branchManager.getIndiceRama(ramaBody);
                    if (indice >= 0) {
                        branchManager.lumiAterrizo(indice);
                    }

                    Object userData = ramaBody.getUserData();
                    if ("cuervo".equals(userData) && cooldownCuervo <= 0f) {
                        cooldownCuervo = TIEMPO_COOLDOWN;
                        GameState.perderVida();
                        tiempoHurt = DURACION_HURT;

                        golpePendiente = true;
                        float dirX = lumiBody.getPosition().x - ramaBody.getPosition().x;
                        golpeDirX = dirX > 0 ? 3f : -3f;

                        if (GameState.sinVidas()) {
                            gameOverPendiente = true;
                        }
                    }
                } else {
                    enSuelo = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Body otroBody = null;
                if (bodyA == lumiBody) otroBody = bodyB;
                else if (bodyB == lumiBody) otroBody = bodyA;

                if (otroBody != null && "pared".equals(otroBody.getUserData())) return;

                yAlSalirSuperficie = lumiBody.getPosition().y;
                enSuelo = false;
                branchManager.lumiSalto();
            }

            @Override public void preSolve(Contact contact, Manifold manifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        crearLumi();
        crearSuelo();
        crearParedes();
        branchManager = new BranchManager(world, anchoPantallaMetros);

        hudBatch = new SpriteBatch();

        corazonesTextura = new Texture("corazones.png");
        corazonLleno = new TextureRegion(corazonesTextura, 299, 330, 360, 314);
        corazonVacio = new TextureRegion(corazonesTextura, 843, 330, 360, 314);

        musicaTextura = new Texture("botones_musica.png");
        int mitadMusica = musicaTextura.getWidth() / 2;
        btnMusicaOn   = new TextureRegion(musicaTextura, 0, 0, mitadMusica, musicaTextura.getHeight());
        btnMusicaMute = new TextureRegion(musicaTextura, mitadMusica, 0, mitadMusica, musicaTextura.getHeight());
        pausaTextura  = new Texture("boton_pausa.png");

        float screenW = Gdx.graphics.getWidth();
        float screenH = Gdx.graphics.getHeight();
        float margen  = 16f;
        float yBtn    = screenH - BTN_HUD_SIZE - margen;
        rectPausa  = new Rectangle(screenW - BTN_HUD_SIZE - margen,
            yBtn, BTN_HUD_SIZE, BTN_HUD_SIZE);
        rectMusica = new Rectangle(screenW - BTN_HUD_SIZE * 2 - BTN_HUD_PAD - margen,
            yBtn, BTN_HUD_SIZE, BTN_HUD_SIZE);

        MusicManager.reproducir(GameState.nivel);

        lumiSheet = new Texture("lumi_caminando.png");
        TextureRegion[][] frames = TextureRegion.split(lumiSheet,
            lumiSheet.getWidth() / 10,
            lumiSheet.getHeight());
        TextureRegion[] framesDerecha   = new TextureRegion[5];
        TextureRegion[] framesIzquierda = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            framesDerecha[i]   = frames[0][i];
            framesIzquierda[i] = frames[0][i + 5];
        }
        lumiAnimDerecha   = new Animation<>(0.12f, framesDerecha);
        lumiAnimIzquierda = new Animation<>(0.12f, framesIzquierda);
        lumiAnimDerecha.setPlayMode(Animation.PlayMode.LOOP);
        lumiAnimIzquierda.setPlayMode(Animation.PlayMode.LOOP);

        lumiIdleSheet = new Texture("lumibases.png");
        TextureRegion[][] framesIdle = TextureRegion.split(lumiIdleSheet,
            lumiIdleSheet.getWidth() / 7,
            lumiIdleSheet.getHeight());
        TextureRegion[] idleArray = new TextureRegion[7];
        for (int i = 0; i < 7; i++) idleArray[i] = framesIdle[0][i];
        lumiAnimIdle = new Animation<>(0.20f, idleArray);
        lumiAnimIdle.setPlayMode(Animation.PlayMode.LOOP);

        lumiJumpSheet = new Texture("lumi_salto.png");
        lumiJumpSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        int hJump = lumiJumpSheet.getHeight();

        TextureRegion[] saltoDerArray = new TextureRegion[2];
        saltoDerArray[0] = new TextureRegion(lumiJumpSheet, 26,  0, 276, hJump);
        saltoDerArray[1] = new TextureRegion(lumiJumpSheet, 394, 0, 253, hJump);
        lumiAnimSaltoDer = new Animation<>(0.18f, saltoDerArray);
        lumiAnimSaltoDer.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] saltoIzqArray = new TextureRegion[2];
        saltoIzqArray[0] = new TextureRegion(saltoDerArray[0]); saltoIzqArray[0].flip(true, false);
        saltoIzqArray[1] = new TextureRegion(saltoDerArray[1]); saltoIzqArray[1].flip(true, false);
        lumiAnimSaltoIzq = new Animation<>(0.18f, saltoIzqArray);
        lumiAnimSaltoIzq.setPlayMode(Animation.PlayMode.NORMAL);

        lumiHurtTexture = new Texture("lumi_herida.png");
        lumiHurtRegion  = new TextureRegion(lumiHurtTexture);

        // Fondo fijo hasta tener los assets de cada nivel
        fondoTextura = new Texture("fondo_nivel1c.png");
        alturaFondoMetros = getAlturaFinalNivel() * 1.5f + FONDO_OFFSET_Y;

        ramaTextura = new Texture("rama.png");
        ramaPatch   = new NinePatch(ramaTextura, 270, 270, 0, 0);
        ramaPatch.setLeftWidth(0.5f);
        ramaPatch.setRightWidth(0.5f);
        ramaPatch.setTopHeight(0f);
        ramaPatch.setBottomHeight(0f);

        // Hojas decorativas
        Pixmap pm = new Pixmap(16, 8, Pixmap.Format.RGBA8888);
        pm.setColor(0f, 0f, 0f, 0f);
        pm.fill();
        pm.setColor(1f, 1f, 1f, 1f);
        for (int py = 0; py < 8; py++) {
            for (int px = 0; px < 16; px++) {
                float dx = (px - 8f) / 7f;
                float dy = (py - 4f) / 3.5f;
                if (dx * dx + dy * dy <= 1f) pm.drawPixel(px, py);
            }
        }
        hojaTextura = new Texture(pm);
        pm.dispose();

        float[][] colores = {
            {0.90f, 0.45f, 0.10f},
            {0.80f, 0.25f, 0.05f},
            {0.92f, 0.75f, 0.15f},
            {0.65f, 0.30f, 0.08f},
            {0.95f, 0.60f, 0.10f},
        };

        hojaX      = new float[NUM_HOJAS];
        hojaY      = new float[NUM_HOJAS];
        hojaVX     = new float[NUM_HOJAS];
        hojaVY     = new float[NUM_HOJAS];
        hojaRot    = new float[NUM_HOJAS];
        hojaRotVel = new float[NUM_HOJAS];
        hojaAncho  = new float[NUM_HOJAS];
        hojaAlto   = new float[NUM_HOJAS];
        hojaFase   = new float[NUM_HOJAS];
        hojaColor  = new float[NUM_HOJAS][3];

        for (int i = 0; i < NUM_HOJAS; i++) {
            hojaX[i]      = MathUtils.random(0f, anchoPantallaMetros);
            hojaY[i]      = MathUtils.random(0f, getAlturaFinalNivel());
            hojaVX[i]     = MathUtils.random(-0.3f, 0.3f);
            hojaVY[i]     = MathUtils.random(-0.8f, -0.3f);
            hojaRot[i]    = MathUtils.random(0f, 360f);
            hojaRotVel[i] = MathUtils.random(-90f, 90f);
            hojaAncho[i]  = MathUtils.random(0.15f, 0.32f);
            hojaAlto[i]   = hojaAncho[i] * 0.5f;
            hojaFase[i]   = MathUtils.random(0f, MathUtils.PI2);
            float[] c     = colores[MathUtils.random(colores.length - 1)];
            hojaColor[i][0] = c[0];
            hojaColor[i][1] = c[1];
            hojaColor[i][2] = c[2];
        }

        gameBatch = new SpriteBatch();
    }

    private float getAlturaFinalNivel() {
        switch (GameState.nivel) {
            case 2:  return Level2.getAlturaFinal();
            case 3:  return Level3.getAlturaFinal();
            case 4:  return Level4.getAlturaFinal();
            default: return Level1.getAlturaFinal();
        }
    }

    private void crearLumi() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(anchoPantallaMetros / 2f, LUMI_SPAWN_Y);

        lumiBody = world.createBody(def);

        CircleShape forma = new CircleShape();
        forma.setRadius(0.4f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape       = forma;
        fixture.density     = 1f;
        fixture.friction    = 0.4f;
        fixture.restitution = 0f;

        lumiBody.createFixture(fixture);
        forma.dispose();
    }

    private void crearSuelo() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, SUELO_Y);

        Body suelo = world.createBody(def);

        EdgeShape forma = new EdgeShape();
        forma.set(-100, 0, 100, 0);
        suelo.createFixture(forma, 0);
        forma.dispose();
    }

    private void crearParedes() {
        float xIzq  = anchoPantallaMetros / 2f - 5.8f;
        float xDer  = anchoPantallaMetros / 2f + 5.8f;
        float yBase = SUELO_Y;
        float yTop  = 5f;

        BodyDef defIzq = new BodyDef();
        defIzq.type = BodyDef.BodyType.StaticBody;
        Body paredIzq = world.createBody(defIzq);
        paredIzq.setUserData("pared");
        EdgeShape formaIzq = new EdgeShape();
        formaIzq.set(xIzq, yBase, xIzq, yTop);
        FixtureDef fxIzq = new FixtureDef();
        fxIzq.shape       = formaIzq;
        fxIzq.restitution = 0f;
        fxIzq.friction    = 0f;
        paredIzq.createFixture(fxIzq);
        formaIzq.dispose();

        BodyDef defDer = new BodyDef();
        defDer.type = BodyDef.BodyType.StaticBody;
        Body paredDer = world.createBody(defDer);
        paredDer.setUserData("pared");
        EdgeShape formaDer = new EdgeShape();
        formaDer.set(xDer, yBase, xDer, yTop);
        FixtureDef fxDer = new FixtureDef();
        fxDer.shape       = formaDer;
        fxDer.restitution = 0f;
        fxDer.friction    = 0f;
        paredDer.createFixture(fxDer);
        formaDer.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (cooldownCuervo > 0f) cooldownCuervo -= delta;

        if (tiempoHurt > 0f) {
            tiempoHurt -= delta;
            if (tiempoHurt <= 0f && gameOverPendiente) {
                gameOverPendiente = false;
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        world.step(delta, 6, 2);
        branchManager.actualizar(delta);

        if (golpePendiente) {
            golpePendiente = false;
            lumiBody.setTransform(
                lumiBody.getPosition().x + golpeDirX,
                lumiBody.getPosition().y + 0.5f,
                0
            );
            lumiBody.setLinearVelocity(golpeDirX, 5f);
        }

        if (irAGameOver) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        if (lumiBody.getPosition().y >= getAlturaFinalNivel() && !irAVictoria) {
            irAVictoria = true;
        }
        if (irAVictoria) {
            game.setScreen(new WinScreen(game));
            return;
        }

        if (lumiBody.getPosition().y < CAIDA_Y && yAlSalirSuperficie > ALTURA_CAIDA_DANIO) {
            yAlSalirSuperficie = 0f;
            GameState.perderVida();

            if (GameState.sinVidas()) {
                game.setScreen(new GameOverScreen(game));
                return;
            } else {
                lumiBody.setTransform(anchoPantallaMetros / 2f, LUMI_SPAWN_Y, 0);
                lumiBody.setLinearVelocity(0, 0);
                yAlSalirSuperficie = 0f;
            }
        }

        camera.position.set(
            anchoPantallaMetros / 2f,
            lumiBody.getPosition().y,
            0
        );
        camera.update();

        alturaMinimaVisible = camera.position.y - (camera.viewportHeight / 2f);

        float touchX       = Gdx.input.getX();
        float anchoPantalla = Gdx.graphics.getWidth();

        if (Gdx.input.isTouched()) {
            tiempoToque += delta;
            tocando = true;

            if (tiempoToque > LIMITE_TAP) {
                if (touchX < anchoPantalla / 3f) {
                    mirandoDerecha = false;
                    moviendose     = true;
                    lumiBody.setLinearVelocity(-3f, lumiBody.getLinearVelocity().y);
                } else if (touchX > (anchoPantalla / 3f) * 2f) {
                    mirandoDerecha = true;
                    moviendose     = true;
                    lumiBody.setLinearVelocity(3f, lumiBody.getLinearVelocity().y);
                } else {
                    moviendose = false;
                }
            }

        } else {
            moviendose = false;

            if (tocando && tiempoToque <= LIMITE_TAP && enSuelo) {
                if (touchX < anchoPantalla / 3f) {
                    mirandoDerecha = false;
                    lumiBody.applyLinearImpulse(new Vector2(-2f, 4.5f), lumiBody.getWorldCenter(), true);
                } else if (touchX > (anchoPantalla / 3f) * 2f) {
                    mirandoDerecha = true;
                    lumiBody.applyLinearImpulse(new Vector2(2f, 4.5f), lumiBody.getWorldCenter(), true);
                } else {
                    lumiBody.applyLinearImpulse(new Vector2(0, 4.5f), lumiBody.getWorldCenter(), true);
                }
            }

            tiempoToque = 0f;
            tocando     = false;

            if (enSuelo) {
                lumiBody.setLinearVelocity(0, lumiBody.getLinearVelocity().y);
            }
        }

        tiempoAnim += delta;
        TextureRegion frameActual;

        if (enSuelo) tiempoSalto = 0f;

        if (tiempoHurt > 0f) {
            frameActual = lumiHurtRegion;
        } else if (!enSuelo) {
            tiempoSalto += delta;
            Animation<TextureRegion> animSalto = mirandoDerecha ? lumiAnimSaltoDer : lumiAnimSaltoIzq;
            frameActual = animSalto.getKeyFrame(tiempoSalto);
        } else {
            if (moviendose) {
                Animation<TextureRegion> animActual = mirandoDerecha ? lumiAnimDerecha : lumiAnimIzquierda;
                frameActual = animActual.getKeyFrame(tiempoAnim);
            } else {
                frameActual = lumiAnimIdle.getKeyFrame(tiempoAnim);
            }
        }

        float lumiX = lumiBody.getPosition().x;
        float lumiY = lumiBody.getPosition().y;
        float tamano = 1.8f;

        float camTop = camera.position.y + camera.viewportHeight / 2f;
        float camBot = camera.position.y - camera.viewportHeight / 2f;
        for (int i = 0; i < NUM_HOJAS; i++) {
            hojaX[i] += (hojaVX[i] + 0.2f * MathUtils.sin(hojaFase[i])) * delta;
            hojaY[i] += hojaVY[i] * delta;
            hojaRot[i]  += hojaRotVel[i] * delta;
            hojaFase[i] += delta * 1.8f;

            if (hojaY[i] < camBot - 1f) {
                hojaX[i] = MathUtils.random(0f, anchoPantallaMetros);
                hojaY[i] = camTop + MathUtils.random(0.5f, 3f);
            }
            if (hojaX[i] < -0.5f)                      hojaX[i] = anchoPantallaMetros + 0.5f;
            if (hojaX[i] > anchoPantallaMetros + 0.5f)  hojaX[i] = -0.5f;
        }

        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();

        gameBatch.draw(fondoTextura,
            0,
            -FONDO_OFFSET_Y,
            anchoPantallaMetros,
            alturaFondoMetros
        );

        ArrayList<float[]> datosRamas = branchManager.getDatosRamas();
        for (float[] d : datosRamas) {
            int indice  = (int) d[3];
            float offsetX = branchManager.getOffsetTemblor(indice);
            float rx = d[0] - d[2] / 2f + offsetX;
            float ry = d[1] - ALTURA_RAMA_VISUAL / 2f;
            ramaPatch.draw(gameBatch, rx, ry, d[2], ALTURA_RAMA_VISUAL);
        }

        branchManager.dibujarCuervos(gameBatch);

        if (!mirandoDerecha && !moviendose && enSuelo && cooldownCuervo <= 0f) {
            gameBatch.draw(frameActual,
                lumiX + tamano / 2f,
                lumiY - tamano / 2f + LUMI_OFFSET_Y,
                -tamano, tamano
            );
        } else {
            gameBatch.draw(frameActual,
                lumiX - tamano / 2f,
                lumiY - tamano / 2f + LUMI_OFFSET_Y,
                tamano, tamano
            );
        }

        for (int i = 0; i < NUM_HOJAS; i++) {
            gameBatch.setColor(hojaColor[i][0], hojaColor[i][1], hojaColor[i][2], 0.85f);
            gameBatch.draw(hojaTextura,
                hojaX[i] - hojaAncho[i] / 2f,
                hojaY[i] - hojaAlto[i] / 2f,
                hojaAncho[i] / 2f, hojaAlto[i] / 2f,
                hojaAncho[i], hojaAlto[i],
                1f, 1f,
                hojaRot[i],
                0, 0, 16, 8,
                false, false
            );
        }
        gameBatch.setColor(1f, 1f, 1f, 1f);
        gameBatch.end();

        hudBatch.begin();

        int vidasMax    = GameState.vidasMaximas;
        int vidasActual = GameState.vidas;
        float startX = 16f;
        float startY = Gdx.graphics.getHeight() - CORAZON_SIZE - 16f;
        for (int i = 0; i < vidasMax; i++) {
            TextureRegion corazon = (i < vidasActual) ? corazonLleno : corazonVacio;
            hudBatch.draw(corazon,
                startX + i * (CORAZON_SIZE + CORAZON_PAD),
                startY,
                CORAZON_SIZE, CORAZON_SIZE
            );
        }

        TextureRegion iconMusica = MusicManager.isMuteado() ? btnMusicaMute : btnMusicaOn;
        hudBatch.draw(iconMusica,
            rectMusica.x, rectMusica.y, rectMusica.width, rectMusica.height);

        hudBatch.draw(pausaTextura,
            rectPausa.x, rectPausa.y, rectPausa.width, rectPausa.height);

        hudBatch.end();

        if (Gdx.input.justTouched()) {
            float tx = Gdx.input.getX();
            float ty = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (rectMusica.contains(tx, ty)) {
                MusicManager.toggleMute();
            } else if (rectPausa.contains(tx, ty)) {
                game.setScreen(new PauseScreen(game, this));
            }
        }
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        hudBatch.dispose();
        corazonesTextura.dispose();
        musicaTextura.dispose();
        pausaTextura.dispose();
        lumiSheet.dispose();
        lumiIdleSheet.dispose();
        lumiJumpSheet.dispose();
        lumiHurtTexture.dispose();
        fondoTextura.dispose();
        ramaTextura.dispose();
        hojaTextura.dispose();
        branchManager.dispose();
        gameBatch.dispose();
    }

    @Override public void show()   {}
    @Override public void resize(int width, int height) {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
}
