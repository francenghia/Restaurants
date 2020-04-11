package com.example.common;

public class ShipperInfo {
    private String name;
    private String key;
    private Double dist;

    public ShipperInfo(String name, String key, Double dist) {
        this.name = name;
        this.key = key;
        this.dist = dist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Double getDist() {
        return dist;
    }

    public void setDist(Double dist) {
        this.dist = dist;
    }
}
