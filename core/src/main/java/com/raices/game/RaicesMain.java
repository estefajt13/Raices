package com.raices.game;

import com.badlogic.gdx.Game;

public class RaicesMain extends Game {

    private ImagePickerListener imagePicker;
    private ImagePickerListener callbackLogin;
    private ShareListener shareListener;        // ← nuevo

    public void setImagePicker(ImagePickerListener picker) {
        this.imagePicker = picker;
    }

    public void setCallbackLogin(ImagePickerListener callback) {
        this.callbackLogin = callback;
    }

    public void setShareListener(ShareListener listener) {   // ← nuevo
        this.shareListener = listener;
    }

    public ShareListener getShareListener() {                // ← nuevo
        return shareListener;
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
        setScreen(new LoginScreen(this));
    }
}
