//package me.nixuehan.timemachine;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
//import me.nixuehan.transaction.Coordinator;
//import me.nixuehan.transaction.invocation.DubboResourceInvocation;
//import me.nixuehan.transaction.invocation.ResourceInvocation;
//import me.nixuehan.transaction.invocation.TccResourceInvocation;
//import me.nixuehan.transaction.Transaction;
//import org.apache.dubbo.common.URL;
//import org.apache.dubbo.common.extension.ExtensionLoader;
//import org.apache.dubbo.rpc.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//
//import java.io.*;
//
//public class DubboInvokerFilter implements Filter {
//
//    @Autowired
//    private ApplicationContext applicationContext;
//
//
//    private Coordinator getCoordinator() {
//        return applicationContext.getBean(Coordinator.class);
//    }
//
//    @Override
//    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//
//        Result result = null;
//
//
//        try {
//            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("object.txt"));
//
//            objectOutputStream.writeObject(invocation);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("object.txt"));
//
//            try {
//                Invocation invocation1 = (Invocation)objectInputStream.readObject();
//
//                URL urls = invoker.getUrl();
//
////                URL url = URL.valueOf("dubbo://192.168.31.114:12347/me.nixuehan.api.ICapitalService?version=0.1");
//
//                URL url = URL.valueOf(urls.getProtocol() + "://" + urls.getHost()+":" + urls.getPort() + "/" + urls.getPath()+"?version=" + urls.getParameter("version"));
//                Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
//
//                System.out.println(invocation.getServiceName());
//                try {
//                    Invoker a = protocol.refer(Class.forName(invocation.getServiceName()), url);
//                    result = a.invoke(invocation1);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//
////        Result result = invoker.invoke(invocation);
//
////        Transaction transaction = this.getCoordinator().getTransactionManager().getTransaction();
////
////        ResourceInvocation dubboResourceInvocation = new DubboResourceInvocation();
////
////        transaction.collect(dubboResourceInvocation);
//
//        return result;
//    }
//}
