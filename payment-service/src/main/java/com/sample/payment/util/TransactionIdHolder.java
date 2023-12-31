package com.sample.payment.util;

import org.springframework.stereotype.Component;

@Component
public class TransactionIdHolder {

    private ThreadLocal<String> currentTransactionId = new ThreadLocal<>();

    public String getCurrentTransactionId() {
        return currentTransactionId.get();
    }

    public void setCurrentTransactionId(String transactionId) {
        currentTransactionId.set(transactionId);
    }
}
