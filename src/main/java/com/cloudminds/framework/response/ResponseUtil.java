package com.cloudminds.framework.response;

import com.cloudminds.framework.i18n.I18nLangUtil;
import com.cloudminds.framework.json.JacksonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ResponseUtil {

    private static final Logger log = LoggerFactory.getLogger(ResponseUtil.class);

    @Autowired
    private I18nLangUtil i18nLangUtil;


    public void httpResponse(ServletResponse response, R r) {
        if (response.isCommitted()) {
            return;
        }
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        servletResponse.setStatus(HttpStatus.OK.value());
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());
        servletResponse.setContentType("application/json; charset=utf-8");

        try {
            servletResponse.getWriter().write(JacksonUtil.toJson(r));
            servletResponse.getWriter().flush();
        } catch (IOException e) {
            log.error("HttpResponse can not be written", e);
        }
    }

    public R unknownException() {

        return R.err(ResponseCode.UNKNOWN_ERROR, i18nLangUtil.getMsg("common.error"));
    }

//========================login/logout=====================================

    public R loginSuccess() {
        return loginSuccess(null);
    }

    public R loginSuccess(Object data) {
        R r = R.ok().setMsg(i18nLangUtil.getMsg("common.login.ok"));
        if (data != null) {
            r.setData(data);
        }
        return r;
    }

    public R accountPasswordWrong() {

        return R.err(ResponseCode.AUTH_ACCOUNT_PWD_WRONG, i18nLangUtil.getMsg("common.login.accountorpassword.error"));
    }

    public R tokenMiss() {

        return R.err(ResponseCode.AUTH_TOKEN_MISSING, i18nLangUtil.getMsg("common.token.miss"));
    }

    public R tokenExpired() {

        return R.err(ResponseCode.AUTH_TOKEN_EXPIRED, i18nLangUtil.getMsg("common.token.expired"));
    }

    public R loginFailed() {

        return R.err(ResponseCode.AUTH_ERROR, i18nLangUtil.getMsg("common.login.fail"));
    }

    public R logoutSuccess() {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.logout.ok"));
    }


//========================operation=====================================

    public R operateSuccess() {

        return operateSuccess(null);
    }

    public R operateSuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.operation.ok")).setData(data);
    }

    public R operateFailed() {

        return R.err(ResponseCode.OPERATION_FAIL, i18nLangUtil.getMsg("common.operation.fail"));
    }

    public R operateFailed(int code, String i18nKey) {

        return R.err(code, i18nLangUtil.getMsg(i18nKey));
    }

    public R targetExist(String i18nTarget, String val) {
        String msg = i18nLangUtil.getMsg(i18nTarget) + "(" + val + "): " + i18nLangUtil.getMsg("common.operation.exist");

        return R.err(ResponseCode.OPERATION_EXIST, msg);
    }

    public R targetNotExist(String i18nTarget, String val) {
        String msg = i18nLangUtil.getMsg(i18nTarget) + "(" + val + "): " + i18nLangUtil.getMsg("common.operation.not.exist");

        return R.err(ResponseCode.OPERATION_NOT_EXIST, msg);
    }

    public R createSuccess() {

        return createSuccess(null);
    }

    public R createSuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.create.ok")).setData(data);
    }

    public R createFailed() {

        return createFailed("common.create.fail");
    }

    public R createFailed(String i18nKey) {

        return R.err(ResponseCode.OPERATION_CREATE_FAIL, i18nLangUtil.getMsg(i18nKey));
    }

    public R updateSuccess() {

        return updateSuccess(null);
    }

    public R updateSuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.update.ok")).setData(data);
    }

    public R updateFailed() {

        return updateFailed("common.update.fail");
    }

    public R updateFailed(String i18nKey) {

        return R.err(ResponseCode.OPERATION_UPDATE_FAIL, i18nLangUtil.getMsg(i18nKey));
    }

    public R deleteSuccess() {

        return deleteSuccess(null);
    }

    public R deleteSuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.delete.ok")).setData(data);
    }

    public R deleteFailed() {

        return deleteFailed("common.delete.fail");
    }

    public R deleteFailed(String i18nKey) {

        return R.err(ResponseCode.OPERATION_DELETE_FAIL, i18nLangUtil.getMsg(i18nKey));
    }


    public R detailSuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.detail.ok")).setData(data);
    }

    public R detailFailed() {

        return detailFailed("common.detail.fail");
    }

    public R detailFailed(String i18nKey) {

        return R.err(ResponseCode.OPERATION_DETAIL_FAIL, i18nLangUtil.getMsg(i18nKey));
    }

    public R querySuccess(Object data) {

        return R.ok().setMsg(i18nLangUtil.getMsg("common.query.ok")).setData(data);
    }

    public R queryEmpty() {

        return R.err(ResponseCode.OPERATION_QUERY_EMPTY, i18nLangUtil.getMsg("common.query.empty"));
    }

    public R queryFailed() {

        return queryFailed("common.query.fail");
    }

    public R queryFailed(String i18nKey) {

        return R.err(ResponseCode.OPERATION_QUERY_FAIL, i18nLangUtil.getMsg(i18nKey));
    }


//========================param=====================================

    public R paramMiss() {

        return paramMiss(StringUtils.EMPTY);
    }

    public R paramMiss(String param) {
        String msg;
        if (StringUtils.isEmpty(param)) {
            msg = i18nLangUtil.getMsg("common.param.missing");
        } else {
            msg = i18nLangUtil.getMsg("common.param.missing") + ": " + i18nLangUtil.getMsg(param);
        }

        return R.err(ResponseCode.PARAM_MISSING, msg);
    }

    public R paramEmpty() {

        return paramEmpty(StringUtils.EMPTY);
    }

    public R paramEmpty(String param) {
        String msg;
        if (StringUtils.isEmpty(param)) {
            msg = i18nLangUtil.getMsg("common.param.empty");
        } else {
            msg = i18nLangUtil.getMsg("common.param.empty") + ": " + i18nLangUtil.getMsg(param);
        }

        return R.err(ResponseCode.PARAM_EMPTY, msg);
    }

    public R paramInvalid() {

        return paramInvalid(StringUtils.EMPTY);
    }

    public R paramInvalid(String param) {
        String msg;
        if (StringUtils.isEmpty(param)) {
            msg = i18nLangUtil.getMsg("common.param.invalid");
        } else {
            msg = i18nLangUtil.getMsg("common.param.invalid") + ": " + i18nLangUtil.getMsg(param);
        }

        return R.err(ResponseCode.PARAM_INVALID, msg);
    }

    public R paramError(int code, String i18nKey) {

        return paramError(code, i18nKey, StringUtils.EMPTY);
    }

    public R paramError(int code, String i18nKey, String param) {
        String msg;
        if (StringUtils.isEmpty(param)) {
            msg = i18nLangUtil.getMsg(i18nKey);
        } else {
            msg = i18nLangUtil.getMsg(i18nKey) + ": " + i18nLangUtil.getMsg(param);
        }

        return R.err(code, msg);
    }



}
