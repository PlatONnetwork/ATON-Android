package com.juzix.wallet.entity;

import java.util.UUID;

/**
 * @author matrixelement
 */
public class NullNodeEntity extends NodeEntity {

    private NullNodeEntity() {
        this.setId(UUID.randomUUID().hashCode());
    }
    
    public static NullNodeEntity getInstance() {
        return new NullNodeEntity();
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
