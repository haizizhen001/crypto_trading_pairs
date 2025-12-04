package com.aquariux.cryptotrading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceResponse {
    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
    private LocalDateTime timestamp;
}
