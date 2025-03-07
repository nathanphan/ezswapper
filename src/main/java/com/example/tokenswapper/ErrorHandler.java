package com.example.tokenswapper;

public class ErrorHandler {
    public String handleError(Exception e) {
        String errorMessage;

        if (e instanceof UnsupportedOperationException) {
            errorMessage = e.getMessage();
        } else if (e.getMessage().contains("insufficient funds")) {
            errorMessage = "Insufficient funds for the transaction. Please check your balance and gas settings.";
        } else if (e.getMessage().contains("gas required exceeds allowance")) {
            errorMessage = "Gas limit is too low. Please increase the gas limit in your .env file.";
        } else if (e.getMessage().contains("nonce too low")) {
            errorMessage = "Transaction nonce error. Please try again.";
        } else if (e.getMessage().contains("transaction underpriced")) {
            errorMessage = "Gas price is too low. Please increase the gas price in your .env file.";
        } else {
            errorMessage = "An unexpected error occurred: " + e.getMessage();
        }

        System.err.println(errorMessage);
        return null; // Indicating failure
    }
}