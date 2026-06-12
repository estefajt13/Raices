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

    private static final float TIEMPO_INESTABLE = 1.0f;
    private static final float TIEMPO_REAPARECE = 1.5f;

    private int[] indicesInestables;

    private float[] timerInestable;
    private float[] timerReaparece;
    private boolean[] sobreInestable;
    private boolean[] ramaEliminada;

    private float[] xInestable, yInestable, anchoInestable;

    private float tiempoTemblor  = 0f;
    private float offsetTemblor  = 0f;
    private int   indiceTemblando = -1;

    private float[] anchosPorIndice;

    public BranchManager(World world, float anchoMundo) {
        this.world      = world;
        this.anchoMundo = anchoMundo;
        this.ramas      = new ArrayList<>();
        this.cuervos    = new ArrayList<>();

        int nivel = GameState.nivel;

        indicesInestables = getRamasInestablesNivel(nivel);

        float[][] datos = getRamasNivel(nivel, anchoMundo);
        int total = datos.length;

        timerInestable = new float[total];
        timerReaparece = new float[total];
        sobreInestable = new boolean[total];
        ramaEliminada  = new boolean[total];

        xInestable     = new float[total];
        yInestable     = new float[total];
        anchoInestable = new float[total];

        crearRamasNivel(nivel);
    }

    private float[][] getRamasNivel(int nivel, float ancho) {
        switch (nivel) {
            case 2:  return Level2.getRamas(ancho);
            case 3:  return Level3.getRamas(ancho);
            case 4:  return Level4.getRamas(ancho);
            default: return Level1.getRamas(ancho);
        }
    }

    private int[] getRamasInestablesNivel(int nivel) {
        switch (nivel) {
            case 2:  return Level2.getRamasInestables();
            case 3:  return Level3.getRamasInestables();
            case 4:  return Level4.getRamasInestables();
            default: return Level1.getRamasInestables();
        }
    }

    private void crearRamasNivel(int nivel) {
        float[][] datos = getRamasNivel(nivel, anchoMundo);
        anchosPorIndice = new float[datos.length];

        for (int i = 0; i < datos.length; i++) {
            crearRama(datos[i][0], datos[i][1], datos[i][2]);
            anchosPorIndice[i] = datos[i][2];

            for (int idx : indicesInestables) {
                if (i == idx) {
                    xInestable[i]     = datos[i][0];
                    yInestable[i]     = datos[i][1];
                    anchoInestable[i] = datos[i][2];
                }
            }
        }

        // Cuervos por nivel — misma lógica que nivel 1
        switch (nivel) {
            case 1:
                cuervos.add(new Cuervo(world, datos[3][0], datos[3][1], datos[3][2], false));
                cuervos.add(new Cuervo(world, datos[7][0], datos[7][1], datos[7][2], true));
                cuervos.add(new Cuervo(world, datos[8][0], datos[8][1], datos[8][2], false));
                break;
            case 2:
                cuervos.add(new Cuervo(world, datos[3][0], datos[3][1], datos[3][2], false));
                cuervos.add(new Cuervo(world, datos[7][0], datos[7][1], datos[7][2], true));
                break;
            case 3:
                cuervos.add(new Cuervo(world, datos[3][0], datos[3][1], datos[3][2], true));
                cuervos.add(new Cuervo(world, datos[5][0], datos[5][1], datos[5][2], false));
                cuervos.add(new Cuervo(world, datos[8][0], datos[8][1], datos[8][2], true));
                break;
            case 4:
                cuervos.add(new Cuervo(world, datos[1][0], datos[1][1], datos[1][2], false));
                cuervos.add(new Cuervo(world, datos[4][0], datos[4][1], datos[4][2], true));
                cuervos.add(new Cuervo(world, datos[7][0], datos[7][1], datos[7][2], false));
                break;
        }
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
        for (int idx : indicesInestables) {
            if (indiceRama == idx && !ramaEliminada[idx]) {
                sobreInestable[idx] = true;
                timerInestable[idx] = 0f;
                indiceTemblando     = idx;
            }
        }
    }

    public void lumiSalto() {
        for (int i = 0; i < sobreInestable.length; i++) {
            sobreInestable[i] = false;
            timerInestable[i] = 0f;
        }
        indiceTemblando = -1;
    }

    public void actualizar(float delta) {
        for (int idx : indicesInestables) {
            if (sobreInestable[idx] && !ramaEliminada[idx]) {
                timerInestable[idx] += delta;

                float progreso   = timerInestable[idx] / TIEMPO_INESTABLE;
                float frecuencia = 8f + progreso * 20f;
                float amplitud   = 0.02f + progreso * 0.10f;

                tiempoTemblor += delta;
                offsetTemblor = MathUtils.sin(tiempoTemblor * frecuencia * MathUtils.PI2) * amplitud;

                if (timerInestable[idx] >= TIEMPO_INESTABLE) {
                    eliminarRamaInestable(idx);
                }
            } else if (indiceTemblando == idx) {
                offsetTemblor = 0f;
            }

            if (ramaEliminada[idx]) {
                timerReaparece[idx] += delta;
                if (timerReaparece[idx] >= TIEMPO_REAPARECE) {
                    recrearRamaInestable(idx);
                }
            }
        }

        for (Cuervo c : cuervos) {
            c.actualizar(delta);
        }
    }

    private void eliminarRamaInestable(int idx) {
        if (!ramaEliminada[idx] && idx < ramas.size()) {
            world.destroyBody(ramas.get(idx));
            ramas.set(idx, null);
            ramaEliminada[idx]  = true;
            sobreInestable[idx] = false;
        }
    }

    private void recrearRamaInestable(int idx) {
        crearRama(xInestable[idx], yInestable[idx], anchoInestable[idx]);

        ramas.set(idx, ramas.get(ramas.size() - 1));
        ramas.remove(ramas.size() - 1);

        ramaEliminada[idx]  = false;
        sobreInestable[idx] = false;
        timerInestable[idx] = 0f;
        timerReaparece[idx] = 0f;
    }

    public float getOffsetTemblor(int indice) {
        if (indice == indiceTemblando) return offsetTemblor;
        return 0f;
    }

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
