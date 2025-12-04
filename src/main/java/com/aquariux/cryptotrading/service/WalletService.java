package com.aquariux.cryptotrading.service;

import com.aquariux.cryptotrading.dto.WalletResponse;
import com.aquariux.cryptotrading.entity.Wallet;
import com.aquariux.cryptotrading.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public List<WalletResponse> getUserWalletBalance(String userId) {
        List<Wallet> wallets = walletRepository.findByUserId(userId);
        return wallets.stream()
            .map(wallet -> new WalletResponse(wallet.getCurrency(), wallet.getBalance()))
            .collect(Collectors.toList());
    }
}
