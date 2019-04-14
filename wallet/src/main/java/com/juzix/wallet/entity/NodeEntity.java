package com.juzix.wallet.entity;

import android.text.TextUtils;

import com.juzix.wallet.db.entity.NodeInfoEntity;

import java.util.UUID;

/**
 * @author matrixelement
 */
public class NodeEntity implements Cloneable, Nullable {

    private String uuid;
    /**
     * 节点地址
     */
    private String nodeAddress;
    /**
     * 是否是默认的节点
     */
    private boolean isDefaultNode;
    /**
     * 主网络
     */
    private boolean isMainNetworkNode;

    private boolean isFormatCorrect = false;

    private boolean isChecked = false;

    protected NodeEntity() {

    }

    private NodeEntity(Builder builder) {
        setUuid(builder.uuid);
        setNodeAddress(builder.nodeAddress);
        setDefaultNode(builder.isDefaultNode);
        setFormatCorrect(builder.isFormatCorrect);
        setChecked(builder.isChecked);
        setMainNetworkNode(builder.isMainNetworkNode);
    }

    public static NodeEntity createNullNode() {
        return NullNodeEntity.getInstance();
    }

    public NodeInfoEntity createNodeInfo() {
        return new NodeInfoEntity(uuid, nodeAddress, isDefaultNode, isChecked, isMainNetworkNode);
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

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isMainNetworkNode() {
        return isMainNetworkNode;
    }

    public void setMainNetworkNode(boolean mainNetworkNode) {
        isMainNetworkNode = mainNetworkNode;
    }

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(uuid) ? 0 : uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NodeEntity) {
            NodeEntity node = (NodeEntity) obj;
            return node.getUuid() != null && node.getUuid().equals(uuid);
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
        private String uuid;
        private String nodeAddress;
        private boolean isDefaultNode;
        private boolean isFormatCorrect;
        private boolean isChecked;
        private boolean isMainNetworkNode;

        public Builder() {
            uuid = UUID.randomUUID().toString();
        }

        public Builder uuid(String val) {
            uuid = val;
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

        public Builder isMainNetworkNode(boolean val) {
            isMainNetworkNode = val;
            return this;
        }

        public NodeEntity build() {
            return new NodeEntity(this);
        }
    }
}
