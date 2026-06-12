package com.raices.game.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.raices.game.GameState;
import com.raices.game.ImagePickerListener;
import com.raices.game.RaicesMain;
import com.raices.game.ShareListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

public class AndroidLauncher extends AndroidApplication
    implements ImagePickerListener, ShareListener {

    private static final int REQUEST_GALERIA = 101;
    private static final int REQUEST_CAMARA  = 102;

    private RaicesMain raicesMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;

        raicesMain = new RaicesMain();
        raicesMain.setImagePicker(this);
        raicesMain.setShareListener(this);   // ← nuevo

        GameState.db = new AndroidPlayerRepository(this);
        initialize(raicesMain, configuration);
    }

    // ── Compartir captura ───────────────────────────────────────────────────
    @Override
    public void compartirPerfil(String rutaArchivo) {
        runOnUiThread(() -> {
            try {
                File archivo = new File(rutaArchivo);
                if (!archivo.exists()) return;

                // FileProvider convierte la ruta interna a una URI segura
                Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivo
                );

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/png");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.putExtra(Intent.EXTRA_TEXT, "¡Mira mi perfil en Raíces! 🌳");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "Compartir perfil con..."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ── Galería / Cámara (sin cambios) ─────────────────────────────────────
    @Override
    public void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_GALERIA);
    }

    @Override
    public void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMARA);
    }

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
            Gdx.app.postRunnable(() -> raicesMain.onImagePicked(finalBytes));
        }
    }

    private byte[] bitmapToBytes(Bitmap bmp) {
        Bitmap escalado = Bitmap.createScaledBitmap(bmp, 128, 128, true);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        escalado.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return out.toByteArray();
    }

    @Override
    public void onImagePicked(byte[] imageBytes) {}
}
