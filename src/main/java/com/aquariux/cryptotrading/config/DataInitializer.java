package com.aquariux.cryptotrading.config;

import com.aquariux.cryptotrading.entity.User;
import com.aquariux.cryptotrading.entity.Wallet;
import com.aquariux.cryptotrading.repository.UserRepository;
import com.aquariux.cryptotrading.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public void run(String... args) {
        log.info("Initializing default user and wallet...");

        User user = new User();
        user.setId("1fe49452-e932-4f69-9161-d3900ec8cded");
        user.setUsername("default_user");
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        Wallet usdtWallet = new Wallet();
        usdtWallet.setUser(user);
        usdtWallet.setCurrency("USDT");
        usdtWallet.setBalance(new BigDecimal("50000.00000000"));
        usdtWallet.setUpdatedAt(LocalDateTime.now());
        walletRepository.save(usdtWallet);

        log.info("Default user created with ID: {} and initial USDT balance: 50000", user.getId());
    }
}
