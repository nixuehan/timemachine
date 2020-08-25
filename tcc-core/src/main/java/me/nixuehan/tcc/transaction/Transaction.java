package me.nixuehan.tcc.transaction;

import me.nixuehan.api.Tcc;
import me.nixuehan.api.TransactionKinds;
import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.context.TransactionContext;
import me.nixuehan.tcc.resource.*;
import me.nixuehan.tcc.utils.Constant;
import me.nixuehan.tcc.utils.KryoUtils;
import me.nixuehan.tcc.utils.TransactionXid;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 主事务
 */
public class Transaction extends AbstractTransaction {

    private String globalTransactionId;

    private String branchTransactionId;

    private TransactionContext transactionContext;

    private TransactionRepository transactionRepository;

    private ResourceManager resourceManager;

    private ExecutorService threadPoolExecutor;

    private ProceedingJoinPoint pjp;

    private boolean async;

    public TransactionContext getTransactionContext() {
        return transactionContext;
    }

    public Transaction(
            TransactionContext transactionContext,
            TransactionRepository transactionRepository,
            ExecutorService threadPoolExecutor,
            ProceedingJoinPoint pjp
    ) {
        super(transactionContext,transactionRepository);
        this.transactionContext = transactionContext;
        this.transactionRepository = transactionRepository;
        this.threadPoolExecutor = threadPoolExecutor;
        this.pjp = pjp;
    }

    private Date currentDate() {
        return new Date();
    }

    private String getGlobalTransactionId() {
        globalTransactionId = transactionContext.getGlobalTransactionId() == null
                    ? TransactionXid.globalTransactionId() : transactionContext.getGlobalTransactionId();
        return globalTransactionId;
    }

    /**
     * 收集dubbo 事物
     * @param resource
     */
    public void collect(Resource resource) {
        if (transactionContext.getTransactionStage() == TransactionStage.TRYING) {

            String globalTransactionId = getGlobalTransactionId();
            branchTransactionId = TransactionXid.branchTransactionId();

            createResource(globalTransactionId,branchTransactionId,resource);

            super.addRemoteResource(resource);
        }
    }

    private void reinitializeTransactionContext(TransactionStage transactionStage) {
        //重置 dubbo rcp context里的信息。因为在这里已丢失
        transactionContext.saveGlobalTransactionId(globalTransactionId);
        transactionContext.saveTransactionStage(transactionStage);
        fetchResource();
    }

    @Override
    public void rollback(Object target) {

        reinitializeTransactionContext(TransactionStage.CANCELLING);

        if (callRemote()) {
            cancel();
        }
    }

    /**
     * 提交事物
     */
    @Override
    public void commit(Object target) {

        reinitializeTransactionContext(TransactionStage.CONFIRMING);

        //远程事务成功 在执行 本地的 confirm
        if (callRemote()) {
            confirm();
        }
    }

    private void createResource(String globalTransactionId, String branchTransactionId, Resource resource) {
        ResourceEntity entity = new ResourceEntity();

        Date now = currentDate();

        entity.setGlobalTransactionId(globalTransactionId);
        entity.setBranchResourceId(branchTransactionId);
        entity.setContent(KryoUtils.serializer(resource));
        entity.setCreateTime(now);
        entity.setIsDelete(Constant.FALSE);
        entity.setLastUpdateTime(now);
        entity.setStage(TransactionStage.TRYING.getValue());
        entity.setRetriedCount(0);
        entity.setVersion(1);
        entity.setModule("resource");

        resourceManager.createResource(entity);
    }

    /**
     * 事务开始
     */
    public void begin(ProceedingJoinPoint pjp) {

        //初始化事务上下文
        if (currentTransactionKinds() == TransactionKinds.MAIN) {
            transactionContext.saveGlobalTransactionId(getGlobalTransactionId());
        }

        //获取 cf  cc  存数据库
        TccBundle tccBundle = getTccResource(pjp);

        initializeTransactionOptions(pjp);

        //初始化一个事务
        initializeTransaction(tccBundle);
    }


    private void initializeTransactionOptions(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        Method method = signature.getMethod();
        Tcc tccAnnotation = method.getAnnotation(Tcc.class);

        //默认 是
        async = tccAnnotation.async();
    }

    /**
     * 构造 confirm  cancel
     * @param pjp
     * @return
     */
    public TccBundle getTccResource(ProceedingJoinPoint pjp) {

        MethodSignature signature = (MethodSignature) pjp.getSignature();

        Method method = signature.getMethod();

        Tcc annotation = method.getAnnotation(Tcc.class);

        String cancelMethod = annotation.cancelMethod();
        String confirmMethod = annotation.confirmMethod();

        Resource confirmResource = new TccResource(method.getParameterTypes(),confirmMethod,pjp.getTarget().getClass(),pjp.getArgs(),"confirm");

        Resource cancelResource = new TccResource(method.getParameterTypes(),cancelMethod,pjp.getTarget().getClass(),pjp.getArgs(),"cancel");

        TccBundle tccBundle = new TccBundle(confirmResource, cancelResource);

        addTccResource(tccBundle);

        return tccBundle;
    }

    /**
     * 全局事务初始化
     */
    private void initializeTransaction(TccBundle tccResource) {

        ResourceEntity entity = new ResourceEntity();

        Date now = currentDate();

        entity.setGlobalTransactionId(getGlobalTransactionId());
        entity.setBranchResourceId("");
        entity.setContent(KryoUtils.serializer(tccResource));
        entity.setCreateTime(now);
        entity.setIsDelete(Constant.FALSE);
        entity.setLastUpdateTime(now);
        entity.setStage(TransactionStage.TRYING.getValue());
        entity.setModule("main");

        resourceManager.createResource(entity);
    }

    /**
     * initialize resourceManager
     */
    public void initializeResourceManager() {
        resourceManager = new ResourceManager(transactionRepository);
    }

    public void distributeResource() {
        resourceManager.distributeResource(getGlobalTransactionId());
    }

    public void fetchResource() {
        resourceManager.distributeResource(globalTransactionId, branchTransactionId, getHolder());
    }

    public boolean confirm() {
        return resourceManager.confirmResource(globalTransactionId);
    }

    public boolean cancel() {
        return resourceManager.cancelResource(globalTransactionId);
    }

    public boolean callRemote() {

        if (async) {

            List<Future> futures = new ArrayList<>();
            List<ResourceBundle> remoteResource = resourceManager.getRemoteResource();

            for (ResourceBundle resourceBundle : remoteResource) {
                futures.add(threadPoolExecutor.submit(new RemoteResourceRunnable(
                        resourceBundle,
                        transactionContext,
                        transactionRepository
                )));
            }

            for (Future future : futures) {
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            return true;

        }else{
            return resourceManager.callRemoteResource(transactionContext);
        }
    }
}