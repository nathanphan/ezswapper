package com.example.tokenswapper;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.math.BigDecimal;
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

    @Override
    public Integer call() throws Exception {
        SwapManager swapManager = new SwapManager();
        try {
            System.out.println("Swapping " + amount + " " + fromToken + " for " + toToken + "...");
            String txHash = swapManager.executeSwap(fromToken, toToken, amount);
            
            if (txHash != null) {
                System.out.println("Transaction submitted successfully!");
                System.out.println("Transaction hash: " + txHash);
                System.out.println("View on Etherscan: https://sepolia.etherscan.io/tx/" + txHash);
                return 0;
            }
            return 1;
        } finally {
            swapManager.shutdown();
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TokenSwapperCLI()).execute(args);
        System.exit(exitCode);
    }
}