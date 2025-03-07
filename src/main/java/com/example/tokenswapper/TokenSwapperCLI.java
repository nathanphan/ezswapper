package com.example.tokenswapper;

import io.github.cdimascio.dotenv.Dotenv;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
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
        
        // Get Uniswap router address
        uniswapRouterAddress = dotenv.get("UNISWAP_ROUTER_ADDRESS");

        // TODO: Implement swap logic
        System.out.println("Preparing to swap " + amount + " " + fromToken + " to " + toToken);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new TokenSwapperCLI()).execute(args);
        System.exit(exitCode);
    }
}