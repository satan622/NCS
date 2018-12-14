package com.monad.noisecontrolsystem.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by temp on 2017. 5. 18..
 */

public class Contributor {
    private int dong;
    private int ho;
    private int noise_data;
    private int vibration_data;

    public Contributor() {

    }

    public float getVibration_data() {
        return vibration_data;
    }

    public void setVibration_data(int vibration_data) {
        this.vibration_data = vibration_data;
    }

    public float getNoise_data() {
        return noise_data;
    }

    public void setNoise_data(int noise_data) {
        this.noise_data = noise_data;
    }

    public int getHo() {
        return ho;
    }

    public void setHo(int ho) {
        this.ho = ho;
    }

    public int getDong() {
        return dong;
    }

    public void setDong(int dong) {
        this.dong = dong;
    }
}