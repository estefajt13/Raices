package com.raices.game;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

    private float tiempoToque = 0f;
    private boolean tocando = false;
    private static final float LIMITE_TAP = 0.2f;
    private static final float PPM = 64f;

    private float cooldownCuervo = 0f;
    private static final float TIEMPO_COOLDOWN = 1.0f;
    private boolean irAGameOver = false;
    private boolean irAVictoria = false;

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
                        lumiBody.setLinearVelocity(
                            lumiBody.getLinearVelocity().x,
                            4f
                        );
                        if (GameState.sinVidas()) {
                            irAGameOver = true;
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

        world.step(delta, 6, 2);
        branchManager.actualizar(delta);

        // Cambios de pantalla fuera del world.step()
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
                    lumiBody.setLinearVelocity(-3f, lumiBody.getLinearVelocity().y);
                } else if (touchX > (anchoPantalla / 3f) * 2f) {
                    lumiBody.setLinearVelocity(3f, lumiBody.getLinearVelocity().y);
                }
            }

        } else {
            if (tocando && tiempoToque <= LIMITE_TAP && enSuelo) {
                if (touchX < anchoPantalla / 3f) {
                    lumiBody.applyLinearImpulse(new Vector2(-2f, 4.5f), lumiBody.getWorldCenter(), true);
                } else if (touchX > (anchoPantalla / 3f) * 2f) {
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
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
