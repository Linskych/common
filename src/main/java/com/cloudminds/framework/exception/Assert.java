package com.cloudminds.framework.exception;


import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

public interface Assert {

    BaseException exception(int code, String i18nKey, String defaultMsg);

    default void assertNotNull(String paramName, Object obj) {
        if (obj == null) {
//            throw exception(code, i18nKey, defaultMsg);
        }
    }

    default void assertNotEmpty(String paramName, String value) {
        if (StringUtils.isEmpty(value)) {
//            throw exception(code, i18nKey, defaultMsg);
        }
    }

    default void assertNotEmpty(String paramName, Collection value) {
        if (CollectionUtils.isEmpty(value)) {
//            throw exception(code, i18nKey, defaultMsg);
        }
    }
}
