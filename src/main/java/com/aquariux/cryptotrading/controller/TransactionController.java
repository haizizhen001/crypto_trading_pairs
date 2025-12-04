package com.aquariux.cryptotrading.controller;

import com.aquariux.cryptotrading.dto.TransactionResponse;
import com.aquariux.cryptotrading.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getUserTransactionHistory(@PathVariable String userId) {
        List<TransactionResponse> transactions = transactionService.getUserTransactionHistory(userId);
        return ResponseEntity.ok(transactions);
    }
}
