package com.aquariux.cryptotrading.repository;

import com.aquariux.cryptotrading.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {

    @Query("SELECT cp FROM CryptoPrice cp WHERE cp.symbol = :symbol ORDER BY cp.timestamp DESC LIMIT 1")
    Optional<CryptoPrice> findLatestBySymbol(String symbol);
}
