package com.raices.game;

public class Level2 {

    public static float[][] getRamas(float ancho) {
        float centro  = ancho / 2f;
        float pocoIzq = centro - 2.5f;
        float pocoDer = centro + 2.5f;

        return new float[][] {
            { centro,   2f,  5.0f },  // Rama 1  — inicio (más angosta que nivel 1)
            { pocoDer,  5f,  4.5f },  // Rama 2  — normal
            { pocoIzq,  8f,  4.5f },  // Rama 3  — viento leve
            { centro,   11f, 4.5f },  // Rama 4  — cuervo 1
            { pocoDer,  14f, 4.0f },  // Rama 5  — normal
            { pocoIzq,  17f, 4.0f },  // Rama 6  — inestable
            { centro,   20f, 4.0f },  // Rama 7  — viento + salto medio
            { pocoIzq,  23f, 4.5f },  // Rama 8  — cuervo 2
            { pocoDer,  26f, 4.0f },  // Rama 9  — inestable
            { centro,   30f, 6.0f },  // Rama 10 — meta
        };
    }

    // Ramas 6 y 9 (índices 5 y 8)
    public static int[] getRamasInestables() {
        return new int[] { 5, 8 };
    }

    // Ramas con viento: índices 2 (leve) y 6 (leve+salto)
    // Intensidad: 0 = sin viento, 1 = leve, 2 = moderado, 3 = fuerte
    public static int[] getRamasConViento() {
        return new int[] { 2, 6 };
    }

    public static float[] getIntensidadViento() {
        return new float[] { 1f, 1.5f };
    }

    // Cuervos: índices de rama y si van a la derecha
    // { indiceRama, velocidad (1=lento, 2=rápido), miraDerecha (1=sí, 0=no) }
    public static float[][] getCuervos() {
        return new float[][] {
            { 3, 1f, 0f },  // Rama 4 — cuervo lento, mira izquierda
            { 7, 2f, 1f },  // Rama 8 — cuervo rápido, mira derecha
        };
    }

    public static float getAlturaFinal() {
        return 32f;
    }
}
