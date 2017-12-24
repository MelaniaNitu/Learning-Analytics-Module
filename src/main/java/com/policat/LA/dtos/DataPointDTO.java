package com.policat.LA.dtos;

public class DataPointDTO {
    private int x;
    private double y;
    private String lineDashType;

    public DataPointDTO(int x, double y) {
        this.x = x;
        this.y = y;
    }

    public DataPointDTO(int x, double y, String lineDashType) {
        this.x = x;
        this.y = y;
        this.lineDashType = lineDashType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getLineDashType() {
        return lineDashType;
    }

    public void setLineDashType(String lineDashType) {
        this.lineDashType = lineDashType;
    }
}
