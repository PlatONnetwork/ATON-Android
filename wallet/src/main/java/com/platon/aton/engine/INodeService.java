package com.platon.aton.engine;

import com.platon.aton.db.entity.NodeEntity;
import com.platon.aton.entity.Node;

import java.util.List;

import io.reactivex.Single;

/**
 * @author matrixelement
 */
public interface INodeService {

    Single<List<Node>> getNodeList();

    Single<Boolean> insertNode(NodeEntity nodeEntity);

    Single<Boolean> insertNode(List<Node> nodeInfoEntityList);

    Single<Boolean> deleteNode(long id);

    Single<Boolean> deleteNode(List<Long> idList);

    Single<Boolean> updateNode(long id, String nodeAddress);

    Single<Boolean> updateNode(long id, boolean isChecked);

    Single<Node> getNode(boolean isChecked);

    Single<List<Node>> getNode(String nodeAddress);
}
