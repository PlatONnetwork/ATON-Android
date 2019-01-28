package com.juzix.wallet.db.entity;

import android.text.TextUtils;

import com.juzix.wallet.entity.NodeEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class NodeInfoEntity extends RealmObject {

    @PrimaryKey
    private long id;
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

    private boolean isChecked;

    public NodeEntity createNode() {
        return new NodeEntity.Builder()
                .id(id)
                .nodeAddress(nodeAddress)
                .isDefaultNode(isDefaultNode)
                .isChecked(isChecked)
                .isMainNetworkNode(isMainNetworkNode)
                .build();
    }

    public NodeInfoEntity() {
    }

    public NodeInfoEntity(long id, String nodeAddress, boolean isDefaultNode, boolean isChecked, boolean isMainNetworkNode) {
        this.id = id;
        this.nodeAddress = nodeAddress;
        this.isDefaultNode = isDefaultNode;
        this.isChecked = isChecked;
        this.isMainNetworkNode = isMainNetworkNode;
    }

    public NodeEntity buildNodeEntity() {
        return TextUtils.isEmpty(nodeAddress) ? NodeEntity.createNullNode() : new NodeEntity.Builder()
                .id(id)
                .nodeAddress(nodeAddress)
                .isDefaultNode(isDefaultNode)
                .isChecked(isChecked)
                .isMainNetworkNode(isMainNetworkNode)
                .build();
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

    public boolean isMainNetworkNode() {
        return isMainNetworkNode;
    }

    public void setMainNetworkNode(boolean mainNetworkNode) {
        isMainNetworkNode = mainNetworkNode;
    }
}
