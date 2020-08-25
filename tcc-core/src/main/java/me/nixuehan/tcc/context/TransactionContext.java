package me.nixuehan.tcc.context;

import me.nixuehan.api.TransactionStage;

/**
 * 事务上下文
 */
public interface TransactionContext {

    String initializeTransactionBundle();
    TransactionContextBundle readBundle();
    TransactionContextBundle saveBundle(TransactionContextBundle bundle);

    default boolean exists() {
        String globalTransactionId = this.readBundle().getGlobalTransactionId();
        return globalTransactionId == null || globalTransactionId.length() == 0 ? false : true;
    }

    default TransactionStage saveTransactionStage(TransactionStage stage) {
        TransactionContextBundle bundle = this.readBundle();
        bundle.setStage(stage);
        this.saveBundle(bundle);
        return stage;
    }

    default TransactionStage getTransactionStage() {
        return this.readBundle().getStage();
    }

    default String saveGlobalTransactionId(String id) {
        TransactionContextBundle bundle = this.readBundle();
        bundle.setGlobalTransactionId(id);
        this.saveBundle(bundle);
        return id;
    }

    default String getGlobalTransactionId() {
        return this.readBundle().getGlobalTransactionId();
    }
}
