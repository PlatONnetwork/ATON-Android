package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import com.platon.aton.R;
import com.platon.aton.component.adapter.NodeListAdapter;
import com.platon.aton.component.ui.contract.NodeSettingsContract;
import com.platon.aton.component.ui.presenter.NodeSettingsPresenter;
import com.platon.aton.component.widget.NodeListDecoration;
import com.platon.aton.component.widget.WrapContentLinearLayoutManager;
import com.platon.aton.entity.Node;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.AndroidUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NodeSettingsActivity extends BaseActivity<NodeSettingsContract.View, NodeSettingsPresenter> implements NodeSettingsContract.View {

    @BindView(R.id.list_nodes)
    RecyclerView listNodes;

    private Unbinder unbinder;
    private NodeListAdapter nodeListAdapter;

    @Override
    public NodeSettingsPresenter createPresenter() {
        return new NodeSettingsPresenter();
    }

    @Override
    public NodeSettingsContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        initView();
        getPresenter().fetchNodes();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_node_settings;
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    @Override
    public void notifyDataChanged(List<Node> nodeEntityList) {
        nodeListAdapter.notifyDataChanged(nodeEntityList);
    }

    @Override
    public void setChecked(int position) {
        nodeListAdapter.setChecked(position);
    }

    private void initView() {

        int padding = AndroidUtil.dip2px(this, 16);
        nodeListAdapter = new NodeListAdapter(this, null);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        listNodes.setLayoutManager(layoutManager);
        listNodes.addItemDecoration(new NodeListDecoration(this, padding, 0, padding, 0));
        listNodes.setAdapter(nodeListAdapter);

        nodeListAdapter.setOnItemCheckedListener((nodeEntity, isChecked) -> getPresenter().updateNode(nodeEntity, isChecked));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NodeSettingsActivity.class);
        context.startActivity(intent);
    }
}
