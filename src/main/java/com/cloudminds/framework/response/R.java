package com.cloudminds.framework.response;

public class R {

    private int code;
    private String msg;
    private Object data;

    private R() {}

    public static R ok() {
        R r = new R();
        r.setCode(ResponseCode.SUCCESS);
        r.setMsg("OK");
        return r;
    }

    public static R ok(int code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static R okI18n(int code, String msgKey) {
        R r = new R();
        r.setCode(code);
        r.setMsg(msgKey);
        return r;
    }

    public static R err() {

        return new R().setCode(ResponseCode.UNKNOWN_ERROR).setMsg("Unknown error.");
    }

    public static R err(int code, String msg) {

        return new R().setCode(code).setMsg(msg);
    }

    public int getCode() {
        return code;
    }

    public R setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public R setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
