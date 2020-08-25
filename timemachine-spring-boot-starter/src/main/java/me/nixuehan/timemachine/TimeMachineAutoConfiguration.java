package me.nixuehan.timemachine;

import me.nixuehan.tcc.repository.JdbcTransactionRepositoryImpl;
import me.nixuehan.tcc.TransactionManager;
import me.nixuehan.tcc.context.DubboTransactionContext;
import me.nixuehan.tcc.context.TransactionContext;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;


@Configuration
@EnableConfigurationProperties(TimeMachineProperties.class)
public class TimeMachineAutoConfiguration {

    @Bean
    public JdbcTransactionRepositoryImpl transactionRepository() {
        return new JdbcTransactionRepositoryImpl();
    }

    @Bean
    public TransactionManager transactionManage() {
        return new TransactionManager();
    }

    @Bean
    public TransactionContext transactionContext() {
        return new DubboTransactionContext();
    }

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(TimeMachineProperties properties) {
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>()
        );
    }
}
