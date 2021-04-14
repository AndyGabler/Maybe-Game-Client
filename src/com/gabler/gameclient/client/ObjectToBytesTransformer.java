package com.gabler.gameclient.client;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.function.Function;

/**
 * Transform a serializable object to a byte array.
 *
 * @author Andy Gabler
 */
public class ObjectToBytesTransformer implements Function<Serializable, byte[]> {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] apply(Serializable object) {
        try {
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            final ObjectOutputStream outputStream = new ObjectOutputStream(byteStream);
            outputStream.writeObject(object);

            final byte[] payload = byteStream.toByteArray();
            byteStream.close();

            return payload;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
