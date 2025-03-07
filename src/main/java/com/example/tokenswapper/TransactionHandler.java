package com.example.tokenswapper;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class TransactionHandler {
    private final Web3j web3j;
    private final Credentials credentials;
    private final String routerAddress;
    private final String wethAddress;
    private final BigInteger gasLimit;
    private final BigInteger gasPrice;

    public TransactionHandler(Web3j web3j, Credentials credentials, String routerAddress,
                            String wethAddress, BigInteger gasLimit, BigInteger gasPrice) {
        this.web3j = web3j;
        this.credentials = credentials;
        this.routerAddress = routerAddress;
        this.wethAddress = wethAddress;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
    }

    public String swapExactETHForTokens(
            BigDecimal ethAmount,
            String tokenAddress,
            BigInteger deadline
    ) throws Exception {
        BigInteger weiValue = Convert.toWei(ethAmount, Convert.Unit.ETHER).toBigInteger();

        Function function = new Function(
            "swapExactETHForTokens",
            Arrays.<Type>asList(
                new Uint256(BigInteger.ZERO),
                new org.web3j.abi.datatypes.DynamicArray<>(
                    org.web3j.abi.datatypes.Address.class,
                    Arrays.asList(
                        new Address(wethAddress),
                        new Address(tokenAddress)
                    )
                ),
                new Address(credentials.getAddress()),
                new Uint256(deadline)
            ),
            Collections.singletonList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);

        return web3j.ethSendTransaction(
            org.web3j.protocol.core.methods.request.Transaction.createFunctionCallTransaction(
                credentials.getAddress(),
                null,
                gasPrice,
                gasLimit,
                routerAddress,
                weiValue,
                encodedFunction
            )
        ).send().getTransactionHash();
    }

    public String swapExactTokensForETH(
            BigDecimal tokenAmount,
            String tokenAddress,
            BigInteger deadline
    ) throws Exception {
        // First approve the router to spend tokens
        String approvalHash = approveToken(tokenAddress, tokenAmount);
        System.out.println("Approval transaction hash: " + approvalHash);

        BigInteger tokenAmountInWei = Convert.toWei(tokenAmount, Convert.Unit.ETHER).toBigInteger();

        Function function = new Function(
            "swapExactTokensForETH",
            Arrays.<Type>asList(
                new Uint256(tokenAmountInWei),
                new Uint256(BigInteger.ZERO),
                new org.web3j.abi.datatypes.DynamicArray<>(
                    org.web3j.abi.datatypes.Address.class,
                    Arrays.asList(
                        new Address(tokenAddress),
                        new Address(wethAddress)
                    )
                ),
                new Address(credentials.getAddress()),
                new Uint256(deadline)
            ),
            Collections.singletonList(new TypeReference<Uint256>() {})
        );

        String encodedFunction = FunctionEncoder.encode(function);

        return web3j.ethSendTransaction(
            org.web3j.protocol.core.methods.request.Transaction.createFunctionCallTransaction(
                credentials.getAddress(),
                null,
                gasPrice,
                gasLimit,
                routerAddress,
                BigInteger.ZERO,
                encodedFunction
            )
        ).send().getTransactionHash();
    }

    private String approveToken(String tokenAddress, BigDecimal amount) throws Exception {
        BigInteger amountInWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

        Function function = new Function(
            "approve",
            Arrays.asList(
                new Address(routerAddress),
                new Uint256(amountInWei)
            ),
            Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        return web3j.ethSendTransaction(
            org.web3j.protocol.core.methods.request.Transaction.createFunctionCallTransaction(
                credentials.getAddress(),
                null,
                gasPrice,
                gasLimit,
                tokenAddress,
                BigInteger.ZERO,
                encodedFunction
            )
        ).send().getTransactionHash();
    }
}