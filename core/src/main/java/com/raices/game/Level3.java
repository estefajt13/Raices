package com.raices.game;

public class Level3 {

    public static float[][] getRamas(float ancho) {
        float centro  = ancho / 2f;
        float pocoIzq = centro - 2.5f;
        float pocoDer = centro + 2.5f;
        float masIzq  = centro - 3.5f;
        float masDer  = centro + 3.5f;

        return new float[][] {
            { centro,   2f,  4.5f },  // Rama 1  — inicio (pequeña)
            { pocoDer,  5.5f,3.5f },  // Rama 2  — inestable
            { pocoIzq,  9f,  4.0f },  // Rama 3  — viento leve
            { masDer,   12f, 4.0f },  // Rama 4  — cuervo 1
            { masIzq,   15f, 3.5f },  // Rama 5  — inestable
            { centro,   18f, 4.0f },  // Rama 6  — cuervo 2
            { pocoDer,  22f, 3.5f },  // Rama 7  — viento fuerte
            { pocoIzq,  26f, 3.5f },  // Rama 8  — inestable
            { masDer,   29f, 4.0f },  // Rama 9  — cuervo (zona difícil)
            { centro,   33f, 6.0f },  // Rama 10 — meta
        };
    }

    // Ramas 2, 5 y 8 (índices 1, 4, 7)
    public static int[] getRamasInestables() {
        return new int[] { 1, 4, 7 };
    }

    // Ramas con viento: índices 2 (leve) y 6 (fuerte)
    public static int[] getRamasConViento() {
        return new int[] { 2, 6 };
    }

    public static float[] getIntensidadViento() {
        return new float[] { 1.5f, 2.5f };
    }

    // { indiceRama, velocidad, miraDerecha (1=sí, 0=no) }
    public static float[][] getCuervos() {
        return new float[][] {
            { 3, 2f, 0f },  // Rama 4 — cuervo rápido
            { 5, 2f, 1f },  // Rama 6 — cuervo rápido
            { 8, 2f, 0f },  // Rama 9 — cuervo zona difícil
        };
    }

    public static float getAlturaFinal() {
        return 35f;
    }
}
