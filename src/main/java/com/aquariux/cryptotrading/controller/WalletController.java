package com.aquariux.cryptotrading.controller;

import com.aquariux.cryptotrading.dto.WalletResponse;
import com.aquariux.cryptotrading.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponse>> getUserWalletBalance(@PathVariable String userId) {
        List<WalletResponse> wallets = walletService.getUserWalletBalance(userId);
        return ResponseEntity.ok(wallets);
    }
}
