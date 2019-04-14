package com.juzix.wallet.engine;

import com.juzix.wallet.db.entity.NodeInfoEntity;
import com.juzix.wallet.entity.NodeEntity;

import java.util.List;

import io.reactivex.Single;

/**
 * @author matrixelement
 */
public interface INodeService {

    Single<List<NodeEntity>> getNodeList();

    Single<Boolean> insertNode(NodeInfoEntity nodeEntity);

    Single<Boolean> insertNode(List<NodeEntity> nodeInfoEntityList);

    Single<Boolean> deleteNode(String nodeAddress);

    Single<Boolean> deleteNode(List<String> idList);

    Single<Boolean> updateNode(String uuid, String nodeAddress);

    Single<Boolean> updateNode(String uuid, boolean isChecked);

    Single<NodeEntity> getNode(boolean isChecked);
}
