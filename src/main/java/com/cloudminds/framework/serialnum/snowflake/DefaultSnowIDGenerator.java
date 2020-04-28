package com.cloudminds.framework.serialnum.snowflake;

import com.cloudminds.framework.serialnum.SerialNumGenerator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;

/**
 * @author shine
 * @date 2019/11/16
 * @desc  twitter snowflake
 * long类型的64切分    0 - 41 bits毫秒级时间 - 5 bits数据中心ID - 5 bits 机器id - 12 bits 毫秒内的计数
 * 12位序列计数最大4095，理论上每秒最多生成1000*4095，大约400w
 */

public class DefaultSnowIDGenerator implements SerialNumGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSnowIDGenerator.class);

    protected final long startTime = 1498608000000L;
    //机器ID占位，5
    protected final long workerIdBits = 5L;
    //数据中心占位，5
    protected final long dataCenterIdBits = 5L;
    //序列计数器占位 , 12
    protected final long sequenceBits = 12L;
    //最大机器ID，32
    protected final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //最大数据中心ID， 32
    protected final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);
    //机器ID左移位数，12
    protected final long workerIdMoveBits = sequenceBits;
    //数据中心ID左移位数，17
    protected final long dataCenterIdMoveBits = sequenceBits + workerIdBits;
    //时间戳左移位数，22
    protected final long timestampMoveBits = sequenceBits + workerIdBits + dataCenterIdBits;
    //序列掩码，12位最大整数值，4095
    protected final long sequenceMask = -1L ^ (-1L << sequenceBits);
    //机器ID，0-31
    protected long workerId;
    //数据中心ID，0-31
    protected long dataCenterId;
    protected long sequence = 0L;
    //上次生成时间戳的时间
    protected long lastTimestamp = -1L;

    @PostConstruct
    public void init() throws Exception {
        setWorkerId();
        setDataCenterId();
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("DataCenter Id can't be greater than %d or less than 0", maxDataCenterId));
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        LOGGER.info("current work id is {}", workerId);

    }

    protected synchronized long nextId() throws RuntimeException{
        long timestamp = currentTime();
        //如果当前时间小于上一次ID生成的时间戳: 说明系统时钟回退过 - 这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出 即 序列 > 4095
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = blockTillNextMillis(lastTimestamp);
            }
        }
        //时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - startTime) << timestampMoveBits) //
                | (dataCenterId << dataCenterIdMoveBits) //
                | (workerId << workerIdMoveBits) //
                | sequence;
    }
    // 阻塞到下一个毫秒 即 直到获得新的时间戳
    protected long blockTillNextMillis(long lastTimestamp) {
        long timestamp = currentTime();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTime();
        }
        return timestamp;
    }

    protected long currentTime() {
        return System.currentTimeMillis();
    }

    @Override
    public String getSerialNum() {
        try {
            return String.valueOf(nextId());
        } catch (Exception e) {
            LOGGER.error("Generate unique id exception.\n", e);
        }
        return StringUtils.EMPTY;
    }

    protected void setWorkerId() {
        this.workerId = 0;
    }

    protected void setDataCenterId() {
        this.dataCenterId = 0;
    }
}
