package com.raices.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;

public class BranchManager {

    private World world;
    private ArrayList<Body> ramas;
    private ArrayList<Cuervo> cuervos;
    private float anchoMundo;

    private static final float TIEMPO_INESTABLE = 2f;
    private static final float TIEMPO_REAPARECE = 4f;

    private int indiceInestable;
    private float timerInestable = 0f;
    private float timerReaparece = 0f;
    private boolean sobreInestable = false;
    private boolean ramaEliminada = false;

    private float xInestable, yInestable, anchoInestable;

    private float tiempoTemblor = 0f;
    private float offsetTemblor = 0f;

    private float[] anchosPorIndice;

    public BranchManager(World world, float anchoMundo) {
        this.world = world;
        this.anchoMundo = anchoMundo;
        this.ramas = new ArrayList<>();
        this.cuervos = new ArrayList<>();
        this.indiceInestable = Level1.getRamaInestable();

        crearRamasNivel1();
    }

    private void crearRamasNivel1() {
        float[][] datos = Level1.getRamas(anchoMundo);
        anchosPorIndice = new float[datos.length];

        for (int i = 0; i < datos.length; i++) {
            crearRama(datos[i][0], datos[i][1], datos[i][2]);
            anchosPorIndice[i] = datos[i][2];

            if (i == indiceInestable) {
                xInestable     = datos[i][0];
                yInestable     = datos[i][1];
                anchoInestable = datos[i][2];
            }
        }

        float[] rama4 = datos[3];
        cuervos.add(new Cuervo(world, rama4[0], rama4[1], rama4[2], false));

        float[] rama8 = datos[7];
        cuervos.add(new Cuervo(world, rama8[0], rama8[1], rama8[2], true));
    }

    private void crearRama(float x, float y, float ancho) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(x, y);

        Body rama = world.createBody(def);

        EdgeShape forma = new EdgeShape();
        forma.set(-ancho / 2, 0, ancho / 2, 0);
        rama.createFixture(forma, 0);
        forma.dispose();

        ramas.add(rama);
    }

    public void lumiAterrizo(int indiceRama) {
        if (indiceRama == indiceInestable && !ramaEliminada) {
            sobreInestable = true;
            timerInestable = 0f;
        }
    }

    public void lumiSalto() {
        sobreInestable = false;
        timerInestable = 0f;
    }

    public void actualizar(float delta) {
        if (sobreInestable && !ramaEliminada) {
            timerInestable += delta;

            float progreso   = timerInestable / TIEMPO_INESTABLE;
            float frecuencia = 8f + progreso * 20f;
            float amplitud   = 0.02f + progreso * 0.10f;
            tiempoTemblor   += delta;
            offsetTemblor    = MathUtils.sin(tiempoTemblor * frecuencia * MathUtils.PI2) * amplitud;

            if (timerInestable >= TIEMPO_INESTABLE) {
                eliminarRamaInestable();
            }
        } else {
            offsetTemblor = 0f;
            tiempoTemblor = 0f;
        }

        if (ramaEliminada) {
            timerReaparece += delta;
            if (timerReaparece >= TIEMPO_REAPARECE) {
                recrearRamaInestable();
            }
        }

        for (Cuervo c : cuervos) {
            c.actualizar(delta);
        }
    }

    private void eliminarRamaInestable() {
        if (!ramaEliminada && indiceInestable < ramas.size()) {
            world.destroyBody(ramas.get(indiceInestable));
            ramas.set(indiceInestable, null);
            ramaEliminada  = true;
            sobreInestable = false;
            offsetTemblor  = 0f;
        }
    }

    private void recrearRamaInestable() {
        crearRama(xInestable, yInestable, anchoInestable);

        ramas.set(indiceInestable, ramas.get(ramas.size() - 1));
        ramas.remove(ramas.size() - 1);

        ramaEliminada  = false;
        sobreInestable = false;
        timerInestable = 0f;
        timerReaparece = 0f;
    }

    public float getTimerInestable()  { return timerInestable; }
    public boolean isRamaEliminada()  { return ramaEliminada; }
    public float getOffsetTemblor()   { return offsetTemblor; }
    public int getIndiceInestable()   { return indiceInestable; }

    public int getIndiceRama(Body body) {
        for (int i = 0; i < ramas.size(); i++) {
            if (ramas.get(i) == body) return i;
        }
        return -1;
    }

    public boolean esCuervo(Body body) {
        for (Cuervo c : cuervos) {
            if (c.body == body) return true;
        }
        return false;
    }

    public ArrayList<float[]> getDatosRamas() {
        ArrayList<float[]> datos = new ArrayList<>();
        for (int i = 0; i < ramas.size(); i++) {
            if (ramas.get(i) == null) continue;
            Body b = ramas.get(i);
            datos.add(new float[]{
                b.getPosition().x,
                b.getPosition().y,
                anchosPorIndice[i],
                i
            });
        }
        return datos;
    }

    public void dibujarCuervos(SpriteBatch batch) {
        for (Cuervo c : cuervos) {
            c.dibujar(batch);
        }
    }

    public void dispose() {
        for (Cuervo c : cuervos) {
            c.dispose();
        }
    }
}
