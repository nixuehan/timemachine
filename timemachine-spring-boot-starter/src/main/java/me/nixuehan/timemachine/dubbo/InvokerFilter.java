package me.nixuehan.timemachine.dubbo;

import me.nixuehan.tcc.Coordinator;
import me.nixuehan.tcc.TransactionManager;
import me.nixuehan.tcc.resource.DubboResource;
import me.nixuehan.tcc.transaction.Transaction;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

@Activate(order = -110000,group = CommonConstants.CONSUMER)
public class InvokerFilter implements Filter {

    private Coordinator coordinator;

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        TransactionManager transactionManager = coordinator.getTransactionManager();
        Transaction transaction = transactionManager.getTransaction();
        transaction.collect(new DubboResource(invoker.getUrl(),invocation));

        Result result = invoker.invoke(invocation);

        return result;
    }
}
