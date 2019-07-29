package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.DelegateRecordAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.DelegateRecordContract;
import com.juzix.wallet.component.ui.presenter.DelegateRecordPresenter;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 所有委托记录（委托和赎回委托）
 */

public class AllDelegateRecordFragment extends MVPBaseFragment<DelegateRecordPresenter> implements DelegateRecordContract.View {
    private Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rlv_list)
    ListView rlv_list;
    @BindView(R.id.layout_no_record)
    LinearLayout ll_no_data;
    private DelegateRecordAdapter mAdapter;

    @Override
    protected DelegateRecordPresenter createPresenter() {
        return new DelegateRecordPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {

    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showLongToast("" +"AllDelegateRecordFragment");
        initView();
    }

    private void initView() {
        //添加下拉刷新的header和加载更多的footer
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);//这个功能是本刷新库的特色功能：在列表滚动到底部时自动加载更多。 如果不想要这个功能，是可以关闭的
        mAdapter = new DelegateRecordAdapter(R.layout.item_delegate_record_list, null);
        rlv_list.setAdapter(mAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
//        int position = FragmentPagerItem.getPosition(getArguments());
//        showLongToast("" +position);
        showLongToast("" +"onResume"+"AllDelegateRecordFragment");
    }
}
