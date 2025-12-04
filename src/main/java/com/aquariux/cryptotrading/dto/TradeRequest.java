package com.aquariux.cryptotrading.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotBlank(message = "Type is required (BUY or SELL)")
    private String type;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.00000001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;
}
