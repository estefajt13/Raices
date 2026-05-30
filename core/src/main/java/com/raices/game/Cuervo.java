package com.raices.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Cuervo {

    public Body body;

    // Límites de movimiento sobre la rama
    private float xIzq;
    private float xDer;

    private float velocidad;
    private int direccion = 1; // 1 = derecha, -1 = izquierda

    // Espera en los bordes
    private float timerEspera = 0f;
    private static final float TIEMPO_ESPERA = 0.6f;
    private boolean esperando = false;

    public Cuervo(World world, float xRama, float yRama, float anchoRama, boolean rapido) {
        // El cuervo se mueve entre los bordes de la rama con un pequeño margen
        this.xIzq = xRama - anchoRama / 2f + 0.3f;
        this.xDer = xRama + anchoRama / 2f - 0.3f;

        // Cuervo lento o rápido según el nivel
        this.velocidad = rapido ? 2.8f : 1.5f;

        // Crear el cuerpo físico
        // KinematicBody = se mueve pero no le afecta la gravedad
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.KinematicBody;
        def.position.set(xRama, yRama + 0.5f); // flota justo encima de la rama

        body = world.createBody(def);

        // Forma circular — tamaño similar a Lumi
        CircleShape forma = new CircleShape();
        forma.setRadius(0.35f);

        // isSensor = true significa que detecta contacto pero NO empuja a Lumi
        // Si fuera false, el cuervo empujaría a Lumi físicamente
        FixtureDef fixture = new FixtureDef();
        fixture.shape = forma;
        fixture.isSensor = true;
        fixture.filter.categoryBits = 0x0002; // categoría 2 = cuervo
        fixture.filter.maskBits = 0x0001;     // solo detecta categoría 1 = Lumi

        body.createFixture(fixture);

        // Guardamos "cuervo" como etiqueta para identificarlo en el ContactListener
        body.setUserData("cuervo");

        forma.dispose();
    }

    public void actualizar(float delta) {
        if (esperando) {
            // Está esperando en el borde — no se mueve
            timerEspera += delta;
            body.setLinearVelocity(0, 0);

            if (timerEspera >= TIEMPO_ESPERA) {
                // Terminó de esperar — invierte dirección y camina
                esperando = false;
                timerEspera = 0f;
                direccion = -direccion;
            }
            return;
        }

        float x = body.getPosition().x;

        // Llegó al borde derecho
        if (x >= xDer && direccion == 1) {
            esperando = true;
            body.setLinearVelocity(0, 0);
            return;
        }

        // Llegó al borde izquierdo
        if (x <= xIzq && direccion == -1) {
            esperando = true;
            body.setLinearVelocity(0, 0);
            return;
        }

        // Camina en la dirección actual
        body.setLinearVelocity(velocidad * direccion, 0);
    }
}
