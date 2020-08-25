package me.nixuehan.tcc.resource;

import java.io.Serializable;
import java.lang.reflect.Method;

//协调者要执行的资源
public class TccResource implements Resource, Serializable {

    private Class targetClass;

    private Class<?>[] parameterTypes;

    private String methodName;

    private Object[] arguments;

    private String tccType;

    public TccResource() {
    }

    public TccResource(Class<?>[] parameterTypes, String methodName, Class targetClass, Object[] arguments, String tccType) {
        this.parameterTypes = parameterTypes;
        this.methodName = methodName;
        this.arguments = arguments;
        this.tccType = tccType;
        this.targetClass = targetClass;
    }

    @Override
    public boolean run() {
        try {
            Method method = targetClass.getMethod(methodName, parameterTypes);

            //todo 做cache
            Object target = targetClass.newInstance();

            method.invoke(target,arguments);

            return true;

        } catch (Throwable e) {
            return false;
        }
    }
}
