package com.juzix.wallet.entity;

import java.util.UUID;

/**
 * @author matrixelement
 */
public class NullNodeEntity extends NodeEntity {

    private NullNodeEntity() {
        this.setUuid(UUID.randomUUID().toString());
    }
    
    public static NullNodeEntity getInstance() {
        return new NullNodeEntity();
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
