package com.raices.game;

public class Level4 {

    public static float[][] getRamas(float ancho) {
        float centro  = ancho / 2f;
        float pocoIzq = centro - 2.5f;
        float pocoDer = centro + 2.5f;
        float masIzq  = centro - 3.5f;
        float masDer  = centro + 3.5f;

        return new float[][] {
            { centro,   2f,   4.0f },  // Rama 1  — inicio (pequeña)
            { pocoDer,  5.5f, 3.5f },  // Rama 2  — cuervo 1
            { masIzq,   9f,   3.5f },  // Rama 3  — inestable
            { masDer,   12.5f,4.0f },  // Rama 4  — viento
            { pocoIzq,  16f,  3.5f },  // Rama 5  — cuervo 2
            { pocoDer,  20f,  3.5f },  // Rama 6  — inestable
            { masIzq,   24f,  3.0f },  // Rama 7  — viento fuerte
            { masDer,   27.5f,3.5f },  // Rama 8  — cuervo 3
            { centro,   31f,  3.0f },  // Rama 9  — inestable + salto difícil
            { centro,   35f,  7.0f },  // Rama 10 — final (sin peligro, ancha)
        };
    }

    // Ramas 3, 6 y 9 (índices 2, 5, 8)
    public static int[] getRamasInestables() {
        return new int[] { 2, 5, 8 };
    }

    // Ramas con viento: índices 3 (moderado) y 6 (fuerte)
    public static int[] getRamasConViento() {
        return new int[] { 3, 6 };
    }

    public static float[] getIntensidadViento() {
        return new float[] { 2f, 3f };
    }

    // { indiceRama, velocidad, miraDerecha (1=sí, 0=no) }
    public static float[][] getCuervos() {
        return new float[][] {
            { 1, 2f, 1f },  // Rama 2 — cuervo rápido
            { 4, 2f, 0f },  // Rama 5 — cuervo rápido
            { 7, 2f, 1f },  // Rama 8 — cuervo rápido
        };
    }

    public static float getAlturaFinal() {
        return 37f;
    }
}
