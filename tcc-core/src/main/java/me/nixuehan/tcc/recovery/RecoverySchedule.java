package me.nixuehan.tcc.recovery;

import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.transaction.RecoveryService;
import me.nixuehan.tcc.transaction.ResourceEntity;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时修复
 */
public class RecoverySchedule {

    public static void start(
            TransactionRepository transactionRepository
    ) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("time: " + System.currentTimeMillis());

                 /**
                 * 1、 状态是 try的  就去执行 cc 不断的重试 n次
                 * 2、 如果状态是 cancelling  就不断的去重试执行 cancelling n次
                 * 3、 如果状态是 confirning  就不断去重试执行 canfirning  n次
                 */

                List<ResourceEntity> resources = transactionRepository.findUnmodified();

                RecoveryService recoveryService = new RecoveryService();
                recoveryService.setTransactionRepository(transactionRepository);

                for (ResourceEntity resource : resources) {
                    recoveryService.deploying(resource);
                }
            }
        }, 10000, 10000, TimeUnit.MILLISECONDS);
    }
}
