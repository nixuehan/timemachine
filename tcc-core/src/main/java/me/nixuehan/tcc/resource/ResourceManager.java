package me.nixuehan.tcc.resource;

import me.nixuehan.api.TransactionStage;
import me.nixuehan.tcc.context.TransactionContext;
import me.nixuehan.tcc.repository.TransactionRepository;
import me.nixuehan.tcc.transaction.ResourceEntity;
import me.nixuehan.tcc.utils.KryoUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.List;

/**
 * 职员管理器
 */
public class ResourceManager {

    private TransactionRepository transactionRepository;

    private List<ResourceEntity> allResources;

    private Resource confirmResource;

    private Resource cancelResource;

    private List<ResourceBundle> resourceBundleList = new ArrayList<>();

    public ResourceManager(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * 分发所有资源
     * @param globalTransactionId
     * @return
     */
    public void distributeResource(String globalTransactionId) {

        allResources = transactionRepository.find(globalTransactionId);

        for (ResourceEntity resource : allResources) {
            if (resource.getBranchResourceId().equals("")) {

                TccBundle tccResources = KryoUtils.deserializer(resource.getContent(), TccBundle.class);
                confirmResource = tccResources.confirm;
                cancelResource = tccResources.cancel;

            }else{

                Resource rs = KryoUtils.deserializer(resource.getContent(), DubboResource.class);

                ResourceBundle resourceBundle = new ResourceBundle();
                resourceBundle.setGlobalTransactionId(resource.getGlobalTransactionId());
                resourceBundle.setBranchResourceId(resource.getBranchResourceId());
                resourceBundle.setResource(rs);

                resourceBundleList.add(resourceBundle);
            }
        }
    }

    public void distributeResource(String globalTransactionId, String branchTransactionId, Deque<Resource> resources) {

        confirmResource = resources.pollFirst();
        cancelResource = resources.pollFirst();

        List<ResourceBundle> resourceBundles = new ArrayList<>();

        for (Resource resource : resources) {
            ResourceBundle resourceBundle = new ResourceBundle();
            resourceBundle.setGlobalTransactionId(globalTransactionId);
            resourceBundle.setBranchResourceId(branchTransactionId);
            resourceBundle.setResource(resource);

            resourceBundles.add(resourceBundle);
        }

        resourceBundleList = resourceBundles;

    }

    /**
     * 获取cf
     * @return
     */
    public boolean confirmResource(String globalTransactionId) {

        ResourceEntity resourceEntity = new ResourceEntity();

        if (confirmResource.run()) {
            resourceEntity.setIsDelete(1);
            resourceEntity.setStage(TransactionStage.SUCCESS.getValue());
            resourceEntity.setLastUpdateTime(new Date());
            resourceEntity.setGlobalTransactionId(globalTransactionId);
            resourceEntity.setBranchResourceId("");
            updateResource(resourceEntity);

            // 成功标志是：   stage: 4   is_delete 1
            return true;
        }else{
            //更新事务的当前状态
            resourceEntity.setIsDelete(1);
            resourceEntity.setStage(TransactionStage.FAIL.getValue());
            resourceEntity.setLastUpdateTime(new Date());
            resourceEntity.setGlobalTransactionId(globalTransactionId);
            resourceEntity.setBranchResourceId(null);
            updateResource(resourceEntity);

            return false;
        }
    }

    /**
     * 获取cc
     * @return
     */
    public boolean cancelResource(String globalTransactionId) {

        ResourceEntity resourceEntity = new ResourceEntity();

        if (cancelResource.run()) {

            resourceEntity.setIsDelete(0);
            resourceEntity.setStage(TransactionStage.FAIL.getValue());
            resourceEntity.setLastUpdateTime(new Date());
            resourceEntity.setGlobalTransactionId(globalTransactionId);
            resourceEntity.setBranchResourceId("");
            updateResource(resourceEntity);

            return true;

        }else{
            resourceEntity.setIsDelete(1);
            resourceEntity.setStage(TransactionStage.FAIL.getValue());
            resourceEntity.setLastUpdateTime(new Date());
            resourceEntity.setGlobalTransactionId(globalTransactionId);
            resourceEntity.setBranchResourceId(null);
            updateResource(resourceEntity);

            return false;
        }
    }

    /**
     * 获取所有remote
     * @return
     */
    public List<ResourceBundle> getRemoteResource() {
        return resourceBundleList;
    }

    /**
     * 创资源
     * @param resource
     */
    public void createResource(ResourceEntity resource) {
        transactionRepository.create(resource);
    }

    /**
     * 删除资源
     * @param resource
     */
    public void deleteResource(ResourceEntity resource) {
        transactionRepository.delete(resource);
    }

    public void updateResource(ResourceEntity resource) {
        transactionRepository.update(resource);
    }

    //异步
    public boolean asyncCallRemoteResource() {
        return true;
    }

    public boolean callRemoteResource(TransactionContext transactionContext) {

        List<ResourceBundle> remoteResource = getRemoteResource();

        for (ResourceBundle resourceBundle : remoteResource) {

            ResourceEntity resourceEntity = new ResourceEntity();

            if (resourceBundle.getResource().run()) {

                //只有当前状态还confirm的时候 才能删除 resource
                if (transactionContext.getTransactionStage() == TransactionStage.CONFIRMING) {
                    resourceEntity.setGlobalTransactionId(resourceBundle.getGlobalTransactionId());
                    resourceEntity.setBranchResourceId(resourceBundle.getBranchResourceId());
                    deleteResource(resourceEntity);
                }

            }else {

                resourceEntity.setIsDelete(1);
                resourceEntity.setStage(transactionContext.getTransactionStage().getValue());
                resourceEntity.setLastUpdateTime(new Date());
                resourceEntity.setGlobalTransactionId(resourceBundle.getGlobalTransactionId());
                resourceEntity.setBranchResourceId(resourceBundle.getBranchResourceId());
                updateResource(resourceEntity);

                //错一个立刻中断， 以后重新主事务在发一起一次
                return false;
            }
        }

        return true;
    }
}
