package com.raices.game.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.raices.game.ImagePickerListener;
import com.raices.game.RaicesMain;
import com.raices.game.GameState;
import com.raices.game.PlayerRepository;
import com.raices.game.android.AndroidPlayerRepository;
import com.badlogic.gdx.Gdx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class AndroidLauncher extends AndroidApplication implements ImagePickerListener {

    private static final int REQUEST_GALERIA = 101;
    private static final int REQUEST_CAMARA  = 102;

    private RaicesMain raicesMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        raicesMain = new RaicesMain();
        raicesMain.setImagePicker(this);   // ← le pasamos el puente

        // Asignar la implementación Android del repositorio a GameState
        GameState.db = new AndroidPlayerRepository(this);

        initialize(raicesMain, configuration);
    }

    // ── Abrir galería ───────────────────────────────────────────────────────
    @Override
    public void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    // ── Abrir cámara ────────────────────────────────────────────────────────
    @Override
    public void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMARA);
    }

    // ── Resultado del intent ────────────────────────────────────────────────
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;

        byte[] bytes = null;

        try {
            if (requestCode == REQUEST_GALERIA) {
                Uri uri = data.getData();
                InputStream stream = getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(stream);
                bytes = bitmapToBytes(bmp);

            } else if (requestCode == REQUEST_CAMARA) {
                Bitmap bmp = (Bitmap) data.getExtras().get("data");
                bytes = bitmapToBytes(bmp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bytes != null) {
            final byte[] finalBytes = bytes;
            // Volvemos al hilo de LibGDX para actualizar la UI
            Gdx.app.postRunnable(() -> raicesMain.onImagePicked(finalBytes));
        }
    }

    private byte[] bitmapToBytes(Bitmap bmp) {
        // Redimensionar a 128x128 para no ocupar mucho en BD
        Bitmap escalado = Bitmap.createScaledBitmap(bmp, 128, 128, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        escalado.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return out.toByteArray();
    }

    @Override
    public void onImagePicked(byte[] imageBytes) {
        // Implementado — el flujo va por onActivityResult → postRunnable
    }
}
