package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private float alturaMaxima = 0f;

    private BitmapFont font;
    private SpriteBatch hudBatch;

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
    private float tiempoAnim = 0f;
    private float tiempoSalto = 0f;
    private boolean mirandoDerecha = true;
    private boolean moviendose = false;
    private SpriteBatch gameBatch;

    private float tiempoToque = 0f;
    private boolean tocando = false;
    private static final float LIMITE_TAP = 0.2f;
    private static final float PPM = 64f;

    private float cooldownCuervo = 0f;
    private static final float TIEMPO_COOLDOWN = 2.0f;
    private boolean irAGameOver = false;
    private boolean irAVictoria = false;

    // Timer dedicado para mostrar la animación hurt
    private float tiempoHurt = 0f;
    private static final float DURACION_HURT = 0.8f;

    // Para procesar el golpe fuera del ContactListener
    private boolean golpePendiente = false;
    private float golpeDirX = 0f;

    // Bandera para ir a GameOver DESPUÉS de que termine la animación hurt
    private boolean gameOverPendiente = false;

    public GameScreen(RaicesMain game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false,
            Gdx.graphics.getWidth() / PPM,
            Gdx.graphics.getHeight() / PPM);

        anchoPantallaMetros = Gdx.graphics.getWidth() / PPM;

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                enSuelo = true;

                Body bodyA = contact.getFixtureA().getBody();
                Body bodyB = contact.getFixtureB().getBody();

                Body ramaBody = null;
                if (bodyA == lumiBody) ramaBody = bodyB;
                else if (bodyB == lumiBody) ramaBody = bodyA;

                if (ramaBody != null) {
                    int indice = branchManager.getIndiceRama(ramaBody);
                    if (indice >= 0) {
                        branchManager.lumiAterrizo(indice);
                    }

                    Object userData = ramaBody.getUserData();
                    if ("cuervo".equals(userData) && cooldownCuervo <= 0f) {
                        cooldownCuervo = TIEMPO_COOLDOWN;
                        GameState.perderVida();

                        // Activa animación hurt
                        tiempoHurt = DURACION_HURT;

                        // Guarda dirección para procesar fuera del step
                        golpePendiente = true;
                        float dirX = lumiBody.getPosition().x - ramaBody.getPosition().x;
                        golpeDirX = dirX > 0 ? 3f : -3f;

                        if (GameState.sinVidas()) {
                            // No ir a GameOver todavía, esperar a que termine la animación hurt
                            gameOverPendiente = true;
                        }
                    }
                }
            }

            @Override
            public void endContact(Contact contact) {
                enSuelo = false;
                branchManager.lumiSalto();
            }

            @Override public void preSolve(Contact contact, Manifold manifold) {}
            @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
        });

        crearLumi();
        crearSuelo();
        branchManager = new BranchManager(world, anchoPantallaMetros);

        font = new BitmapFont();
        font.getData().setScale(2f);
        hudBatch = new SpriteBatch();

        lumiSheet = new Texture("lumi_caminando.png");
        TextureRegion[][] frames = TextureRegion.split(lumiSheet,
            lumiSheet.getWidth() / 10,
            lumiSheet.getHeight()
        );
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
            lumiIdleSheet.getHeight()
        );
        TextureRegion[] idleArray = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            idleArray[i] = framesIdle[0][i];
        }
        lumiAnimIdle = new Animation<>(0.20f, idleArray);
        lumiAnimIdle.setPlayMode(Animation.PlayMode.LOOP);

        lumiJumpSheet = new Texture("lumi_salto.png");
        lumiJumpSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        int hJump = lumiJumpSheet.getHeight();

        TextureRegion[] saltoDerArray = new TextureRegion[2];
        saltoDerArray[0] = new TextureRegion(lumiJumpSheet,  26, 0, 276, hJump);
        saltoDerArray[1] = new TextureRegion(lumiJumpSheet, 394, 0, 253, hJump);
        lumiAnimSaltoDer = new Animation<>(0.18f, saltoDerArray);
        lumiAnimSaltoDer.setPlayMode(Animation.PlayMode.NORMAL);

        TextureRegion[] saltoIzqArray = new TextureRegion[2];
        saltoIzqArray[0] = new TextureRegion(saltoDerArray[0]);
        saltoIzqArray[0].flip(true, false);
        saltoIzqArray[1] = new TextureRegion(saltoDerArray[1]);
        saltoIzqArray[1].flip(true, false);
        lumiAnimSaltoIzq = new Animation<>(0.18f, saltoIzqArray);
        lumiAnimSaltoIzq.setPlayMode(Animation.PlayMode.NORMAL);

        lumiHurtTexture = new Texture("lumi_herida.png");
        lumiHurtRegion  = new TextureRegion(lumiHurtTexture);

        gameBatch = new SpriteBatch();
    }

    private void crearLumi() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(anchoPantallaMetros / 2f, 1f);

        lumiBody = world.createBody(def);

        CircleShape forma = new CircleShape();
        forma.setRadius(0.4f);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = forma;
        fixture.density = 1f;
        fixture.friction = 0.4f;
        fixture.restitution = 0f;

        lumiBody.createFixture(fixture);
        forma.dispose();
    }

    private void crearSuelo() {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(0, 0);

        Body suelo = world.createBody(def);

        EdgeShape forma = new EdgeShape();
        forma.set(-100, 0, 100, 0);
        suelo.createFixture(forma, 0);
        forma.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.1f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (cooldownCuervo > 0f) cooldownCuervo -= delta;

        // Bajar el timer de hurt
        if (tiempoHurt > 0f) {
            tiempoHurt -= delta;

            // Cuando termina la animación hurt, recién ahora se procesa el GameOver
            if (tiempoHurt <= 0f && gameOverPendiente) {
                gameOverPendiente = false;
                game.setScreen(new GameOverScreen(game));
                return;
            }
        }

        world.step(delta, 6, 2);
        branchManager.actualizar(delta);

        // Procesar golpe pendiente DESPUÉS del world.step
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

        if (lumiBody.getPosition().y >= Level1.getAlturaFinal() && !irAVictoria) {
            irAVictoria = true;
        }
        if (irAVictoria) {
            game.setScreen(new WinScreen(game));
            return;
        }

        if (lumiBody.getPosition().y > alturaMaxima) {
            alturaMaxima = lumiBody.getPosition().y;
        }

        if (lumiBody.getPosition().y < 1.2f && alturaMaxima > 3f) {
            GameState.perderVida();

            if (GameState.sinVidas()) {
                game.setScreen(new GameOverScreen(game));
                return;
            } else {
                lumiBody.setTransform(anchoPantallaMetros / 2f, 1f, 0);
                lumiBody.setLinearVelocity(0, 0);
                alturaMaxima = 0f;
            }
        }

        camera.position.set(
            anchoPantallaMetros / 2f,
            lumiBody.getPosition().y,
            0
        );
        camera.update();

        alturaMinimaVisible = camera.position.y - (camera.viewportHeight / 2f);

        float touchX = Gdx.input.getX();
        float anchoPantalla = Gdx.graphics.getWidth();

        if (Gdx.input.isTouched()) {
            tiempoToque += delta;
            tocando = true;

            if (tiempoToque > LIMITE_TAP) {
                if (touchX < anchoPantalla / 3f) {
                    mirandoDerecha = false;
                    moviendose = true;
                    lumiBody.setLinearVelocity(-3f, lumiBody.getLinearVelocity().y);
                } else if (touchX > (anchoPantalla / 3f) * 2f) {
                    mirandoDerecha = true;
                    moviendose = true;
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
            tocando = false;

            if (enSuelo) {
                lumiBody.setLinearVelocity(0, lumiBody.getLinearVelocity().y);
            }
        }

        debugRenderer.render(world, camera.combined);

        // --- Animación ---
        tiempoAnim += delta;
        TextureRegion frameActual;

        if (enSuelo) tiempoSalto = 0f;

        if (tiempoHurt > 0f) {
            // Prioridad máxima: mostrar hurt mientras el timer esté activo
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

        float lumiX  = lumiBody.getPosition().x;
        float lumiY  = lumiBody.getPosition().y;
        float tamano = 1.8f;

        gameBatch.setProjectionMatrix(camera.combined);
        gameBatch.begin();
        if (!mirandoDerecha && !moviendose && enSuelo && cooldownCuervo <= 0f) {
            gameBatch.draw(frameActual,
                lumiX + tamano / 2f,
                lumiY - tamano / 2f,
                -tamano,
                tamano
            );
        } else {
            gameBatch.draw(frameActual,
                lumiX - tamano / 2f,
                lumiY - tamano / 2f,
                tamano,
                tamano
            );
        }
        gameBatch.end();

        hudBatch.begin();
        font.draw(hudBatch,
            "Vidas: " + GameState.vidas,
            20,
            Gdx.graphics.getHeight() - 20
        );
        hudBatch.end();
    }

    @Override
    public void dispose() {
        world.dispose();
        debugRenderer.dispose();
        font.dispose();
        hudBatch.dispose();
        lumiSheet.dispose();
        lumiIdleSheet.dispose();
        lumiJumpSheet.dispose();
        lumiHurtTexture.dispose();
        gameBatch.dispose();
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
