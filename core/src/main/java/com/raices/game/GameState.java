package com.raices.game;

public class GameState {

    public static int vidas = 3;
    public static int nivel = 1;

    public static void reiniciar() {
        vidas = 3;
        nivel = 1;
    }

    public static void perderVida() {
        vidas--;
    }

    public static boolean sinVidas() {
        return vidas <= 0;
    }
}
