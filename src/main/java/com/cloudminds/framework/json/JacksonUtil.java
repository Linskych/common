package com.cloudminds.framework.json;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JacksonUtil {

    private static final Logger log = LoggerFactory.getLogger(JacksonUtil.class);

    /**
     * @desc des/ser by only field
     * */
    private static final ObjectMapper FIELD_MAPPER = new ObjectMapper();

    private static final ObjectMapper UNDERSCORE_FIELD_MAPPER = new ObjectMapper();

    /**
     * @desc des/ser by field only if there are not getters/setters
     *       Note that getters must be public and setters can be any for defaults. Change settings below if it is necessary.
     * */
    private static final ObjectMapper MIX_MAPPER = new ObjectMapper();


    //set necessary config here for jackson
    static {
        FIELD_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        FIELD_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        FIELD_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        FIELD_MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

        UNDERSCORE_FIELD_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        UNDERSCORE_FIELD_MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        UNDERSCORE_FIELD_MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        UNDERSCORE_FIELD_MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);

        MIX_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        FIELD_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UNDERSCORE_FIELD_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MIX_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        UNDERSCORE_FIELD_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    private JacksonUtil() {}

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
     * @desc Use public getters first and then field if there are not public getters
     * */
    public static String toJsonMix(Object obj) {
        if (null != obj) {
            try {
                return MIX_MAPPER.writeValueAsString(obj);
            } catch (JsonProcessingException e) {
                log.error("Write json for {} occurs error.", obj, e);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * toJsonWithUnderscore -> to_json_with_underscore
     * */
    public static String toJsonWithUnderscore(Object obj) {
        if (null != obj) {
            try {
                return UNDERSCORE_FIELD_MAPPER.writeValueAsString(obj);
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

    /**
     * @desc The default constructor is needed for T.
     *       Use setters first and then fields if there are not any setters(private, protected, public. This is different from toJsonMix.)
     * */
    public static <T> T toObjectMix(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return MIX_MAPPER.readValue(json, clazz);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return null;
    }

    public static <T> T toObjectWithUnderscore(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return UNDERSCORE_FIELD_MAPPER.readValue(json, clazz);
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
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

    public static <T> List<T> toListWithUnderscore(String json, Class<T> clazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return UNDERSCORE_FIELD_MAPPER.readValue(json,
                        UNDERSCORE_FIELD_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException e) {
                log.error("Read json from {} occurs error.", json, e);
            }
        }
        return Collections.emptyList();
    }

    public static <K, V> Map<K, V> toMapWithUnderscore(String json, Class<K> keyClazz, Class<V> valueClazz) {
        if (StringUtils.isNotEmpty(json)) {
            try {
                return UNDERSCORE_FIELD_MAPPER.readValue(json,
                        UNDERSCORE_FIELD_MAPPER.getTypeFactory().constructMapType(Map.class, keyClazz, valueClazz));
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
