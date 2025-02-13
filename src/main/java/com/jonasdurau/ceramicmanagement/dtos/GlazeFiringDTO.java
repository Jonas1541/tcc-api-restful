package com.jonasdurau.ceramicmanagement.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public class GlazeFiringDTO {
    
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;

    @Positive(message = "A temperatura deve ser positiva")
    private double temperature;

    @Positive(message = "O tempo de queima deve ser positivo")
    private double burnTime;

    @Positive(message = "O tempo de resfriamento deve ser positivo")
    private double coolingTime;

    private long kilnId;

    @NotEmpty(message = "A queima deve ter produtos")
    @Valid
    private List<GlostDTO> glosts = new ArrayList<>();

    private BigDecimal cost;

    public GlazeFiringDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(double burnTime) {
        this.burnTime = burnTime;
    }

    public double getCoolingTime() {
        return coolingTime;
    }

    public void setCoolingTime(double coolingTime) {
        this.coolingTime = coolingTime;
    }

    public long getKilnId() {
        return kilnId;
    }

    public void setKilnId(long kilnId) {
        this.kilnId = kilnId;
    }

    public List<GlostDTO> getGlosts() {
        return glosts;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
