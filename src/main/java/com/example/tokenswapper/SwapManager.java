package com.example.tokenswapper;

import io.github.cdimascio.dotenv.Dotenv;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SwapManager {
    private final Web3j web3j;
    private final Credentials credentials;
    private final TransactionHandler transactionHandler;
    private final ErrorHandler errorHandler;

    public SwapManager() {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        
        // Initialize Web3j
        String infuraUrl = String.format("https://sepolia.infura.io/v3/%s", 
            dotenv.get("INFURA_API_KEY"));
        this.web3j = Web3j.build(new HttpService(infuraUrl));

        // Load credentials
        this.credentials = Credentials.create(dotenv.get("WALLET_PRIVATE_KEY"));
        
        // Initialize handlers
        String uniswapRouterAddress = dotenv.get("UNISWAP_ROUTER_ADDRESS");
        String wethAddress = dotenv.get("WETH_ADDRESS");
        BigInteger gasLimit = BigInteger.valueOf(Long.parseLong(dotenv.get("GAS_LIMIT")));
        BigInteger gasPrice = BigInteger.valueOf(
            Long.parseLong(dotenv.get("GAS_PRICE_GWEI")) * 1_000_000_000L
        );

        this.transactionHandler = new TransactionHandler(
            web3j,
            credentials,
            uniswapRouterAddress,
            wethAddress,
            gasLimit,
            gasPrice
        );
        this.errorHandler = new ErrorHandler();
    }

    public String executeSwap(String fromToken, String toToken, BigDecimal amount) {
        try {
            // Calculate deadline (30 minutes from now)
            BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 1800);

            // Execute the swap based on token type
            if ("ETH".equalsIgnoreCase(fromToken)) {
                return transactionHandler.swapExactETHForTokens(amount, toToken, deadline);
            } else if ("ETH".equalsIgnoreCase(toToken)) {
                return transactionHandler.swapExactTokensForETH(amount, fromToken, deadline);
            } else {
                throw new UnsupportedOperationException("Currently only ETH to Token or Token to ETH swaps are supported");
            }
        } catch (Exception e) {
            return errorHandler.handleError(e);
        }
    }

    public void shutdown() {
        web3j.shutdown();
    }
}