package me.nixuehan.tcc.resource;

import me.nixuehan.tcc.exception.TransactionException;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;

import java.io.Serializable;

public class DubboResource implements Resource, Serializable {
    private Invocation invocation;

    private String dubboProtocol;

    private String host;

    private Integer port;

    private String path;

    private String version;

    public DubboResource() {
    }

    public DubboResource(URL url, Invocation invocation) {

        dubboProtocol = url.getProtocol();
        host = url.getHost();
        port = url.getPort();
        path = url.getPath();
        version = url.getParameter("version");

        this.invocation = invocation;
    }

    private Invoker getInvoker() throws TransactionException {
        Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

        try {
            URL invokerUrl = URL.valueOf(dubboProtocol + "://" + host +":" + port + "/" +  path +"?version=" + version);

            Invoker<?> refer = protocol.refer(Class.forName(invocation.getServiceName()), invokerUrl);
            return refer;

        } catch (ClassNotFoundException e) {

            throw new TransactionException(e);

        }
    }

    private Invocation getInvocation() {
        return invocation;
    }

    @Override
    public boolean run() {

        try {
            getInvoker().invoke(getInvocation());
            return true;
        }catch (TransactionException e) {
            return false;
        }
    }
}
