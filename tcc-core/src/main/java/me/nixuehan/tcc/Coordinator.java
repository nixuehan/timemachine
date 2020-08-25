package me.nixuehan.tcc;

import me.nixuehan.tcc.extension.SpringBootApplicationContext;
import me.nixuehan.tcc.recovery.RecoverySchedule;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.resource.TccBundle;
import me.nixuehan.tcc.transaction.ResourceEntity;
import me.nixuehan.tcc.utils.KryoUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationContext;

import java.util.List;

@Aspect
public class Coordinator {

    /**
     * 事务管理器
     */
    private TransactionManager transactionManager;

    private ApplicationContext applicationContext;

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setSpringBootApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getSpringBootApplicationContext() {
        return applicationContext;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void recoverySchedule() {
        TransactionRepository transactionRepository = transactionManager.getTransactionRepository();
        List<ResourceEntity> resources = transactionRepository.findUnmodified();

        RecoverySchedule.start(
                transactionManager.getTransactionRepository()
        );
    }

    @Pointcut("@annotation(me.nixuehan.api.Tcc)")
    public void tccBeInvoked(){}

    @Around("tccBeInvoked()")
    public Object interceptTransaction(ProceedingJoinPoint pjp) throws Throwable {
        return transactionManager.run(pjp);
    }
}
