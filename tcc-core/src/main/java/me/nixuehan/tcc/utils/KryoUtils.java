package me.nixuehan.tcc.utils;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.tools.javac.code.Attribute;
import me.nixuehan.tcc.resource.DubboResource;
import me.nixuehan.tcc.resource.TccBundle;
import me.nixuehan.tcc.resource.TccResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class KryoUtils {
    private final static ThreadLocal<Kryo> threadLocalKryo = new ThreadLocal<Kryo>(){
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            kryo.setCopyReferences(false);
//            kryo.register(TccBundle.class);
//            kryo.register(DubboResource.class);
//            kryo.register(TccResource.class);
//            kryo.register(Deque.class);
            return kryo;
        }
    };

    /**
     *
     * @param object
     * @param <T>
     * @return
     */
    public static<T> byte[] serializer(T object) {
        Kryo kryo = threadLocalKryo.get();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Output output = new Output(outputStream);
        kryo.writeObject(output,object);
        output.flush();
        output.close();
        return outputStream.toByteArray();
    }

    public static<T> T deserializer(byte[] data, Class<T> clazz) {
        Kryo kryo = threadLocalKryo.get();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        Input input = new Input(inputStream);
        T object = kryo.readObject(input, clazz);
        input.close();
        return object;
    }
}
