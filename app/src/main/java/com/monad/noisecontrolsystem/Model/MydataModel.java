package com.monad.noisecontrolsystem.Model;

/**
 * Created by temp on 2017. 5. 25..
 */

public final class MydataModel {
    private static int room;
    private static float noise;
    private static float vibration_data;

    private static MydataModel data;
    public MydataModel() {

    }
    public static MydataModel getInstance() {
        if (data == null) {
            data = new MydataModel(0f, 0f);
        }
        return data;
    }

    public MydataModel(float noise, float vibration) {
        this.noise = noise;
        this.vibration_data = vibration;
    }

    public static int getRoom() {
        return room;
    }

    public static void setRoom(int room) {
        MydataModel.room = room;
    }

    public float getNoise() {
        return noise;
    }

    public void setNoise(float noise) {
        this.noise = noise;
    }

    public float getVibration() {
        return vibration_data;
    }

    public void setVibration(float vibration) {
        this.vibration_data = vibration;
    }
}
