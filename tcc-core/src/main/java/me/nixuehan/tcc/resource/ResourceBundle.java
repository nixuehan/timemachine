package me.nixuehan.tcc.resource;

public class ResourceBundle {

    String globalTransactionId;
    String branchResourceId;
    Resource resource;


    public ResourceBundle() {
        super();
    }

    public ResourceBundle(TccResource tccResource) {
        this.resource = tccResource;
    }

    public String getGlobalTransactionId() {
        return globalTransactionId;
    }

    public void setGlobalTransactionId(String globalTransactionId) {
        this.globalTransactionId = globalTransactionId;
    }

    public String getBranchResourceId() {
        return branchResourceId;
    }

    public void setBranchResourceId(String branchResourceId) {
        this.branchResourceId = branchResourceId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
