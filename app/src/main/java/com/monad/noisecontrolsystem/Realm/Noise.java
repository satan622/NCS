package com.monad.noisecontrolsystem.Realm;

import io.realm.RealmObject;

public class Noise extends RealmObject {
    private int vibration;
    private int noise;


    public int getVibration() {
        return vibration;
    }

    public void setVibration(int vibration) {
        this.vibration = vibration;
    }

    public int getNoise() {
        return noise;
    }

    public void setNoise(int noise) {
        this.noise = noise;
    }
}
