package me.nixuehan.demo.redpacket.service;

import me.nixuehan.api.IRedService;
import me.nixuehan.api.Tcc;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "0.1")
public class RedServiceImpl implements IRedService {

    @Override
    @Tcc(confirmMethod = "confirmMomery",cancelMethod = "cancelMomery", async = false)
    public String getRedMomery() {

        throw new RuntimeException("redpacket exception");

//        return "red 50$";
    }

    public void confirmMomery() {
        System.out.println("red confirm");
    }

    public void cancelMomery() {
        System.out.println("red cancel");
    }
}
