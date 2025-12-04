package com.aquariux.cryptotrading.controller;

import com.aquariux.cryptotrading.dto.PriceResponse;
import com.aquariux.cryptotrading.entity.CryptoPrice;
import com.aquariux.cryptotrading.service.PriceAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceAggregationService priceAggregationService;

    @GetMapping("/{symbol}")
    public ResponseEntity<PriceResponse> getLatestPrice(@PathVariable String symbol) {
        CryptoPrice cryptoPrice = priceAggregationService.getLatestPrice(symbol);
        PriceResponse response = new PriceResponse(
            cryptoPrice.getSymbol(),
            cryptoPrice.getBidPrice(),
            cryptoPrice.getAskPrice(),
            cryptoPrice.getTimestamp()
        );
        return ResponseEntity.ok(response);
    }
}
