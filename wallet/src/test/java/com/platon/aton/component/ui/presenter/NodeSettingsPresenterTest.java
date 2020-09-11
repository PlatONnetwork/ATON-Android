package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.platon.aton.BaseTestCase;
import com.platon.aton.R;
import com.platon.aton.component.ui.contract.NodeSettingsContract;
import com.platon.aton.db.entity.NodeEntity;
import com.platon.aton.entity.Node;
import com.platon.framework.network.SchedulersTransformer;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


public class NodeSettingsPresenterTest extends BaseTestCase {

    private NodeSettingsPresenter presenter;
    @Mock
    private NodeSettingsContract.View  view;



    @Override
    public void initSetup() {

        view = Mockito.mock(NodeSettingsContract.View.class);
        presenter = new NodeSettingsPresenter();
        presenter.attachView(view);
    }

    @Test
    public  void cancel(){
        List<Node> nodeList = new ArrayList<>();
        Node node =new Node();
        node.setNodeAddress("https://192.168.12.22");
        node.setDefaultNode(false);
        node.setMainNetworkNode(false);
        node.setChecked(false);
        node.setChainId("103");
        node.setFormatCorrect(false);
        nodeList.add(node);

        Node node2 =new Node();
        node2.setNodeAddress("https://192.168.12.82");
        node2.setDefaultNode(true);
        node2.setMainNetworkNode(true);
        node2.setChecked(false);
        node2.setChainId("103");
        node2.setFormatCorrect(false);
        nodeList.add(node2);

        Node node3 =new Node();
        node3.setNodeAddress("https://192.168.12.182");
        node3.setDefaultNode(true);
        node3.setMainNetworkNode(false);
        node3.setChecked(false);
        node3.setChainId("103");
        node3.setFormatCorrect(false);
        nodeList.add(node3);

        List<Node> removeNodeList = new ArrayList<>();
        if (nodeList != null && !nodeList.isEmpty()) {
            for (int i = 0; i < nodeList.size(); i++) {
                Node nd = nodeList.get(i);
                if (node.isDefaultNode() || !TextUtils.isEmpty(node.getNodeAddress())) {
                    continue;
                }

//                String nodeAddress = presenter.getView().getNodeAddress(node.getId());
//                if (TextUtils.isEmpty(nodeAddress) || !nodeAddress.trim().matches("^(http(s?)://)?((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})")) {
//                    removeNodeList.add(node);
//                }

            }
        }
//        if (!removeNodeList.isEmpty()) {
//            presenter.getView().removeNodeList(removeNodeList);
//        }
    }

    @Test
    public  void  save(){
        List<Node> nodeList = new ArrayList<>();
        Node node =new Node();
        node.setNodeAddress("https://192.168.12.22");
        node.setDefaultNode(false);
        node.setMainNetworkNode(false);
        node.setChecked(false);
        node.setChainId("103");
        node.setFormatCorrect(false);
        nodeList.add(node);

        Node node2 =new Node();
        node2.setNodeAddress("https://192.168.12.82");
        node2.setDefaultNode(true);
        node2.setMainNetworkNode(true);
        node2.setChecked(false);
        node2.setChainId("103");
        node2.setFormatCorrect(false);
        nodeList.add(node2);

        Node node3 =new Node();
        node3.setNodeAddress("https://192.168.12.182");
        node3.setDefaultNode(true);
        node3.setMainNetworkNode(false);
        node3.setChecked(false);
        node3.setChainId("103");
        node3.setFormatCorrect(false);
        nodeList.add(node3);

        List<Node> removeNodeList = new ArrayList<>();
        List<Node> errorNodeList = new ArrayList<>();
        List<Node> normalNodeList = new ArrayList<>();
//        if (nodeList != null && !nodeList.isEmpty()) {
//            for (int i = 0; i < nodeList.size(); i++) {
//                Node nd = nodeList.get(i);
//                if (node.isDefaultNode()) {
//                    continue;
//                }
//
//                String nodeAddress = presenter.getView().getNodeAddress(node.getId());
//
//                if (TextUtils.isEmpty(nodeAddress)) {
//                    removeNodeList.add(node);
//                } else {
//                    Node nodeEntity = node.clone();
//                    String address = nodeAddress.trim();
//                    if (address.matches("^(http(s?)://)?((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})") || address.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})")) {
//                        if (address.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)):\\d{3,})")) {
//                            nodeEntity.setNodeAddress("http://".concat(address));
//                        } else {
//                            nodeEntity.setNodeAddress(address);
//                        }
//                        normalNodeList.add(nodeEntity);
//                    } else {
//                        nodeEntity.setFormatCorrect(true);
//                        errorNodeList.add(nodeEntity);
//                    }
//                }
//            }
//        }

//        if (errorNodeList.isEmpty()) {
//            if (!removeNodeList.isEmpty()) {
//                presenter.getView().removeNodeList(removeNodeList);
//            }
//            if (!normalNodeList.isEmpty()) {
////                insertNodeList(normalNodeList);
//                presenter.getView().updateNodeList(normalNodeList);
//            }
//            presenter.getView().showTitleView(false);
//        } else {
////            showLongToast(R.string.node_format_error);
//           presenter.getView().updateNodeList(errorNodeList);
//        }

    }

    @Test
    public void insertNodeList(){
        List<Node> nodeList = new ArrayList<>();
        Node node =new Node();
        node.setNodeAddress("https://192.168.12.22");
        node.setDefaultNode(false);
        node.setMainNetworkNode(false);
        node.setChecked(false);
        node.setChainId("103");
        node.setFormatCorrect(false);
        nodeList.add(node);

        Node node2 =new Node();
        node2.setNodeAddress("https://192.168.12.82");
        node2.setDefaultNode(true);
        node2.setMainNetworkNode(true);
        node2.setChecked(false);
        node2.setChainId("103");
        node2.setFormatCorrect(false);
        nodeList.add(node2);

        Node node3 =new Node();
        node3.setNodeAddress("https://192.168.12.182");
        node3.setDefaultNode(true);
        node3.setMainNetworkNode(false);
        node3.setChecked(false);
        node3.setChainId("103");
        node3.setFormatCorrect(false);
        nodeList.add(node3);

        Flowable.fromIterable(nodeList)
                .map(new Function<Node, NodeEntity>() {
                    @Override
                    public NodeEntity apply(Node node) throws Exception {
                        return node.createNodeInfo();
                    }
                }).toList()
                .map(new Function<List<NodeEntity>, Boolean>() {
                    @Override
                    public Boolean apply(List<NodeEntity> nodeEntities) throws Exception {
                        return nodeEntities.size()> 0;
                    }
                }).subscribeOn(Schedulers.io())
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Boolean>() {

                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean.booleanValue()) {
                            System.out.println(R.string.save_node_succeed);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("NodesettingsPresenterTest", throwable.getMessage());
                    }
                });

    }


}
