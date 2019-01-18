package com.juzix.wallet.entity;

import com.juzix.wallet.db.entity.NodeInfoEntity;

/**
 * @author matrixelement
 */
public class NodeEntity implements Cloneable, Nullable {

    private long id;
    /**
     * 节点地址
     */
    private String nodeAddress;
    /**
     * 是否是默认的节点
     */
    private boolean isDefaultNode;

    private boolean isFormatCorrect = false;

    private boolean isChecked = false;

    protected NodeEntity() {

    }

    private NodeEntity(Builder builder) {
        setId(builder.id);
        setNodeAddress(builder.nodeAddress);
        setDefaultNode(builder.isDefaultNode);
        setFormatCorrect(builder.isFormatCorrect);
        setChecked(builder.isChecked);
    }

    public static NodeEntity createNullNode() {
        return NullNodeEntity.getInstance();
    }

    public NodeInfoEntity createNodeInfo() {
        return new NodeInfoEntity(id, nodeAddress, isDefaultNode, isChecked);
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public boolean isDefaultNode() {
        return isDefaultNode;
    }

    public void setDefaultNode(boolean defaultNode) {
        isDefaultNode = defaultNode;
    }

    public boolean isFormatCorrect() {
        return isFormatCorrect;
    }

    public void setFormatCorrect(boolean formatCorrect) {
        isFormatCorrect = formatCorrect;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NodeEntity) {
            NodeEntity node = (NodeEntity) obj;
            return id == node.id;
        }
        return super.equals(obj);
    }

    @Override
    public NodeEntity clone() {
        NodeEntity nodeEntity = null;
        try {
            nodeEntity = (NodeEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return nodeEntity;
    }

    @Override
    public boolean isNull() {
        return false;
    }


    public static final class Builder {
        private long id;
        private String nodeAddress;
        private boolean isDefaultNode;
        private boolean isFormatCorrect;
        private boolean isChecked;

        public Builder() {
            id = System.currentTimeMillis();
        }

        public Builder id(long val) {
            id = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public Builder isDefaultNode(boolean val) {
            isDefaultNode = val;
            return this;
        }

        public Builder isFormatCorrect(boolean val) {
            isFormatCorrect = val;
            return this;
        }

        public Builder isChecked(boolean val) {
            isChecked = val;
            return this;
        }

        public NodeEntity build() {
            return new NodeEntity(this);
        }
    }
}
