package com.aquariux.cryptotrading.dto;

import lombok.Data;

@Data
public class BinanceTickerResponse {
    private String symbol;
    private String bidPrice;
    private String bidQty;
    private String askPrice;
    private String askQty;
}
