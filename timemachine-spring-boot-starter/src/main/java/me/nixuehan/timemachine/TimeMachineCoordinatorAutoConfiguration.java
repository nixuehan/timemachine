package me.nixuehan.timemachine;

import me.nixuehan.tcc.recovery.RecoverySchedule;
import me.nixuehan.tcc.repository.JdbcTransactionRepositoryImpl;
import me.nixuehan.tcc.Coordinator;
import me.nixuehan.tcc.TransactionManager;
import me.nixuehan.tcc.context.TransactionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TimeMachineCoordinatorAutoConfiguration {

    @Autowired
    @Qualifier("tccDataSource")
    private DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TransactionManager transactionManager;

    @Autowired
    private TransactionContext transactionContext;

    @Autowired
    private JdbcTransactionRepositoryImpl transactionRepository;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Bean
    public Coordinator coordinator() {

        Coordinator coordinator = new Coordinator();

        transactionRepository.setDataSource(dataSource);

        transactionRepository.setProjectId(applicationContext.getId());

        transactionManager.setTransactionRepository(transactionRepository);

        transactionManager.setTransactionContext(transactionContext);

        transactionManager.setThreadPoolExecutor(threadPoolExecutor);

        coordinator.setTransactionManager(transactionManager);

        coordinator.setSpringBootApplicationContext(applicationContext);

        coordinator.recoverySchedule();

        return coordinator;
    }
}
