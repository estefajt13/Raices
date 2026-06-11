package com.raices.game;

public class GameState {

    // ── Vidas y nivel ────────────────────────────────────────────────────────
    public static int vidasMaximas = 3;
    public static int vidas        = vidasMaximas;
    public static int nivel        = 1;
    public static int puntaje      = 0;

    // ── Niveles desbloqueados (índice 0 no se usa, 1-4 son los niveles) ──────
    private static boolean[] nivelesDesbloqueados = {false, true, false, false, false};

    // ── Jugador activo ───────────────────────────────────────────────────────
    public static long   jugadorId     = -1;
    public static String nombreJugador = "";
    public static byte[] fotoJugador   = null;   // bytes JPEG para previsualizar

    // ── Bridge a persistencia (lo asigna AndroidLauncher) ───────────────────
    // Se usa la interfaz PlayerRepository definida en core, implementada por Android
    public static PlayerRepository db = null;

    // ────────────────────────────────────────────────────────────────────────
    //  Métodos de jugador
    // ────────────────────────────────────────────────────────────────────────

    public static void setJugador(long id, String nombre, byte[] foto) {
        jugadorId     = id;
        nombreJugador = nombre;
        fotoJugador   = foto;
    }

    /**
     * Guarda o recupera el jugador en SQLite y devuelve su id.
     * Si el bridge no está conectado (ej: desktop) devuelve -1.
     */
    public static long guardarJugadorEnBD(String nombre, byte[] foto) {
        if (db != null) return db.guardarJugador(nombre, foto);
        return -1;
    }

    /**
     * Actualiza el puntaje en BD solo si supera el récord anterior.
     * Se llama desde WinScreen y GameOverScreen.
     */
    public static void guardarPuntajeEnBD() {
        if (db != null && jugadorId >= 0) {
            db.actualizarPuntaje(jugadorId, puntaje);
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    //  Métodos de juego
    // ────────────────────────────────────────────────────────────────────────

    /** Reinicia todo (vidas, puntaje). Se usa al volver al menú principal. */
    public static void reiniciar() {
        vidas  = vidasMaximas;
        puntaje = 0;
    }

    /** Reinicia solo las vidas para el nivel actual. */
    public static void reiniciarNivel() {
        vidas = vidasMaximas;
    }

    public static void perderVida() {
        if (vidas > 0) vidas--;
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

    /**
     * Llama al completar un nivel:
     *  - desbloquea el siguiente
     *  - suma puntaje según vidas restantes
     *  - guarda el récord en BD
     */
    public static void completarNivel(int nivel) {
        if (nivel < 4) desbloquearNivel(nivel + 1);
        puntaje += vidas * 100;
        guardarPuntajeEnBD();   // ← guarda automáticamente al ganar
    }

    public static int getNivelSiguiente(int nivelActual) {
        for (int i = nivelActual + 1; i <= 4; i++) {
            if (esNivelDesbloqueado(i)) return i;
        }
        return -1;
    }
}
