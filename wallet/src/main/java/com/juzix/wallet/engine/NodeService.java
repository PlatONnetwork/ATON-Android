package com.juzix.wallet.engine;

import com.juzix.wallet.db.entity.NodeInfoEntity;
import com.juzix.wallet.db.sqlite.NodeInfoDao;
import com.juzix.wallet.entity.NodeEntity;

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
    public Single<List<NodeEntity>> getNodeList() {

        return Single.fromCallable(new Callable<List<NodeInfoEntity>>() {
            @Override
            public List<NodeInfoEntity> call() throws Exception {
                return NodeInfoDao.getNodeList();
            }
        }).toFlowable()
                .flatMap(new Function<List<NodeInfoEntity>, Publisher<NodeInfoEntity>>() {
                    @Override
                    public Publisher<NodeInfoEntity> apply(List<NodeInfoEntity> nodeInfoEntities) throws Exception {
                        return Flowable.fromIterable(nodeInfoEntities);
                    }
                })
                .map(new Function<NodeInfoEntity, NodeEntity>() {
                    @Override
                    public NodeEntity apply(NodeInfoEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.createNode();
                    }
                })
                .toList();
    }

    @Override
    public Single<Boolean> insertNode(NodeInfoEntity nodeInfoEntity) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.insertNode(nodeInfoEntity);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertNode(List<NodeEntity> nodeEntityList) {

        return Flowable.fromIterable(nodeEntityList)
                .map(new Function<NodeEntity, NodeInfoEntity>() {
                    @Override
                    public NodeInfoEntity apply(NodeEntity nodeEntity) throws Exception {
                        return nodeEntity.createNodeInfo();
                    }
                })
                .toList()
                .map(new Function<List<NodeInfoEntity>, Boolean>() {
                    @Override
                    public Boolean apply(List<NodeInfoEntity> nodeInfoEntityList) throws Exception {
                        return NodeInfoDao.insertNodeList(nodeInfoEntityList);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(long id) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.deleteNode(id);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(List<Long> idList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.deleteNode(idList);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, String nodeAddress) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.updateNode(id, nodeAddress);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, boolean isChecked) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.updateNode(id, isChecked);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<NodeEntity> getNode(boolean isChecked) {
        return Flowable.fromIterable(NodeInfoDao.getNode(isChecked))
                .firstElement()
                .map(new Function<NodeInfoEntity, NodeEntity>() {
                    @Override
                    public NodeEntity apply(NodeInfoEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.buildNodeEntity();
                    }
                })
                .toSingle()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<List<NodeEntity>> getNode(String nodeAddress) {
        return Flowable.fromIterable(NodeInfoDao.getNode(nodeAddress))
                .map(new Function<NodeInfoEntity, NodeEntity>() {
                    @Override
                    public NodeEntity apply(NodeInfoEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.buildNodeEntity();
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io());
    }
}
