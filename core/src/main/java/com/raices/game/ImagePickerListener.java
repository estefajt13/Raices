package com.raices.game;

public interface ImagePickerListener {
    void onImagePicked(byte[] imageBytes);
    void abrirGaleria();
    void abrirCamara();
}
