package me.nixuehan.tcc.resource;

import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.context.TransactionContext;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.transaction.ResourceEntity;

import java.util.Date;
import java.util.concurrent.Callable;

public class RemoteResourceRunnable implements Callable {

    private ResourceBundle resourceBundle;

    private TransactionContext transactionContext;

    private TransactionRepository transactionRepository;

    public RemoteResourceRunnable(ResourceBundle resourceBundle, TransactionContext transactionContext, TransactionRepository transactionRepository) {
        this.resourceBundle = resourceBundle;
        this.transactionContext = transactionContext;
        this.transactionRepository = transactionRepository;
    }


    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Object call() throws Exception {

        ResourceEntity resourceEntity = new ResourceEntity();

        if (resourceBundle.getResource().run()) {

            //只有当前状态还confirm的时候 才能删除 resource
            if (transactionContext.getTransactionStage() == TransactionStage.CONFIRMING) {
                resourceEntity.setGlobalTransactionId(resourceBundle.getGlobalTransactionId());
                resourceEntity.setBranchResourceId(resourceBundle.getBranchResourceId());
                transactionRepository.delete(resourceEntity);
            }

        }else {

            resourceEntity.setIsDelete(1);
            resourceEntity.setStage(transactionContext.getTransactionStage().getValue());
            resourceEntity.setLastUpdateTime(new Date());
            resourceEntity.setGlobalTransactionId(resourceBundle.getGlobalTransactionId());
            resourceEntity.setBranchResourceId(resourceBundle.getBranchResourceId());
            transactionRepository.update(resourceEntity);
        }

        return null;
    }
}
