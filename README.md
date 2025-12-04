# Crypto Trading System
Supporting ETHUSDT and BTCUSDT trading pairs.

## Features

1. **Price Aggregation**: 10-second interval scheduler fetching prices from Binance API or Houbi API
2. **Trading**: Buy/sell crypto using best aggregated prices
3. **Wallet Management**: View crypto wallet balances
4. **Transaction History**: Track all trading transactions

## Build and Run

### Using Maven

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

## Initial Setup

On startup, the application automatically creates:
- A default user with username "default_user" (ID: 1fe49452-e932-4f69-9161-d3900ec8cded)
- Initial USDT wallet balance: 50,000 USDT

## Testing the Application

### Test Scenario 1: Check Prices
```bash
# Wait for scheduler to run (10 seconds after startup)
curl http://localhost:8080/api/prices/ETHUSDT
curl http://localhost:8080/api/prices/BTCUSDT
```

### Test Scenario 2: Buy Crypto
```bash
# Check initial wallet balance
curl http://localhost:8080/api/wallets/user/1fe49452-e932-4f69-9161-d3900ec8cded

# Buy 0.1 ETH
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -d '{"userId": "1fe49452-e932-4f69-9161-d3900ec8cded", "symbol": "ETHUSDT", "type": "BUY", "quantity": 0.1}'

# Check updated wallet balance
curl http://localhost:8080/api/wallets/user/1fe49452-e932-4f69-9161-d3900ec8cded

# Check transaction history
curl http://localhost:8080/api/transactions/user/1fe49452-e932-4f69-9161-d3900ec8cded
```

### Test Scenario 3: Sell Crypto
```bash
# Sell 0.05 ETH
curl -X POST http://localhost:8080/api/trades \
  -H "Content-Type: application/json" \
  -d '{"userId": "1fe49452-e932-4f69-9161-d3900ec8cded", "symbol": "ETHUSDT", "type": "SELL", "quantity": 0.05}'

# Check updated balances
curl http://localhost:8080/api/wallets/user/1fe49452-e932-4f69-9161-d3900ec8cded
```
## H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:cryptodb`
- Username: `sa`
- Password: (leave blank)

