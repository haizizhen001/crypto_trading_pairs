package com.aquariux.cryptotrading.service;

import com.aquariux.cryptotrading.dto.TradeRequest;
import com.aquariux.cryptotrading.dto.TradeResponse;
import com.aquariux.cryptotrading.entity.CryptoPrice;
import com.aquariux.cryptotrading.entity.Transaction;
import com.aquariux.cryptotrading.entity.User;
import com.aquariux.cryptotrading.entity.Wallet;
import com.aquariux.cryptotrading.repository.TransactionRepository;
import com.aquariux.cryptotrading.repository.UserRepository;
import com.aquariux.cryptotrading.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradingService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final PriceAggregationService priceAggregationService;

    @Transactional
    public TradeResponse executeTrade(TradeRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        CryptoPrice latestPrice = priceAggregationService.getLatestPrice(request.getSymbol());

        Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(request.getType().toUpperCase());
        BigDecimal price;
        String cryptoCurrency = extractCryptoCurrency(request.getSymbol());

        if (transactionType == Transaction.TransactionType.BUY) {
            price = latestPrice.getAskPrice();
            return executeBuy(user, request.getSymbol(), cryptoCurrency, request.getQuantity(), price);
        } else {
            price = latestPrice.getBidPrice();
            return executeSell(user, request.getSymbol(), cryptoCurrency, request.getQuantity(), price);
        }
    }

    private TradeResponse executeBuy(User user, String symbol, String cryptoCurrency, BigDecimal quantity, BigDecimal price) {
        BigDecimal totalAmount = quantity.multiply(price);

        Wallet usdtWallet = walletRepository.findByUserIdAndCurrency(user.getId(), "USDT")
            .orElseThrow(() -> new RuntimeException("USDT wallet not found"));

        if (usdtWallet.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("Insufficient USDT balance");
        }

        usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalAmount));
        walletRepository.save(usdtWallet);

        Wallet cryptoWallet = walletRepository.findByUserIdAndCurrency(user.getId(), cryptoCurrency)
            .orElseGet(() -> {
                Wallet newWallet = new Wallet();
                newWallet.setUser(user);
                newWallet.setCurrency(cryptoCurrency);
                newWallet.setBalance(BigDecimal.ZERO);
                return newWallet;
            });

        cryptoWallet.setBalance(cryptoWallet.getBalance().add(quantity));
        walletRepository.save(cryptoWallet);

        Transaction transaction = createTransaction(user, symbol, Transaction.TransactionType.BUY, quantity, price, totalAmount);

        log.info("BUY trade executed: User={}, Symbol={}, Quantity={}, Price={}, Total={}",
            user.getId(), symbol, quantity, price, totalAmount);

        return new TradeResponse(
            transaction.getId(),
            symbol,
            "BUY",
            quantity,
            price,
            totalAmount,
            transaction.getTimestamp(),
            "Trade executed successfully"
        );
    }

    private TradeResponse executeSell(User user, String symbol, String cryptoCurrency, BigDecimal quantity, BigDecimal price) {
        BigDecimal totalAmount = quantity.multiply(price);

        Wallet cryptoWallet = walletRepository.findByUserIdAndCurrency(user.getId(), cryptoCurrency)
            .orElseThrow(() -> new RuntimeException(cryptoCurrency + " wallet not found"));

        if (cryptoWallet.getBalance().compareTo(quantity) < 0) {
            throw new RuntimeException("Insufficient " + cryptoCurrency + " balance");
        }

        cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(quantity));
        walletRepository.save(cryptoWallet);

        Wallet usdtWallet = walletRepository.findByUserIdAndCurrency(user.getId(), "USDT")
            .orElseThrow(() -> new RuntimeException("USDT wallet not found"));

        usdtWallet.setBalance(usdtWallet.getBalance().add(totalAmount));
        walletRepository.save(usdtWallet);

        Transaction transaction = createTransaction(user, symbol, Transaction.TransactionType.SELL, quantity, price, totalAmount);

        log.info("SELL trade executed: User={}, Symbol={}, Quantity={}, Price={}, Total={}",
            user.getId(), symbol, quantity, price, totalAmount);

        return new TradeResponse(
            transaction.getId(),
            symbol,
            "SELL",
            quantity,
            price,
            totalAmount,
            transaction.getTimestamp(),
            "Trade executed successfully"
        );
    }

    private Transaction createTransaction(User user, String symbol, Transaction.TransactionType type,
                                         BigDecimal quantity, BigDecimal price, BigDecimal totalAmount) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSymbol(symbol);
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setTotalAmount(totalAmount);
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    private String extractCryptoCurrency(String symbol) {
        if (symbol.equals("ETHUSDT")) {
            return "ETH";
        } else if (symbol.equals("BTCUSDT")) {
            return "BTC";
        }
        throw new RuntimeException("Unsupported trading pair: " + symbol);
    }
}
