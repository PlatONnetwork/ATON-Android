package com.platon.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import com.platon.framework.util.AndroidUtil;
import com.platon.wallet.R;
import com.platon.wallet.component.adapter.NodeListAdapter;
import com.platon.wallet.component.ui.base.MVPBaseActivity;
import com.platon.wallet.component.ui.contract.NodeSettingsContract;
import com.platon.wallet.component.ui.presenter.NodeSettingsPresenter;
import com.platon.wallet.component.widget.NodeListDecoration;
import com.platon.wallet.component.widget.WrapContentLinearLayoutManager;
import com.platon.wallet.entity.Node;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NodeSettingsActivity extends MVPBaseActivity<NodeSettingsPresenter> implements NodeSettingsContract.View {

    @BindView(R.id.list_nodes)
    RecyclerView listNodes;

    private Unbinder unbinder;
    private NodeListAdapter nodeListAdapter;

    @Override
    protected NodeSettingsPresenter createPresenter() {
        return new NodeSettingsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_settings);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.fetchNodes();
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

        nodeListAdapter.setOnItemCheckedListener((nodeEntity, isChecked) -> mPresenter.updateNode(nodeEntity, isChecked));

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
