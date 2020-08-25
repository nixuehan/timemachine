package me.nixuehan.tcc.utils;

import java.util.UUID;

public class TransactionXid {

    public static String globalTransactionId() {
        return getTransactionId();
    }

    public static String branchTransactionId() {
        return getTransactionId();
    }

    private static String getTransactionId() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
