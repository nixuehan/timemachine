package me.nixuehan.tcc.transaction;

import me.nixuehan.api.TransactionKinds;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.context.TransactionContext;
import me.nixuehan.tcc.resource.Resource;
import me.nixuehan.tcc.resource.ResourceBundle;
import me.nixuehan.tcc.resource.ResourceManager;
import me.nixuehan.tcc.resource.TccBundle;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractTransaction {
    /**
     * 事务上下文
     */
    protected TransactionContext transactionContext;

    protected TransactionRepository transactionRepository;

    //当前所有资源全部暂存threadlocal
    private ThreadLocal<Deque<Resource>> holder = new ThreadLocal<>();

    AbstractTransaction(
            TransactionContext transactionContext,
            TransactionRepository transactionRepository
    ) {
        //初始化事务
        this.holder.set(new LinkedList<Resource>());
        this.transactionContext = transactionContext;
        this.transactionRepository = transactionRepository;
    }

    protected Deque<Resource> getHolder() {
        return holder.get();
    }

    protected void addTccResource(TccBundle tcc) {
        Deque<Resource> resouces = getHolder();

        resouces.addLast(tcc.getConfirm());
        resouces.addLast(tcc.getCancel());

    }

    protected void addRemoteResource(Resource resource) {

        Deque<Resource> resouces = getHolder();

        resouces.addLast(resource);
    }

    /**
     * 当前事务类型
     * 主事务 or 子事务
     * @return
     */
    public TransactionKinds currentTransactionKinds() {
        return transactionContext.exists() ? TransactionKinds.SUB : TransactionKinds.MAIN;
    }

    public abstract void commit(Object target);

    public abstract void rollback(Object target);

}
