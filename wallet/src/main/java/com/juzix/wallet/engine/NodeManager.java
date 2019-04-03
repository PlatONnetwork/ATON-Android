package com.juzix.wallet.engine;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.entity.NodeEntity;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author matrixelement
 */
public class NodeManager {

    private final static String[] DEFAULT_NODE_URL_LIST = new String[]{Constants.URL.WEB3J_URL};

    private NodeEntity curNode;
    private NodeService nodeService;

    private NodeManager() {

    }

    private static class InstanceHolder {
        private static volatile NodeManager INSTANCE = new NodeManager();
    }

    public static NodeManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public NodeEntity getCurNode() {
        return curNode;
    }

    public void setCurNode(NodeEntity curNode) {
        this.curNode = curNode;
    }

    public void init() {

        nodeService = new NodeService();

        nodeService.getNodeList()
                .toFlowable()
                .flatMap(new Function<List<NodeEntity>, Publisher<NodeEntity>>() {
                    @Override
                    public Publisher<NodeEntity> apply(List<NodeEntity> nodeEntityList) throws Exception {
                        if (nodeEntityList.isEmpty()) {
                            return nodeService.insertNode(getDefaultNodeList()).flatMapPublisher(new Function<Boolean, Publisher<NodeEntity>>() {
                                @Override
                                public Publisher<NodeEntity> apply(Boolean aBoolean) throws Exception {
                                    return getCheckedNode().toFlowable();
                                }
                            });
                        } else {
                            return getCheckedNode().toFlowable();
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<NodeEntity>() {

                    @Override
                    public void accept(NodeEntity nodeEntity) throws Exception {
                        switchNode(nodeEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void switchNode(NodeEntity nodeEntity) {
        setCurNode(nodeEntity);
        Web3jManager.getInstance().init(nodeEntity.getNodeAddress());
    }

    public Single<List<NodeEntity>> getNodeList() {
        return nodeService.getNodeList();
    }

    public Single<NodeEntity> getCheckedNode() {
        return nodeService.getNode(true);
    }

    public Single<Boolean> insertNodeList(List<NodeEntity> nodeEntityList) {
        return nodeService.insertNode(nodeEntityList);
    }

    public Single<Boolean> deleteNode(NodeEntity nodeEntity) {
        return nodeService.deleteNode(nodeEntity.getId());
    }

    public void updateNode(NodeEntity nodeEntity, boolean isChecked) {
        nodeService.updateNode(nodeEntity.getId(), isChecked)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean.booleanValue() && isChecked) {
                            NodeManager.getInstance().switchNode(nodeEntity);
                        }
                    }
                });
    }

    private List<NodeEntity> getDefaultNodeList() {

        List<NodeEntity> nodeInfoEntityList = new ArrayList<>();

        for (int i = 0; i < DEFAULT_NODE_URL_LIST.length; i++) {
            nodeInfoEntityList.add(new NodeEntity.Builder().nodeAddress(DEFAULT_NODE_URL_LIST[i]).isDefaultNode(true).isChecked(i == 0).isMainNetworkNode(false).build());
        }
        return nodeInfoEntityList;
    }

}
