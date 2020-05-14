package com.cloudminds.framework.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

public class ByteRedisSerializer implements RedisSerializer<Object> {

    public ByteRedisSerializer() {}

    @Override
    public byte[] serialize(Object o) throws SerializationException {

        return JacksonSerializerUtil.toJsonByte(o);
    }

    /**
     * @desc Return raw bytes to client. So client needs to des result itself and client can decide how to des it.
     * */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {

        return bytes;
    }
}
