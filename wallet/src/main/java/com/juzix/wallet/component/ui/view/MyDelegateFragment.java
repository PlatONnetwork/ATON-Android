package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.MyDelegateAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.MyDelegateContract;
import com.juzix.wallet.component.ui.presenter.MyDelegatePresenter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.juzix.wallet.component.widget.headerandfooter.HeaderAndFooterRecyclerView;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.DelegateInfo;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * 我的委托页面
 */

public class MyDelegateFragment extends MVPBaseFragment<MyDelegatePresenter> implements MyDelegateContract.View {
    private Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.iv_total_delegate)
    ImageView iconDelegate;
    @BindView(R.id.tv_total_delegate)
    TextView tv_total_delegate;
    @BindView(R.id.tv_delegate_record)
    TextView tv_delegate_record;
    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;
    @BindView(R.id.list_delegate)
    HeaderAndFooterRecyclerView list_delegate;
    @BindView(R.id.ll_problem)
    LinearLayout ll_problem;
    @BindView(R.id.ll_tutorial)
    LinearLayout ll_tutorial;
    @BindView(R.id.ll_guide)
    LinearLayout ll_guide;

    private MyDelegateAdapter mMyDelegateAdapter;
    private LinearLayoutManager layoutManager;
    List<DelegateInfo> datalist;

    @Override
    protected MyDelegatePresenter createPresenter() {
        return new MyDelegatePresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
//        mPresenter.loadMyDelegateData();
        refreshLayout.autoRefresh();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_delegate, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventPublisher.getInstance().register(this);
        initViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initClickListener();
    }

    private void initClickListener() {
        RxView.clicks(tv_delegate_record)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        DelegateRecordActivity.actionStart(getContext());
                    }
                });
        RxView.clicks(ll_problem).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo 暂时写的一个假的链接
//                        CommonHybridActivity.actionStart(getContext(), "https://www.baidu.com");
                        DelegateActivity.actionStart(getContext(), "", "", "", 0);
                    }
                });

        RxView.clicks(ll_tutorial).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo 暂时写的一个假链接
                        CommonHybridActivity.actionStart(getContext(), "https://www.baidu.com", WebType.WEB_TYPE_COMMON);
                    }
                });


    }


    private void initViews() {
        showLoadingDialog();
        initRefreshView();
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mMyDelegateAdapter = new MyDelegateAdapter(datalist);
        list_delegate.setLayoutManager(layoutManager);
//        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.include_my_delegate_footer, list_delegate, false);
        list_delegate.setAdapter(mMyDelegateAdapter);

        list_delegate.setEnabled(true);
        mMyDelegateAdapter.setOnItemClickListener(new MyDelegateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String walletAddress, String walletName, String walletIcon) {
                //跳转到委托详情页
                DelegateDetailActivity.actionStart(getContext(), walletAddress, walletName, walletIcon);
            }
        });


    }

    private void initRefreshView() {
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setEnableLoadMore(false);//禁止上拉加载更多
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadMyDelegateData();
            }
        });
    }


    public boolean isRecyclerScrollable() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) list_delegate.getLayoutManager();
        MyDelegateAdapter adapter = (MyDelegateAdapter) list_delegate.getAdapter();
        if (layoutManager == null || adapter == null) return false;
        return layoutManager.findLastCompletelyVisibleItemPosition() <= adapter.getItemCount() - 1;
    }

    /**
     * computeVerticalScrollExtent()  是当前屏幕显示的区域高度，
     * computeVerticalScrollOffset()  是当前屏幕之前滑过的距离
     * computeVerticalScrollRange()  是整个View控件的高度(RecycleView)
     *
     * @param recyclerView
     * @return
     */
    public boolean isSlideToBottom(RecyclerView recyclerView) {
//        if (recyclerView == null) {
//            return false;
//        }
//        LinearLayoutManager  linearLayoutManager  = (LinearLayoutManager) list_delegate.getLayoutManager();
//
//        int childCount = linearLayoutManager.getChildCount();
//        int totalHeight = 0;
//        for (int i = 0; i < childCount; i++) {
//            totalHeight += linearLayoutManager.getChildAt(i).getHeight();
//        }
//
//        if (totalHeight >= recyclerView.computeVerticalScrollRange()) {
//            return true;
//        }
//        return false;

        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        //屏幕中最后一个可见子项的position
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();
        //RecyclerView的滑动状态
        int state = recyclerView.getScrollState();
        if (visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1) {
            return true;
        } else {
            return false;
        }

    }


    //获取recyclerview滑动距离
    public int getScollYDistance() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) list_delegate.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisiableChildView = layoutManager.findViewByPosition(position);
        int itemHeight = firstVisiableChildView.getHeight();
        return (position) * itemHeight - firstVisiableChildView.getTop();
    }


    public void addFooterViewForRecyclerView() {
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.include_my_delegate_footer, list_delegate, false);
        LinearLayout ll_common_problem = footerView.findViewById(R.id.ll_common_problem);
        LinearLayout ll_use_guide = footerView.findViewById(R.id.ll_use_guide);

        list_delegate.removeFooterView(footerView);

        Log.d("MyDelegateFragment", "=============" + list_delegate.canScrollVertically(1) + "===================" + list_delegate.canScrollVertically(-1));

        LinearLayoutManager layoutManager = (LinearLayoutManager) list_delegate.getLayoutManager();
        //当前屏幕所看到的子项个数
        int visibleItemCount = layoutManager.getChildCount();
        //当前RecyclerView的所有子项个数
        int totalItemCount = layoutManager.getItemCount();


