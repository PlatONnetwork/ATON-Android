package com.juzix.wallet.engine;

import com.juzix.wallet.db.entity.NodeInfoEntity;
import com.juzix.wallet.db.sqlite.NodeInfoDao;
import com.juzix.wallet.entity.NodeEntity;

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

        return Flowable.fromIterable(NodeInfoDao.getInstance()
                .getNodeList())
                .map(new Function<NodeInfoEntity, NodeEntity>() {
                    @Override
                    public NodeEntity apply(NodeInfoEntity nodeInfoEntity) throws Exception {
                        return nodeInfoEntity.createNode();
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> insertNode(NodeInfoEntity nodeInfoEntity) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.getInstance().insertNode(nodeInfoEntity);
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
                        return NodeInfoDao.getInstance().insertNodeList(nodeInfoEntityList);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(long id) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.getInstance().deleteNode(id);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> deleteNode(List<Long> idList) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.getInstance().deleteNode(idList);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, String nodeAddress) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.getInstance().updateNode(id, nodeAddress);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<Boolean> updateNode(long id, boolean isChecked) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return NodeInfoDao.getInstance().updateNode(id, isChecked);
            }
        }).subscribeOn(Schedulers.io());
    }

    @Override
    public Single<NodeEntity> getNode(boolean isChecked) {
        return Flowable.fromIterable(NodeInfoDao.getInstance().getNode(isChecked))
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
}
