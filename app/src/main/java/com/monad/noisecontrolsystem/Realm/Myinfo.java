package com.monad.noisecontrolsystem.Realm;

import io.realm.RealmObject;


public class Myinfo extends RealmObject {
    private int firtNumber;
    private int lastNumber;

    public int getFirtNumber() {
        return firtNumber;
    }

    public void setFirtNumber(int firtNumber) {
        this.firtNumber = firtNumber;
    }

    public int getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }
}
