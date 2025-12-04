package com.aquariux.cryptotrading.service;

import com.aquariux.cryptotrading.dto.BinanceTickerResponse;
import com.aquariux.cryptotrading.dto.HuobiTickerResponse;
import com.aquariux.cryptotrading.entity.CryptoPrice;
import com.aquariux.cryptotrading.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class PriceAggregationService {

    private final CryptoPriceRepository cryptoPriceRepository;
    private final RestTemplate restTemplate;

    private static final String BINANCE_API_URL = "https://api.binance.com/api/v3/ticker/bookTicker";
    private static final String HUOBI_API_URL = "https://api.huobi.pro/market/tickers";
    private static final List<String> SUPPORTED_SYMBOLS = Arrays.asList("ETHUSDT", "BTCUSDT");

    // Huobi uses lowercase: ethusdt, btcusdt
    private static final Map<String, String> HUOBI_SYMBOL_MAP = Map.of(
        "ETHUSDT", "ethusdt",
        "BTCUSDT", "btcusdt"
    );

    @Scheduled(fixedRate = 10000) // Run every 10 seconds
    public void aggregatePrices() {
        log.info("Starting price aggregation from Binance and Huobi...");

        for (String symbol : SUPPORTED_SYMBOLS) {
            try {
                // Fetch from both exchanges in parallel and wait for both (like await Promise.all)
                CompletableFuture<PriceData> binanceFuture = CompletableFuture.supplyAsync(() -> fetchBinancePrice(symbol));
                CompletableFuture<PriceData> huobiFuture = CompletableFuture.supplyAsync(() -> fetchHuobiPrice(symbol));

                PriceData binancePrice = binanceFuture.join();  // Block until Binance completes
                PriceData huobiPrice = huobiFuture.join();      // Block until Huobi completes

                BigDecimal bestBid = null;
                BigDecimal bestAsk = null;
                String bidSource = null;
                String askSource = null;

                if (binancePrice != null && huobiPrice != null) {
                    // Compare both exchanges
                    bestBid = binancePrice.bid.compareTo(huobiPrice.bid) > 0 ? binancePrice.bid : huobiPrice.bid;
                    bestAsk = binancePrice.ask.compareTo(huobiPrice.ask) < 0 ? binancePrice.ask : huobiPrice.ask;
                    bidSource = binancePrice.bid.compareTo(huobiPrice.bid) > 0 ? "BINANCE" : "HUOBI";
                    askSource = binancePrice.ask.compareTo(huobiPrice.ask) < 0 ? "BINANCE" : "HUOBI";
                } else if (binancePrice != null) {
                    bestBid = binancePrice.bid;
                    bestAsk = binancePrice.ask;
                    bidSource = askSource = "BINANCE";
                } else if (huobiPrice != null) {
                    bestBid = huobiPrice.bid;
                    bestAsk = huobiPrice.ask;
                    bidSource = askSource = "HUOBI";
                }

                if (bestBid != null && bestAsk != null) {
                    saveBestPrice(symbol, bestBid, bestAsk, bidSource, askSource);
                    log.info("Saved best price for {}: Bid={} ({}), Ask={} ({})",
                        symbol, bestBid, bidSource, bestAsk, askSource);
                }

            } catch (Exception e) {
                log.error("Error aggregating prices for {}: {}", symbol, e.getMessage(), e);
            }
        }

        log.info("Price aggregation completed");
    }

    private PriceData fetchBinancePrice(String symbol) {
        try {
            BinanceTickerResponse[] tickers = restTemplate.getForObject(
                BINANCE_API_URL + "?symbol=" + symbol,
                BinanceTickerResponse[].class
            );

            if (tickers != null && tickers.length > 0) {
                BinanceTickerResponse ticker = tickers[0];
                return new PriceData(
                    new BigDecimal(ticker.getBidPrice()),
                    new BigDecimal(ticker.getAskPrice())
                );
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Binance price for {}: {}", symbol, e.getMessage());
        }
        return null;
    }

    private PriceData fetchHuobiPrice(String symbol) {
        try {
            HuobiTickerResponse response = restTemplate.getForObject(
                HUOBI_API_URL,
                HuobiTickerResponse.class
            );

            if (response != null && "ok".equals(response.getStatus()) && response.getData() != null) {
                String huobiSymbol = HUOBI_SYMBOL_MAP.get(symbol);

                Optional<HuobiTickerResponse.HuobiTicker> ticker = response.getData().stream()
                    .filter(t -> huobiSymbol.equals(t.getSymbol()))
                    .findFirst();

                if (ticker.isPresent() && ticker.get().getBid() != null && ticker.get().getAsk() != null) {
                    return new PriceData(ticker.get().getBid(), ticker.get().getAsk());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch Huobi price for {}: {}", symbol, e.getMessage());
        }
        return null;
    }

    private void saveBestPrice(String symbol, BigDecimal bidPrice, BigDecimal askPrice,
                               String bidSource, String askSource) {
        CryptoPrice cryptoPrice = new CryptoPrice();
        cryptoPrice.setSymbol(symbol);
        cryptoPrice.setBidPrice(bidPrice);
        cryptoPrice.setAskPrice(askPrice);
        cryptoPrice.setSource("BEST (Bid:" + bidSource + ", Ask:" + askSource + ")");
        cryptoPrice.setTimestamp(LocalDateTime.now());

        cryptoPriceRepository.save(cryptoPrice);
    }

    private static class PriceData {
        BigDecimal bid;
        BigDecimal ask;

        PriceData(BigDecimal bid, BigDecimal ask) {
            this.bid = bid;
            this.ask = ask;
        }
    }

    public CryptoPrice getLatestPrice(String symbol) {
        return cryptoPriceRepository.findLatestBySymbol(symbol)
            .orElseThrow(() -> new RuntimeException("No price found for symbol: " + symbol));
    }
}
