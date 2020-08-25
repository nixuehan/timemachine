package me.nixuehan.demo.order.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatasourceConfiguration {

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primary() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "tccDataSource")
    @Qualifier("tccDataSource")
    @ConfigurationProperties(prefix = "spring.tcc-datasource")
    public DataSource tcc() {
        return DataSourceBuilder.create().build();
    }
}
