package com.jonasdurau.ceramicmanagement.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.jonasdurau.ceramicmanagement.entities.enums.TransactionType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_glaze")
public class Glaze {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String color;
    private BigDecimal unitValue;

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeResourceUsage> resourceUsages = new ArrayList<>();

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeMachineUsage> machineUsages = new ArrayList<>();

    @OneToMany(mappedBy = "glaze", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GlazeTransaction> transactions = new ArrayList<>();

    private BigDecimal unitCost;

    public Glaze() {
    }

    public double getCurrentQuantity() {
        double total = 0.0;
        for (GlazeTransaction tx : transactions) {
            if(tx.getType() == TransactionType.INCOMING) {
                total += tx.getQuantity();
            }
            else {
                total -= tx.getQuantity();
            }
        }
        return total;
    }

    public BigDecimal getCurrentQuantityPrice() {
        return BigDecimal.valueOf(getCurrentQuantity())
                .multiply(this.unitValue)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public BigDecimal getUnitValue() {
        return unitValue;
    }

    public void setUnitValue(BigDecimal unitValue) {
        this.unitValue = unitValue;
    }

    public List<GlazeResourceUsage> getResourceUsages() {
        return resourceUsages;
    }

    public List<GlazeMachineUsage> getMachineUsages() {
        return machineUsages;
    }

    public List<GlazeTransaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Glaze other = (Glaze) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
