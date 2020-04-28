package com.cloudminds.framework.serialnum.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shine
 * @date 2020/3/30
 * @desc
 */
public class HostMessageUtil {

    private static Logger log = LoggerFactory.getLogger(HostMessageUtil.class);

    public final static String HOST_IP = "host_ip";
    public final static String HOST_NAME = "host_name";

    private static InetAddress netAddress;

    /**
     * 获取本机的主机名和ip
     */
    public static Map<String, String> getHostMessage() {
        try {
            if (null == netAddress) {
                netAddress = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException" + e.getCause());
        }
        Map<String, String> hostMessageMap = new HashMap<String, String>();
        hostMessageMap.put(HOST_IP, netAddress.getHostAddress());
        hostMessageMap.put(HOST_NAME, netAddress.getHostName());
        return hostMessageMap;
    }

    public static String getHostIp() {
        try {
            if (null == netAddress) {
                netAddress = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException" + e.getCause());
        }
        return netAddress.getHostAddress();
    }

    public static String getHostName() {
        try {
            if (null == netAddress) {
                netAddress = InetAddress.getLocalHost();
            }
        } catch (UnknownHostException e) {
            log.error("UnknownHostException" + e.getCause());
        }
        return netAddress.getHostName();
    }
}
