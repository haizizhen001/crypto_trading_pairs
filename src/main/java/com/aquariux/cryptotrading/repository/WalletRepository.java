package com.aquariux.cryptotrading.repository;

import com.aquariux.cryptotrading.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    List<Wallet> findByUserId(String userId);
    Optional<Wallet> findByUserIdAndCurrency(String userId, String currency);
}
