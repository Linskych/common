package com.cloudminds.framework.redis;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JacksonSerializerUtil {

    private static final Logger log = LoggerFactory.getLogger(JacksonSerializerUtil.class);

    /**
     * @desc des/ser by only field
     * */
    private static final ObjectMapper FIELD_MAPPER = new ObjectMapper();


    //set necessary config here for jackson
    static {
        FIELD_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        FIELD_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        FIELD_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        FIELD_MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

        FIELD_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private JacksonSerializerUtil() {}

    public static byte[] toJsonByte(Object obj) {
        if (null != obj) {
            try {
                return FIELD_MAPPER.writeValueAsBytes(obj);
            } catch (JsonProcessingException e) {
                log.error("Write byte for {} occurs error.", obj, e);
            }
        }
        return null;
    }

    public static String toJson(Object obj) {
        if (null != obj) {
            try {
                return FIELD_MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("Write json for {} occurs error.", obj, e);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * @desc The default constructor is needed for T
     * */
    public static <T> T toObject(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return FIELD_MAPPER.readValue(json, clazz);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return null;
    }

    public static <T> T toObject(byte[] bytes, Class<T> clazz) {
        if (!ArrayUtils.isEmpty(bytes)) {
            try {
                return FIELD_MAPPER.readValue(bytes, clazz);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", new String(bytes), e);
            }
        }
        return null;
    }

    //The default constructor is needed for T
    public static <T> T toObject(String json, TypeReference<T> type) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return FIELD_MAPPER.readValue(json, type);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return null;
    }

    public static <T> T toObject(byte[] bytes, TypeReference<T> type) {
        if (!ArrayUtils.isEmpty(bytes)) {
            try {
                return FIELD_MAPPER.readValue(bytes, type);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", new String(bytes), e);
            }
        }
        return null;
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return FIELD_MAPPER.readValue(json, FIELD_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return Collections.emptyList();
    }

    public static <T> List<T> toList(byte[] bytes, Class<T> clazz) {
        if (!ArrayUtils.isEmpty(bytes)) {
            try {
                return FIELD_MAPPER.readValue(bytes, FIELD_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", new String(bytes), e);
            }
        }
        return Collections.emptyList();
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return FIELD_MAPPER.readValue(json, FIELD_MAPPER.getTypeFactory().constructMapType(Map.class, keyClazz, valueClazz));
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return Collections.emptyMap();
    }

    public static boolean isJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return false;
        }
        try {
            JsonNode node = FIELD_MAPPER.readTree(json);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

}
