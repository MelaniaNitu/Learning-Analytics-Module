package com.policat.LA.dtos;

import java.math.BigDecimal;

public class DataPointBarDTO {
    private String label;
    private BigDecimal y;

    public DataPointBarDTO(String label, BigDecimal y) {
        this.label = label;
        this.y = y;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }
}
