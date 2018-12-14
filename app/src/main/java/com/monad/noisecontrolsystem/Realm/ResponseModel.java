package com.monad.noisecontrolsystem.Realm;

/**
 * Created by temp on 2017. 5. 29..
 */

public class ResponseModel {
    private int noise;
    private int vibration_data;
    public ResponseModel() {

    }

    public float getNoise() {
        return noise;
    }

    public void setNoise(int noise) {
        this.noise = noise;
    }

    public float getVibration_data() {
        return vibration_data;
    }

    public void setVibration_data(int vibration_data) {
        this.vibration_data = vibration_data;
    }
}
