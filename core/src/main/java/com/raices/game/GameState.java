package com.raices.game;

public class GameState {

    // ── Vidas y nivel ────────────────────────────────────────────────────────
    public static int vidasMaximas = 3;
    public static int vidas        = vidasMaximas;
    public static int nivel        = 1;
    public static int puntaje      = 0;

    // ── Jugador activo ───────────────────────────────────────────────────────
    public static long   jugadorId     = -1;
    public static String nombreJugador = "";
    public static byte[] fotoJugador   = null;

    // ── Bridge a persistencia (lo asigna AndroidLauncher) ───────────────────
    public static PlayerRepository db = null;

    // ────────────────────────────────────────────────────────────────────────
    //  Métodos de jugador
    // ────────────────────────────────────────────────────────────────────────

    public static void setJugador(long id, String nombre, byte[] foto) {
        jugadorId     = id;
        nombreJugador = nombre;
        fotoJugador   = foto;
    }

    public static long guardarJugadorEnBD(String nombre, byte[] foto) {
        if (db != null) return db.guardarJugador(nombre, foto);
        return -1;
    }

    public static void guardarPuntajeEnBD() {
        if (db != null && jugadorId >= 0) {
            db.actualizarPuntaje(jugadorId, puntaje);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Métodos de juego
    // ────────────────────────────────────────────────────────────────────────

    public static void reiniciar() {
        vidas   = vidasMaximas;
        puntaje = 0;
    }

    public static void reiniciarNivel() {
        vidas = vidasMaximas;
    }

    public static void perderVida() {
        if (vidas > 0) vidas--;
    }

    public static boolean sinVidas() {
        return vidas <= 0;
    }

    public static void completarNivel(int nivel) {
        puntaje += vidas * 100;
        guardarPuntajeEnBD();
    }

    // Todos los niveles siempre disponibles
    public static boolean esNivelDesbloqueado(int nivel) {
        return nivel >= 1 && nivel <= 4;
    }

    // Devuelve el siguiente nivel, o -1 si ya es el último
    public static int getNivelSiguiente(int nivelActual) {
        if (nivelActual < 4) return nivelActual + 1;
        return -1;
    }
}
