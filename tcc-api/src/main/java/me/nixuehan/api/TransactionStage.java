package me.nixuehan.api;

/**
 * 事务状态
 */
public enum TransactionStage {
    TRYING(1),
    CONFIRMING(2),
    CANCELLING(3),
    SUCCESS(4), //执行了cf
    FAIL(5); //执行了cc

    private int transactionStage;

    TransactionStage(int stage) {
        this.transactionStage = stage;
    }

    public int getValue() {
        return this.transactionStage;
    }

    public static TransactionStage valueOf(int stage) {
        switch (stage) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }
}
