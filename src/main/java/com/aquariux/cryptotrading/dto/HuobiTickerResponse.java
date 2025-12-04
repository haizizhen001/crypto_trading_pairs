package com.aquariux.cryptotrading.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class HuobiTickerResponse {
    private String status;
    private Long ts;
    private List<HuobiTicker> data;

    @Data
    public static class HuobiTicker {
        private String symbol;
        private BigDecimal bid;  // Best bid price
        private BigDecimal ask;  // Best ask price
        private BigDecimal bidSize;
        private BigDecimal askSize;
    }
}