package com.example.tokenswapper;

import io.github.cdimascio.dotenv.Dotenv;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
import java.math.BigInteger;  // Add this import
import java.util.concurrent.Callable;

@Command(
    name = "token-swapper",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "A CLI tool for swapping tokens on Uniswap (Sepolia testnet)"
)
public class TokenSwapperCLI implements Callable<Integer> {

    @Option(names = {"-f", "--from"}, description = "Source token address (use 'ETH' for Ethereum)", required = true)
    private String fromToken;

    @Option(names = {"-t", "--to"}, description = "Destination token address", required = true)
    private String toToken;

    @Option(names = {"-a", "--amount"}, description = "Amount to swap (in source token units)", required = true)
    private BigDecimal amount;

    private Web3j web3j;
    private Credentials credentials;
    private String uniswapRouterAddress;

    @Override
    public Integer call() throws Exception {
        // Load environment variables
        Dotenv dotenv = Dotenv.load();
        
        // Initialize Web3j
        String infuraUrl = String.format("https://sepolia.infura.io/v3/%s", 
            dotenv.get("INFURA_API_KEY"));
        web3j = Web3j.build(new HttpService(infuraUrl));

        // Load credentials
        credentials = Credentials.create(dotenv.get("WALLET_PRIVATE_KEY"));
        
        // Get Uniswap router address and WETH address
        uniswapRouterAddress = dotenv.get("UNISWAP_ROUTER_ADDRESS");
        String wethAddress = dotenv.get("WETH_ADDRESS");

        // Get gas configuration from environment
        BigInteger gasLimit = BigInteger.valueOf(Long.parseLong(dotenv.get("GAS_LIMIT")));
        BigInteger gasPrice = BigInteger.valueOf(
            Long.parseLong(dotenv.get("GAS_PRICE_GWEI")) * 1_000_000_000L // Convert Gwei to Wei
        );

        // Create UniswapService instance
        UniswapService uniswapService = new UniswapService(
            web3j,
            credentials,
            uniswapRouterAddress,
            wethAddress,
            gasLimit,
            gasPrice
        );

        // Calculate deadline (30 minutes from now)
        BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 1800);

        try {
            String txHash;
            if ("ETH".equalsIgnoreCase(fromToken)) {
                System.out.println("Swapping " + amount + " ETH for tokens...");
                txHash = uniswapService.swapExactETHForTokens(amount, toToken, deadline).get();
            } else if ("ETH".equalsIgnoreCase(toToken)) {
                System.out.println("Swapping " + amount + " tokens for ETH...");
                txHash = uniswapService.swapExactTokensForETH(amount, fromToken, deadline).get();
            } else {
                System.err.println("Currently only ETH to Token or Token to ETH swaps are supported");
                return 1;
            }

            System.out.println("Transaction submitted successfully!");
            System.out.println("Transaction hash: " + txHash);
            System.out.println("View on Etherscan: https://sepolia.etherscan.io/tx/" + txHash);
            return 0;
        } catch (Exception e) {
            System.err.println("Error during swap: " + e.getMessage());
            return 1;
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TokenSwapperCLI()).execute(args);
        System.exit(exitCode);
    }
}