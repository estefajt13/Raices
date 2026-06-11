package com.raices.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Cuervo {

    public Body body;

    private float xIzq;
    private float xDer;
    private float velocidad;
    private int direccion = 1;

    private float timerEspera = 0f;
    private static final float TIEMPO_ESPERA = 0.6f;
    private boolean esperando = false;

    private Texture sheetCaminando;
    private Texture sheetQuieto;
    private Animation<TextureRegion> animCaminando;
    private Animation<TextureRegion> animQuieto;
    private float tiempoAnim = 0f;

    // Tamaño visual del sprite — NO afecta la física
    private static final float TAMANO = 2.5f;

    // Ajuste vertical del sprite respecto al centro físico
    // Negativo = baja el sprite, positivo = lo sube
    private static final float SPRITE_OFFSET_Y = 0.6f;

    // Radio del círculo de colisión (más grande = hitbox más grande)
    private static final float RADIO_COLISION = 0.6f;

    public Cuervo(World world, float xRama, float yRama, float anchoRama, boolean rapido) {
        this.xIzq      = xRama - anchoRama / 2f + 0.3f;
        this.xDer      = xRama + anchoRama / 2f - 0.3f;
        this.velocidad = rapido ? 2.8f : 1.5f;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(xRama, yRama + 0.5f);

        body = world.createBody(def);

        CircleShape forma = new CircleShape();
        forma.setRadius(RADIO_COLISION);   // ← antes era 0.35f

        FixtureDef fixture = new FixtureDef();
        fixture.shape               = forma;
        fixture.isSensor            = true;
        fixture.filter.categoryBits = 0x0002;
        fixture.filter.maskBits     = 0x0001;

        body.createFixture(fixture);
        body.setUserData("cuervo");
        forma.dispose();

        // Animación caminando — 7 frames, 203x256 cada uno
        sheetCaminando = new Texture("CuervoGuardia_caminando.png");
        TextureRegion[] camArray = new TextureRegion[7];
        for (int i = 0; i < 7; i++) {
            camArray[i] = new TextureRegion(sheetCaminando, i * 203, 0, 203, 256);
        }
        animCaminando = new Animation<>(0.12f, camArray);
        animCaminando.setPlayMode(Animation.PlayMode.LOOP);

        // Animación quieto — 4 frames, 203x256 cada uno
        sheetQuieto = new Texture("CuervoGuardian_quieto.png");
        TextureRegion[] quiArray = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            quiArray[i] = new TextureRegion(sheetQuieto, i * 203, 0, 203, 256);
        }
        animQuieto = new Animation<>(0.20f, quiArray);
        animQuieto.setPlayMode(Animation.PlayMode.LOOP);
    }

    public void actualizar(float delta) {
        tiempoAnim += delta;

        if (esperando) {
            timerEspera += delta;
            body.setLinearVelocity(0, 0);
            if (timerEspera >= TIEMPO_ESPERA) {
                esperando   = false;
                timerEspera = 0f;
                direccion   = -direccion;
            }
            return;
        }

        float x = body.getPosition().x;

        if (x >= xDer && direccion == 1) {
            esperando = true;
            body.setLinearVelocity(0, 0);
            return;
        }
        if (x <= xIzq && direccion == -1) {
            esperando = true;
            body.setLinearVelocity(0, 0);
            return;
        }

        body.setLinearVelocity(velocidad * direccion, 0);
    }

    public void dibujar(SpriteBatch batch) {
        TextureRegion frame = esperando
            ? animQuieto.getKeyFrame(tiempoAnim)
            : animCaminando.getKeyFrame(tiempoAnim);

        float x = body.getPosition().x;
        float y = body.getPosition().y + SPRITE_OFFSET_Y;  // ← offset aplicado aquí

        if (direccion == 1) {
            // Mirando derecha — voltea porque el sprite viene mirando izquierda
            batch.draw(frame,
                x + TAMANO / 2f,
                y - TAMANO / 2f,
                -TAMANO, TAMANO
            );
        } else {
            // Mirando izquierda — dibuja normal
            batch.draw(frame,
                x - TAMANO / 2f,
                y - TAMANO / 2f,
                TAMANO, TAMANO
            );
        }
    }

    public void dispose() {
        sheetCaminando.dispose();
        sheetQuieto.dispose();
    }
}
