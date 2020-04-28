package com.cloudminds.framework.serialnum.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author shine
 * @date 2020/3/30
 * @desc
 */
public class CuratorUtil {

    private static final Logger logger = LoggerFactory.getLogger(CuratorUtil.class);

    private static CuratorFramework client;

    private final static String CREATE_TIME = "create_time";

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static CuratorFramework getCuratorClient(String zkPath, String rootPath) {
        if (client == null) {
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            client = CuratorFrameworkFactory.builder()
                    .connectString(zkPath)
                    .sessionTimeoutMs(5000)
                    .connectionTimeoutMs(5000)
                    .retryPolicy(retryPolicy)
                    .namespace(rootPath)
                    .build();
        }
        return client;

    }

    public static int getWorkId(String zkPath, String rootPath){
        if (client == null) {
            client = getCuratorClient(zkPath, rootPath);
        }
        if (client.isStarted() == false) {
            client.start();
        }
        for (int i = 1; i < 1024; i++) {
            try {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/"+i, getZkData().getBytes());
                logger.info("work id {} is available.", i);
                return i;
            } catch (Exception e) {
                logger.info("work id {} can not be used. {} ", i,e);
            }

        }
        return 0;
    }

    private static String getZkData() {
        Map<String,String> hostMessageMap  = com.cloudminds.ross.common.util.idGenerator.HostMessageUtil.getHostMessage();
        Date date = new Date();
        hostMessageMap.put(CREATE_TIME, FORMAT.format(date));
        return JSONArray.toJSONString(hostMessageMap);
    }
}
