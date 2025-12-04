package com.aquariux.cryptotrading.controller;

import com.aquariux.cryptotrading.dto.TradeRequest;
import com.aquariux.cryptotrading.dto.TradeResponse;
import com.aquariux.cryptotrading.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    @PostMapping
    public ResponseEntity<TradeResponse> executeTrade(@Valid @RequestBody TradeRequest request) {
        try {
            TradeResponse response = tradingService.executeTrade(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            TradeResponse errorResponse = new TradeResponse();
            errorResponse.setMessage("Trade failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
