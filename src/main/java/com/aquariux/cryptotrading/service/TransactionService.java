package com.aquariux.cryptotrading.service;

import com.aquariux.cryptotrading.dto.TransactionResponse;
import com.aquariux.cryptotrading.entity.Transaction;
import com.aquariux.cryptotrading.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionResponse> getUserTransactionHistory(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByTimestampDesc(userId);
        return transactions.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getSymbol(),
            transaction.getType().name(),
            transaction.getQuantity(),
            transaction.getPrice(),
            transaction.getTotalAmount(),
            transaction.getTimestamp()
        );
    }
}
