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

    Single<Boolean> deleteNode(long id);

    Single<Boolean> deleteNode(List<Long> idList);

    Single<Boolean> updateNode(long id, String nodeAddress);

    Single<Boolean> updateNode(long id, boolean isChecked);

    Single<NodeEntity> getNode(boolean isChecked);
}
