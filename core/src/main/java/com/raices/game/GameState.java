package com.raices.game;

public class GameState {

    public static int vidas = 3;
    public static int nivel = 1;
    public static int puntaje = 0;

    // Niveles desbloqueados (1 = desbloqueado, 0 = bloqueado)
    // Por defecto, solo el nivel 1 está desbloqueado
    private static boolean[] nivelesDesbloqueados = {false, true, false, false, false};
    // indice 0 no se usa, indices 1-4 son los niveles

    public static void reiniciar() {
        vidas = 3;
        puntaje = 0;
    }

    public static void reiniciarNivel() {
        vidas = 3;
    }

    public static void perderVida() {
        vidas--;
    }

    public static boolean sinVidas() {
        return vidas <= 0;
    }

    public static void desbloquearNivel(int nivel) {
        if (nivel >= 1 && nivel <= 4) {
            nivelesDesbloqueados[nivel] = true;
        }
    }

    public static boolean esNivelDesbloqueado(int nivel) {
        if (nivel >= 1 && nivel <= 4) {
            return nivelesDesbloqueados[nivel];
        }
        return false;
    }

    public static void completarNivel(int nivel) {
        // Desbloquea el siguiente nivel si existe
        if (nivel < 4) {
            desbloquearNivel(nivel + 1);
        }
        // Aumenta el puntaje basado en las vidas restantes
        puntaje += vidas * 100;
    }

    public static int getNivelSiguiente(int nivelActual) {
        for (int i = nivelActual + 1; i <= 4; i++) {
            if (esNivelDesbloqueado(i)) {
                return i;
            }
        }
        return -1; // No hay siguiente nivel
    }
}
