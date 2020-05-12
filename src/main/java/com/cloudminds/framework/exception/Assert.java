package com.cloudminds.framework.exception;

public interface Assert {

    BaseException exception();

    default void assertNotNull(Object obj) {
        if (obj == null) {
            throw exception();
        }
    }
}
