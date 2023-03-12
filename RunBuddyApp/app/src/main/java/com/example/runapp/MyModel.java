package com.example.runapp;

public class MyModel {
    String name = "";
    double time = 0;
    double distance = 0;

    public MyModel(String name, double time, double distance) {
        this.name = name;
        this.time = time;
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
