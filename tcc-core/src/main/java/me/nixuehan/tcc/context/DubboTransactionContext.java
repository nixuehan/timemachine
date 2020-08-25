package me.nixuehan.tcc.context;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.rpc.RpcContext;

/**
 * dubbo transaction context
 */
public class DubboTransactionContext implements TransactionContext {

    private final String TRANSACTION_CONTEXT_KEY = "TIMEMACHINE";

    @Override
    public TransactionContextBundle readBundle() {

        String bundleContent = RpcContext.getContext().getAttachment(TRANSACTION_CONTEXT_KEY);

        if (bundleContent == null) {
            bundleContent = this.initializeTransactionBundle();
        }

        return JSON.parseObject(bundleContent, TransactionContextBundle.class);
    }

    @Override
    public TransactionContextBundle saveBundle(TransactionContextBundle bundle) {
        RpcContext.getContext().setAttachment(TRANSACTION_CONTEXT_KEY,JSON.toJSONString(bundle));
        return bundle;
    }

    @Override
    public String initializeTransactionBundle() {
        String bundleContent = JSON.toJSONString(new TransactionContextBundle());
        RpcContext.getContext().setAttachment(TRANSACTION_CONTEXT_KEY,bundleContent);
        return bundleContent;
    }
}
