package me.nixuehan.tcc;

import me.nixuehan.api.TransactionKinds;
import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.context.TransactionContext;
import me.nixuehan.tcc.recovery.RecoverySchedule;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.resource.DubboResource;
import me.nixuehan.tcc.resource.Resource;
import me.nixuehan.tcc.resource.TccResource;
import me.nixuehan.tcc.transaction.Transaction;
import org.aspectj.lang.ProceedingJoinPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

public class TransactionManager {

    private Transaction transaction;

    private TransactionRepository transactionRepository;

    private TransactionContext transactionContext;

    private ExecutorService threadPoolExecutor;

    private ProceedingJoinPoint pjp = null;

    public ProceedingJoinPoint getPjp() {
        return pjp;
    }

    public void setThreadPoolExecutor(ExecutorService threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void setTransactionContext(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    public Object run(ProceedingJoinPoint pjp) throws Throwable {
        transaction = new Transaction(transactionContext,transactionRepository,threadPoolExecutor, pjp);
        return processor(pjp);
    }

    public Object main(ProceedingJoinPoint pjp) throws Throwable {

        Object result = null;

        transactionContext.saveTransactionStage(TransactionStage.TRYING);

        transaction.begin(pjp);

        try {

            result = pjp.proceed();

        }catch (Throwable e) {

            transaction.rollback(pjp.getTarget());

            throw e;
        }

        transaction.commit(pjp.getTarget());

        return result;
    }

    private Object sub(ProceedingJoinPoint pjp) throws Throwable {

        Object result = null;

        //子事务
        TransactionContext transactionContext = transaction.getTransactionContext();

        switch (transactionContext.getTransactionStage()) {
            case TRYING:
                    transaction.begin(pjp);
                    result = pjp.proceed();
                break;
            case CONFIRMING:
                    transaction.distributeResource();
                    transaction.confirm();
                    transaction.callRemote();
                break;
            case CANCELLING:
                    transaction.distributeResource();
                    transaction.cancel();
                    transaction.callRemote();
                break;
        }

        return result;
    }

    public Object processor(ProceedingJoinPoint pjp) throws Throwable {

        transaction.initializeResourceManager();

        return transaction.currentTransactionKinds() == TransactionKinds.MAIN ? main(pjp) : sub(pjp);

    }
}
