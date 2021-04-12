package com.gabler.gameclient.client;

import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.function.Function;

/**
 * Transform bytes to an object of the parameterized type.
 *
 * @param <OBJECT_TYPE> The type of object
 * @author Andy Gabler
 */
public class BytesToObjectTransformer<OBJECT_TYPE> implements Function<byte[], OBJECT_TYPE> {

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows
    public OBJECT_TYPE apply(byte[] bytes) {
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(bytes);
        final ObjectInputStream objectInputStream = new ObjectInputStream(byteStream);
        final Object object = objectInputStream.readObject();
        final OBJECT_TYPE request = (OBJECT_TYPE) object;
        return request;
    }
}