//        Log.d("MyDelegateFragment", "=============recyclerview滑动的距离" + "=" + getScollYDistance());
        Log.d("MyDelegateFragment", " ==================visibleItemCount" + visibleItemCount + "--------------------totalItemCount" + totalItemCount);

        Log.d("MyDelegateFragment", "---------------------屏幕显示高度computeVerticalScrollExtent" + "==" + list_delegate.computeVerticalScrollExtent() +
                "-----------recyclerview控件高度computeVerticalScrollRange" + "==" + list_delegate.computeVerticalScrollRange() +
                "================当前屏幕之前滑过的距离computeVerticalScrollOffset" + "==" + list_delegate.computeVerticalScrollOffset());

        Log.d("MyDelegateFragment", "是否可以滚动" + "=" + list_delegate.getLayoutManager().canScrollVertically());

//
//        if (visibleItemCount == 0 && list_delegate.computeVerticalScrollOffset() > 0) {
//            if (list_delegate.computeVerticalScrollExtent() >= list_delegate.computeVerticalScrollRange()) {
//                ll_guide.setVisibility(View.GONE);
//                list_delegate.addFooterView(footerView);
//            } else {
//                list_delegate.removeFooterView(footerView);
//                ll_guide.setVisibility(View.VISIBLE);
//            }
//
//        } else {


        if (visibleItemCount < totalItemCount) {
            ll_guide.setVisibility(View.GONE); //这里有问题
            list_delegate.addFooterView(footerView);
        } else if (list_delegate.canScrollVertically(1) || list_delegate.canScrollVertically(-1)) {
            ll_guide.setVisibility(View.GONE);
            list_delegate.addFooterView(footerView);
        } else {
            ll_guide.setVisibility(View.VISIBLE);
            list_delegate.removeFooterView(footerView);
        }
//
//        }


//        if (list_delegate.canScrollVertically(1) || list_delegate.canScrollVertically(-1)) { //超过recyclerview的高度啦
//            ll_guide.setVisibility(View.GONE);
//            list_delegate.addFooterView(footerView);
//        } else {
//            ll_guide.setVisibility(View.VISIBLE);
//            list_delegate.removeFooterView(footerView);
//        }


        RxView.clicks(ll_common_problem).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo 暂时写的一个假链接
                        CommonHybridActivity.actionStart(getContext(), "https://www.baidu.com",WebType.WEB_TYPE_COMMON);
                    }
                });


        RxView.clicks(ll_use_guide).compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        //todo 暂时写的一个假链接
                        CommonHybridActivity.actionStart(getContext(), "https://www.jd.com",WebType.WEB_TYPE_COMMON);
                    }
                });

    }

    @Override
    public void showMyDelegateData(List<DelegateInfo> list) {

        if (list != null && list.size() > 0) {
            mMyDelegateAdapter.notifyDataChanged(list);
            ll_no_data.setVisibility(View.GONE);
            showTotal(list);
            addFooterViewForRecyclerView();
        } else {
            ll_no_data.setVisibility(View.VISIBLE);
        }
        refreshLayout.finishRefresh();
        dismissLoadingDialogImmediately();


    }

    private void showTotal(List<DelegateInfo> list) {
        double total = 0;
        for (DelegateInfo info : list) {
            total += NumberParserUtils.parseDouble(NumberParserUtils.parseDouble(NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(info.getDelegate(), "1E18"))));
        }

        tv_total_delegate.setText(total > 0 ? StringUtil.formatBalance(total, false) : "— —");
    }


    @Override
    public void showMyDelegateDataByPosition(int positon, DelegateInfo delegateInfo) {
        //刷新某个item
        mMyDelegateAdapter.notifyItemDataChanged(positon, delegateInfo);
        refreshLayout.finishRefresh();

    }

    @Override
    public void showMyDelegateDataFailed() {
        refreshLayout.finishRefresh();
        mMyDelegateAdapter.notifyDataSetChanged();
        dismissLoadingDialogImmediately();
    }


    //接收event事件然后刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMyDelegate(Event.UpdateDelegateTabEvent tabEvent) {
        if (AppSettings.getInstance().getMydelegateTab()) {
            refreshLayout.autoRefresh();
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            AppSettings.getInstance().setMydelegateTab(true);
        } else {
            AppSettings.getInstance().setMydelegateTab(false);
        }

    }


//        Log.d("MyDelegateFragment", "======获取recyclerview的滚动状态===================" + list_delegate.getScrollState());
//
//        if (list_delegate.getScrollY() == 0) {
//            ll_guide.setVisibility(View.VISIBLE);
//            list_delegate.removeFooterView(footerView);
//        }
//
//        list_delegate.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                Log.d("MyDelegateFragment", "======获取recyclerview的滚动状态=======onScrollStateChanged============" + newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                Log.d("MyDelegateFragment", "======获取recyclerview的滚动状态=======onScrollxxxxxxxxxxxxxxx============" + list_delegate.getScrollState());
//
////                Log.d("MyDelegateFragment", "滚动的距离" + "==========================" + dy + "=====================" + dx);
//                list_delegate.removeFooterView(footerView);
//                if (dy > 0 || dy < 0) { //表示有滚动的状态此时
//                    list_delegate.addFooterView(footerView);
//                    ll_guide.setVisibility(View.GONE);
//
//                } else {
//                    list_delegate.removeFooterView(footerView);
//                    ll_guide.setVisibility(View.VISIBLE);
//                }
//
//            }
//        });
}
