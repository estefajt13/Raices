package com.raices.game;

import com.badlogic.gdx.Game;

public class RaicesMain extends Game {

    private ImagePickerListener imagePicker;

    // Callback que LoginScreen registra para recibir la foto
    private ImagePickerListener callbackLogin;

    public void setImagePicker(ImagePickerListener picker) {
        this.imagePicker = picker;
    }

    public void setCallbackLogin(ImagePickerListener callback) {
        this.callbackLogin = callback;
    }

    /** Llamado desde AndroidLauncher cuando llega la foto */
    public void onImagePicked(byte[] bytes) {
        if (callbackLogin != null) {
            callbackLogin.onImagePicked(bytes);
        }
    }

    public void abrirGaleria() {
        if (imagePicker != null) imagePicker.abrirGaleria();
    }

    public void abrirCamara() {
        if (imagePicker != null) imagePicker.abrirCamara();
    }

    @Override
    public void create() {
        setScreen(new LoginScreen(this));   // ← arranca en Login
    }
}
