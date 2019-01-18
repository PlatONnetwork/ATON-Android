package com.juzix.wallet.entity;

/**
 * @author matrixelement
 */
public class NullNodeEntity extends NodeEntity {

    private NullNodeEntity() {
        this.setId(System.currentTimeMillis());
    }
    
    public static NullNodeEntity getInstance() {
        return new NullNodeEntity();
    }

    @Override
    public boolean isNull() {
        return true;
    }
}
