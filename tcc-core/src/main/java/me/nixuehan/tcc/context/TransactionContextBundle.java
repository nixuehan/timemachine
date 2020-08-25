package me.nixuehan.tcc.context;

import me.nixuehan.api.TransactionStage;

/**
 * 事务传递
 */
public class TransactionContextBundle {

    /**
     * 全局事务id
     */
    private String globalTransactionId;

    /**
     * 事务当前阶段
     */
    private TransactionStage stage;

    public String getGlobalTransactionId() {
        return globalTransactionId;
    }

    public void setGlobalTransactionId(String globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
    }

    public TransactionStage getStage() {
        return stage;
    }

    public void setStage(TransactionStage stage) {
        this.stage = stage;
    }
}
