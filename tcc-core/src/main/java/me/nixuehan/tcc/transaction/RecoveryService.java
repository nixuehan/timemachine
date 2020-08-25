package me.nixuehan.tcc.transaction;

import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.resource.TccBundle;
import me.nixuehan.tcc.utils.KryoUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 修复
 */
public class RecoveryService {

    private static final Logger logger = LoggerFactory.getLogger(RecoveryService.class);

    private TransactionRepository transactionRepository;

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void deploying(ResourceEntity resource) {

        resource.setRetriedCount(resource.getRetriedCount()+1);

        TccBundle tccInvocation = getTccInvocation(resource);

        clean();

        switch (TransactionStage.valueOf(resource.getStage())) {
            case TRYING:
                cancelTransaction(resource,tccInvocation);
                break;
            case CONFIRMING:
                confirmTransaction(resource,tccInvocation);
                break;
            case CANCELLING:
                cancelTransaction(resource,tccInvocation);
                break;
        }
    }

    private TccBundle getTccInvocation(ResourceEntity resource) {
        TccBundle tccBundle = KryoUtils.deserializer(resource.getContent(), TccBundle.class);
        return tccBundle;
    }

    private void cancelTransaction(ResourceEntity resource, TccBundle tccBundle) {
        //todo 支持异步
        //只要主事务
        if (StringUtils.isEmpty(resource.getBranchResourceId())) {
            tccBundle.getCancel().run();
            //todo 更新主事务状态
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setGlobalTransactionId(resource.getGlobalTransactionId());
            resourceEntity.setStage(TransactionStage.FAIL.getValue());
            resourceEntity.setLastUpdateTime(new Date());

            if (transactionRepository.update(resourceEntity)) {
                logger.info(String.format("resource(%s) update failed due to record version conflict",resource.getGlobalTransactionId()));
            }
        }
    }

    private void confirmTransaction(ResourceEntity resource, TccBundle tccBundle) {
        //todo 支持异步
        if (StringUtils.isEmpty(resource.getBranchResourceId())) {
            tccBundle.getConfirm().run();
            //todo 更新主事务状态
            ResourceEntity resourceEntity = new ResourceEntity();
            resourceEntity.setGlobalTransactionId(resource.getGlobalTransactionId());
            resourceEntity.setStage(TransactionStage.SUCCESS.getValue());
            resourceEntity.setLastUpdateTime(new Date());

            if (transactionRepository.update(resourceEntity)) {
                logger.info(String.format("resource(%s) update failed due to record version conflict",resource.getGlobalTransactionId()));
            }
        }
    }

    private void clean() {
        //清除子事务记录
        transactionRepository.delete();
    }
}
