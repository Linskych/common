package com.cloudminds.framework.exception;

import com.cloudminds.framework.response.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ParamExceptionUtil {

    public R paramMiss() {

        return paramMiss(StringUtils.EMPTY);
    }

    public R paramMiss(String param) {

        return null;
    }

    public R paramInvalid() {

        return paramInvalid(StringUtils.EMPTY);
    }

    public R paramInvalid(String param) {

        return null;
    }

    public R paramError(String i18nKey) {

        return paramError(i18nKey, StringUtils.EMPTY);
    }

    public R paramError(String i18nKey, String param) {

        return null;
    }
}
