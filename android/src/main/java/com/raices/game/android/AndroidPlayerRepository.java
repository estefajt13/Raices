package com.raices.game.android;

import android.content.Context;

import com.raices.game.PlayerRepository;
import com.raices.game.RegistroJugador;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementación Android de PlayerRepository que delega en PlayerDatabase.
 */
public class AndroidPlayerRepository implements PlayerRepository {

	private final PlayerDatabase db;

	public AndroidPlayerRepository(Context context) {
		db = new PlayerDatabase(context);
	}

	@Override
	public long guardarJugador(String nombre, byte[] foto) {
		return db.guardarJugador(nombre, foto);
	}

	@Override
	public void actualizarFoto(long id, byte[] foto) {
		db.actualizarFoto(id, foto);
	}

	@Override
	public void actualizarPuntaje(long id, int nuevoPuntaje) {
		db.actualizarPuntaje(id, nuevoPuntaje);
	}

	@Override
	public List<RegistroJugador> getTopJugadores(int limite) {
		List<PlayerDatabase.RegistroJugador> lista = db.getTopJugadores(limite);
		List<RegistroJugador> out = new ArrayList<>(lista.size());
		for (PlayerDatabase.RegistroJugador r : lista) {
			RegistroJugador rr = new RegistroJugador();
			rr.id = r.id; rr.nombre = r.nombre; rr.foto = r.foto; rr.puntaje = r.puntaje;
			out.add(rr);
		}
		return out;
	}
}


