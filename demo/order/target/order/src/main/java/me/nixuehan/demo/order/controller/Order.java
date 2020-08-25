package me.nixuehan.demo.order.controller;

import me.nixuehan.api.ICapitalService;
import me.nixuehan.api.Tcc;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class Order {

    @Reference(version = "0.1")
    private ICapitalService capitalService;

    @ResponseBody
    @GetMapping("/pay")
    @Tcc(confirmMethod = "testConfirm",cancelMethod = "testCancel", async = false)
    public String test() {

        RpcContext.getContext().setAttachment("userKey", "aaaaaaaaaaa");

        String momery = capitalService.getMomery();

        return momery;
    }

    public void testConfirm() {
        System.out.println("Order Confirm");
    }

    public void testCancel() {
        System.out.println("Order Cancel");
    }
}
