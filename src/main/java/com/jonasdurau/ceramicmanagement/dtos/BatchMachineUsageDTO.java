package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class BatchMachineUsageDTO {
    
    @NotNull(message = "O ID da máquina não pode ser nulo.")
    @Positive(message = "O ID da máquina deve ser positivo.")
    private Long machineId;

    private String name;

    @Positive(message = "O tempo de uso deve ser maior que 0.")
    private long usageTimeSeconds;

    private double energyConsumption; 

    public BatchMachineUsageDTO() {
    }

    public Long getMachineId() {
        return machineId;
    }

    public void setMachineId(Long machineId) {
        this.machineId = machineId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUsageTimeSeconds() {
        return usageTimeSeconds;
    }

    public void setUsageTimeSeconds(long usageTimeSeconds) {
        this.usageTimeSeconds = usageTimeSeconds;
    }

    public double getEnergyConsumption() {
        return energyConsumption;
    }

    public void setEnergyConsumption(double energyConsumption) {
        this.energyConsumption = energyConsumption;
    }
}
