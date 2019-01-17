package com.example.gianpaolorusso.trackbus;

import java.io.Serializable;

public class BusClass implements Serializable {
    public double getLongitudine() {
        return longitudine;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public int getN() {
        return n;
    }

    public String getKey() {
        return key;
    }

    double longitudine;
    double latitudine;

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isLog() {
        return log;
    }

    boolean log;

    public String getNota() {
        return nota;
    }

    String nota;
    int n;
    String key;

    public BusClass(double longitudine, double latitudine, int n,String key,String nota) {
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.n = n;
        this.nota=nota;
        this.key=key;

    }
}
