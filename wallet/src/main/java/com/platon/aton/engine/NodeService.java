package com.platon.aton.engine;

import com.platon.aton.db.entity.NodeEntity;
import com.platon.aton.db.sqlite.NodeDao;
import com.platon.aton.entity.Node;

import org.reactivestreams.Publisher;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class NodeService implements INodeService {

    @Override
    public Single<List<Node>> getNodeList() {

        return Single.fromCallable(new Callable<List<NodeEntity>>() {
            @Override
            public List<NodeEntity> call() throws Exception {
                return NodeDao.getNodeList();
            }
        }).toFlowable()
                .flatMap(new Function<List<NodeEntity>, Publisher<NodeEntity>>() {
                    @Override
                    public Publisher<NodeEntity> apply(List<NodeEntity> nodeInfoEntities) throws Exception {
                        return Flowable.fromIterable(nodeInfoEntities);
                    }
                })
                .map(new Function<NodeEntity, Node>() {
                    @Override
                    public Node apply(NodeEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.createNode();
                    }
                })
                .toList();
    }

    @Override
    public Single<Boolean> insertNode(NodeEntity nodeInfoEntity) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.insertNode(nodeInfoEntity);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertNode(List<Node> nodeEntityList) {

        return Flowable.fromIterable(nodeEntityList)
                .map(new Function<Node, NodeEntity>() {
                    @Override
                    public NodeEntity apply(Node nodeEntity) throws Exception {
                        return nodeEntity.createNodeInfo();
                    }
                })
                .toList()
                .map(new Function<List<NodeEntity>, Boolean>() {
                    @Override
                    public Boolean apply(List<NodeEntity> nodeInfoEntityList) throws Exception {
                        return NodeDao.insertNodeList(nodeInfoEntityList);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(long id) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.deleteNode(id);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(List<Long> idList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.deleteNode(idList);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, String nodeAddress) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.updateNode(id, nodeAddress);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, boolean isChecked) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeDao.updateNode(id, isChecked);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Node> getNode(boolean isChecked) {
        return Flowable.fromIterable(NodeDao.getNode(isChecked))
                .firstElement()
                .map(new Function<NodeEntity, Node>() {
                    @Override
                    public Node apply(NodeEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.buildNodeEntity();
                    }
                })
                .toSingle()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<List<Node>> getNode(String nodeAddress) {
        return Flowable.fromIterable(NodeDao.getNode(nodeAddress))
                .map(new Function<NodeEntity, Node>() {
                    @Override
                    public Node apply(NodeEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.buildNodeEntity();
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io());
    }
}
