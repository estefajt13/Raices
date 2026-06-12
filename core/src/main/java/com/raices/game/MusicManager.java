package com.raices.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {

    private static Music musicaActual = null;
    private static boolean muteado = false;
    private static String archivoActual = "";

    private static final String[] MUSICAS = {
        "",                  // índice 0 no se usa
        "musica_nivel1.ogg", // nivel 1
        "musica_nivel2.ogg", // nivel 2
        "musica_nivel3.ogg", // nivel 3
        "musica_nivel4.ogg"  // nivel 4
    };

    public static void reproducir(int nivel) {
        String archivo = (nivel >= 1 && nivel <= 4) ? MUSICAS[nivel] : MUSICAS[1];

        // Fallback: si el archivo no existe usar música del nivel 1
        if (!Gdx.files.internal(archivo).exists()) {
            archivo = MUSICAS[1];
        }

        // Si ya está sonando la misma no reiniciar
        if (archivo.equals(archivoActual) && musicaActual != null && musicaActual.isPlaying()) return;

        detener();
        archivoActual = archivo;
        musicaActual = Gdx.audio.newMusic(Gdx.files.internal(archivo));
        musicaActual.setLooping(true);
        musicaActual.setVolume(muteado ? 0f : 1f);
        musicaActual.play();
    }

    public static void detener() {
        if (musicaActual != null) {
            musicaActual.stop();
            musicaActual.dispose();
            musicaActual = null;
            archivoActual = "";
        }
    }

    public static void pausar() {
        if (musicaActual != null) musicaActual.pause();
    }

    public static void reanudar() {
        if (musicaActual != null && !muteado) musicaActual.play();
    }

    public static void toggleMute() {
        muteado = !muteado;
        if (musicaActual != null) {
            musicaActual.setVolume(muteado ? 0f : 1f);
        }
    }

    public static boolean isMuteado() { return muteado; }
}
