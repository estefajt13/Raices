package com.raices.game.android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class PlayerDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME    = "raices.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLA        = "jugadores";
    public static final String COL_ID       = "id";
    public static final String COL_NOMBRE   = "nombre";
    public static final String COL_FOTO     = "foto";      // BLOB
    public static final String COL_PUNTAJE  = "puntaje";

    public PlayerDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLA + " (" +
                COL_ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE  + " TEXT NOT NULL UNIQUE, " +
                COL_FOTO    + " BLOB, " +
                COL_PUNTAJE + " INTEGER DEFAULT 0)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA);
        onCreate(db);
    }

    // ── Crear o actualizar jugador ──────────────────────────────────────────
    public long guardarJugador(String nombre, byte[] foto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NOMBRE, nombre);
        if (foto != null) cv.put(COL_FOTO, foto);
        cv.put(COL_PUNTAJE, 0);

        // INSERT OR IGNORE — no sobreescribe si ya existe
        long id = db.insertWithOnConflict(TABLA, null, cv,
            SQLiteDatabase.CONFLICT_IGNORE);

        // Si ya existía (id == -1), devuelve el id real
        if (id == -1) {
            Cursor c = db.query(TABLA,
                new String[]{COL_ID}, COL_NOMBRE + "=?",
                new String[]{nombre}, null, null, null);
            if (c.moveToFirst()) id = c.getLong(0);
            c.close();
        }
        db.close();
        return id;
    }

    // ── Actualizar foto de un jugador ───────────────────────────────────────
    public void actualizarFoto(long id, byte[] foto) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_FOTO, foto);
        db.update(TABLA, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    // ── Actualizar puntaje solo si es mayor al guardado ────────────────────
    public void actualizarPuntaje(long id, int nuevoPuntaje) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(TABLA,
            new String[]{COL_PUNTAJE}, COL_ID + "=?",
            new String[]{String.valueOf(id)}, null, null, null);
        int puntajeActual = 0;
        if (c.moveToFirst()) puntajeActual = c.getInt(0);
        c.close();

        if (nuevoPuntaje > puntajeActual) {
            ContentValues cv = new ContentValues();
            cv.put(COL_PUNTAJE, nuevoPuntaje);
            db.update(TABLA, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
        }
        db.close();
    }

    // ── Top 10 para la pantalla de puntajes ────────────────────────────────
    public static class RegistroJugador {
        public long   id;
        public String nombre;
        public byte[] foto;
        public int    puntaje;
    }

    public List<RegistroJugador> getTopJugadores(int limite) {
        List<RegistroJugador> lista = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLA,
            new String[]{COL_ID, COL_NOMBRE, COL_FOTO, COL_PUNTAJE},
            null, null, null, null,
            COL_PUNTAJE + " DESC",
            String.valueOf(limite));

        while (c.moveToNext()) {
            RegistroJugador r = new RegistroJugador();
            r.id      = c.getLong(0);
            r.nombre  = c.getString(1);
            r.foto    = c.getBlob(2);
            r.puntaje = c.getInt(3);
            lista.add(r);
        }
        c.close();
        db.close();
        return lista;
    }
}

