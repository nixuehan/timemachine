package me.nixuehan.demo.redpacket;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDubbo
@SpringBootApplication
public class RedpacketApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedpacketApplication.class,args);
    }
}
