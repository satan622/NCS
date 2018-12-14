package com.monad.noisecontrolsystem.Model;

import java.sql.Time;

/**
 * Created by temp on 2017. 5. 30..
 */

public class TimeModel {
    private int auto_increment_id;
    private int ho;
    private float noise_data;
    private float vibration_data;
    private String create_date;

    public TimeModel() {

    }

    public int getAuto_increment_id() {
        return auto_increment_id;
    }

    public void setAuto_increment_id(int auto_increment_id) {
        this.auto_increment_id = auto_increment_id;
    }

    public int getHo() {
        return ho;
    }

    public void setHo(int ho) {
        this.ho = ho;
    }

    public float getNoise_data() {
        return noise_data;
    }

    public void setNoise_data(float noise_data) {
        this.noise_data = noise_data;
    }

    public float getVibration_data() {
        return vibration_data;
    }

    public void setVibration_data(float vibration_data) {
        this.vibration_data = vibration_data;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }
}
