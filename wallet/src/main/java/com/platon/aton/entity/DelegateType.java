package com.platon.aton.entity;


public class DelegateType {
    private String type;
    private String amount;


    public DelegateType(String type, String amount) {
        this.type = type;
        this.amount = amount;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

//    @Override
//    public int hashCode() {
//        return TextUtils.isEmpty(address) ? 0 : address.hashCode();
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) {
//            return true;
//        }
//        if (obj instanceof Wallet) {
//            Wallet entity = (Wallet) obj;
//            return entity.getUuid() != null && entity.getUuid().equals(address);
//        }
//        return super.equals(obj);
//    }
}
