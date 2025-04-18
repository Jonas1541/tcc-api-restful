package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_batch")
public class Batch extends BaseEntity {

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchResourceUsage> resourceUsages = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BatchMachineUsage> machineUsages = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResourceTransaction> resourceTransactions = new ArrayList<>();

    private BigDecimal batchTotalWaterCostAtTime;
    private BigDecimal resourceTotalCostAtTime;
    private BigDecimal machinesEnergyConsumptionCostAtTime;
    private BigDecimal batchFinalCostAtTime;

    public Batch() {
    }

    public double getBatchTotalWater() {
        return resourceUsages.stream()
                .mapToDouble(BatchResourceUsage::getTotalWater)
                .sum();
    }

    public double getResourceTotalQuantity() {
        return resourceUsages.stream()
                .mapToDouble(BatchResourceUsage::getTotalQuantity)
                .sum();
    }

    public BigDecimal getResourceTotalCost() {
        return resourceUsages.stream()
                .map(BatchResourceUsage::getTotalCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double getMachinesEnergyConsumption() {
        return machineUsages.stream()
                .mapToDouble(BatchMachineUsage::getEnergyConsumption)
                .sum();
    }

    public List<BatchResourceUsage> getResourceUsages() {
        return resourceUsages;
    }

    public List<BatchMachineUsage> getMachineUsages() {
        return machineUsages;
    }

    public List<ResourceTransaction> getResourceTransactions() {
        return resourceTransactions;
    }

    public BigDecimal getBatchTotalWaterCostAtTime() {
        return batchTotalWaterCostAtTime;
    }

    public void setBatchTotalWaterCostAtTime(BigDecimal batchTotalWaterCostAtTime) {
        this.batchTotalWaterCostAtTime = batchTotalWaterCostAtTime;
    }

    public BigDecimal getResourceTotalCostAtTime() {
        return resourceTotalCostAtTime;
    }

    public void setResourceTotalCostAtTime(BigDecimal resourceTotalCostAtTime) {
        this.resourceTotalCostAtTime = resourceTotalCostAtTime;
    }

    public BigDecimal getMachinesEnergyConsumptionCostAtTime() {
        return machinesEnergyConsumptionCostAtTime;
    }

    public void setMachinesEnergyConsumptionCostAtTime(BigDecimal machinesEnergyConsumptionCostAtTime) {
        this.machinesEnergyConsumptionCostAtTime = machinesEnergyConsumptionCostAtTime;
    }

    public BigDecimal getBatchFinalCostAtTime() {
        return batchFinalCostAtTime;
    }

    public void setBatchFinalCostAtTime(BigDecimal batchFinalCostAtTime) {
        this.batchFinalCostAtTime = batchFinalCostAtTime;
    }
}
