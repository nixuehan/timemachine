package me.nixuehan.timemachine;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "timemachine.tcc")
public class TimeMachineProperties {

    private int corePoolSize = 4;

    private int maximumPoolSize = 10;

    private long keepAliveTime = 30L;

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }
}
