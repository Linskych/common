package com.cloudminds.framework.redis.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Lock multiple keys at the same time. For example, user buys different products so you need to lock product codes at the same time.
 * */
public class MultipleLock {

    private static final Logger log = LoggerFactory.getLogger(MultipleLock.class);

    private List<String> keys;

}
