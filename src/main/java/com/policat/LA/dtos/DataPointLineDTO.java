package com.policat.LA.dtos;

public class DataPointLineDTO {
    private Integer x;
    private double y;

    public DataPointLineDTO(Integer x, double y) {
        this.x = x;
        this.y = y;
    }

    public DataPointLineDTO(Integer x, double y, String lineDashType) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
