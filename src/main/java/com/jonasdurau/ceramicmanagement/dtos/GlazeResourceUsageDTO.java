package com.jonasdurau.ceramicmanagement.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GlazeResourceUsageDTO {

    @NotNull(message = "O ID do recurso não pode ser nulo.")
    @Positive(message = "O ID do recurso deve ser positivo.")
    private Long resourceId;

    @Positive(message = "A quantidade deve ser positiva.")
    private double quantity;

    public GlazeResourceUsageDTO() {
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
