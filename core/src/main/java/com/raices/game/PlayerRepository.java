package com.raices.game;

import java.util.List;

/**
 * Contrato para persistencia de jugadores. Implementado por cada plataforma.
 */
public interface PlayerRepository {
    long guardarJugador(String nombre, byte[] foto);
    void actualizarFoto(long id, byte[] foto);
    void actualizarPuntaje(long id, int nuevoPuntaje);
    java.util.List<RegistroJugador> getTopJugadores(int limite);
}

