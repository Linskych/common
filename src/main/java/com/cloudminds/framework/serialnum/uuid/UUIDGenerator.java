package com.cloudminds.framework.serialnum.uuid;

import com.cloudminds.framework.serialnum.SerialNumGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("uuidGenerator")
public class UUIDGenerator implements SerialNumGenerator {

    @Override
    public String getSerialNum() {

        return UUID.randomUUID().toString().replace("-", "");
    }
}
