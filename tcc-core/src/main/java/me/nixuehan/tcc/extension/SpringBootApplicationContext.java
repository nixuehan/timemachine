package me.nixuehan.tcc.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringBootApplicationContext {

    private static final Map<String,Object> CONTEXT = new ConcurrentHashMap<>();


    public static void put(String k, Object v) {
        if (CONTEXT.containsKey(k)) {
            CONTEXT.replace(k,v);
        }else{
            CONTEXT.put(k,v);
        }
    }

    public static <T> T get(Class<T> type , String name) {
        return (T)CONTEXT.get(name);
    }
}
