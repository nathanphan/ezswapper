# Java-Based Uniswap Token Swapper

A Java application that demonstrates interaction with Uniswap on the Ethereum Sepolia testnet, allowing users to swap between ETH and ERC-20 tokens.

## Overview

This project showcases practical blockchain development skills using Java and Web3j, implementing a command-line interface for token swapping on Uniswap. It serves as a demonstration of DEX integration and Web3 development capabilities.

## Prerequisites

- Java JDK 17 or higher
- Maven
- An Ethereum wallet with Sepolia testnet ETH
- Infura API key

## Setup Instructions

1. Clone the repository
2. Copy `.env.example` to `.env` and configure:
   - Add your Infura API key
   - Add your wallet's private key
   - Verify contract addresses (defaults are for Sepolia testnet)

### Getting Testnet Resources

1. **Sepolia Testnet ETH**:
   - Visit the Sepolia faucet: https://sepoliafaucet.com
   - Connect your wallet and request test ETH

2. **Infura API Key**:
   - Sign up at https://infura.io
   - Create a new project
   - Copy the project ID (API Key)

## Building the Project

```bash
mvn clean package
```

This will create a JAR file with all dependencies in the `target` directory.

## Running the Application

To swap ETH for tokens:
```bash
java -jar target/token-swapper-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --from ETH \
  --to 0x3e622317f8C93f7328350cF0B56d9eD4C620C5d6 \
  --amount 0.01
```

To swap tokens for ETH:
```bash
java -jar target/token-swapper-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --from 0x3e622317f8C93f7328350cF0B56d9eD4C620C5d6 \
  --to ETH \
  --amount 10
```

## Key Concepts

### Web3j Integration
The project uses Web3j to interact with the Ethereum blockchain. Web3j provides Java libraries for working with smart contracts and sending transactions.

### Uniswap Integration
The application interacts with Uniswap V2 Router contract to perform token swaps. Key operations include:
- ETH to Token swaps using `swapExactETHForTokens`
- Token to ETH swaps using `swapExactTokensForETH`
- Token approvals for ERC-20 tokens

### Security Considerations
- Private keys are loaded from environment variables
- Token approvals are handled securely
- Gas parameters are configurable

## Error Handling

The application handles common scenarios:
- Insufficient balance
- Network connectivity issues
- Invalid token addresses
- Failed transactions

## Future Enhancements

- Support for token-to-token swaps
- Price impact calculations
- Slippage protection
- Gas price optimization
- Support for other DEX protocols

## License

MIT License