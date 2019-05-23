package com.juzix.wallet.entity;

import java.util.UUID;

/**
 * @author matrixelement
 */
public class NullNode extends Node {

    private NullNode() {
        this.setId(UUID.randomUUID().hashCode());
    }
    
    public static NullNode getInstance() {
        return new NullNode();
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
