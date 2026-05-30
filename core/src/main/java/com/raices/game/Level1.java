package com.raices.game;

public class Level1 {

    public static float[][] getRamas(float ancho) {
        float centro  = ancho / 2f;
        float pocoIzq = centro - 2.5f;
        float pocoDer = centro + 2.5f;

        return new float[][] {
            { centro,   2f,  6.0f },  // Rama 1 — muy ancha, fácil
            { pocoDer,  5f,  5.0f },  // Rama 2
            { pocoIzq,  8f,  5.0f },  // Rama 3
            { centro,   11f, 5.0f },  // Rama 4 — cuervo 1
            { pocoDer,  14f, 4.5f },  // Rama 5
            { pocoIzq,  17f, 4.5f },  // Rama 6
            { centro,   20f, 4.0f },  // Rama 7 — inestable
            { pocoIzq,  23f, 5.0f },  // Rama 8 — cuervo 2
            { pocoDer,  26f, 4.5f },  // Rama 9
            { centro,   30f, 6.0f },  // Rama 10 — final
        };
    }

    public static int getRamaInestable() {
        return 6;
    }

    public static float getAlturaFinal() {
        return 32f;
    }
}
