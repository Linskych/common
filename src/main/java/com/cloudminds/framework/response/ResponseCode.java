package com.cloudminds.framework.response;

public interface ResponseCode {

    int SUCCESS = 0;
    int AUTH_TOKEN_EXPIRED = 100;
    int AUTH_TOKEN_MISSING = 101;
    int AUTH_ACCOUNT_PWD_WRONG = 110;
    int AUTH_ERROR = 199;

    int PARAM_INVALID = 200;
    int PARAM_MISSING = 201;
    int PARAM_EMPTY = 202;

    int OPERATION_EXIST = 390;
    int OPERATION_NOT_EXIST = 391;
    int OPERATION_FAIL = 399;

    int OPERATION_CREATE_FAIL = 309;

    int OPERATION_UPDATE_FAIL = 319;

    int OPERATION_DELETE_FAIL = 329;

    int OPERATION_DETAIL_FAIL = 339;

    int OPERATION_QUERY_EMPTY = 340;
    int OPERATION_QUERY_FAIL = 349;


    int UNKNOWN_ERROR = 999;
}
