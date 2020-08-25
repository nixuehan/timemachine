package me.nixuehan.demo.capital.service;

import me.nixuehan.api.ICapitalService;
import me.nixuehan.api.IRedService;
import me.nixuehan.api.Tcc;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.rpc.RpcContext;

import java.util.Map;

@Service(version = "0.1")
public class CapitalServiceImpl implements ICapitalService {


    @Reference(version = "0.1")
    private IRedService redService;

    @Override
    @Tcc(confirmMethod = "confirmMomery",cancelMethod = "cancelMomery", async = false)
    public String getMomery() {


        String userKey = RpcContext.getContext().getAttachment("TIMEMACHINE");


//        throw new RuntimeException();

        return redService.getRedMomery();
    }

    public void confirmMomery() {
        System.out.println("capital confirm");
    }



    public void cancelMomery() {
        System.out.println("capital cancel");
    }
}
