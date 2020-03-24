package com.platon.aton.component.ui.view;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.adapter.MyDelegateAdapter;
import com.platon.aton.component.ui.contract.MyDelegateContract;
import com.platon.aton.component.ui.dialog.BaseDialogFragment;
import com.platon.aton.component.ui.dialog.CommonGuideDialogFragment;
import com.platon.aton.component.ui.presenter.MyDelegatePresenter;
import com.platon.aton.component.widget.CustomRefreshHeader;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.entity.DelegateInfo;
import com.platon.aton.entity.GuideType;
import com.platon.aton.entity.WebType;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DateUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseLazyFragment;
import com.platon.framework.utils.PreferenceTool;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;


/**
 * 我的委托页面
 */

public class MyDelegateFragment extends BaseLazyFragment<MyDelegateContract.View, MyDelegatePresenter> implements MyDelegateContract.View {

    @IntDef({
            TotalAmountType.TOTAL_DELEGATED,
            TotalAmountType.TOTAL_REWARD,
            TotalAmountType.TOTAL_UNCLAIMED_REWARD
    })
    public @interface TotalAmountType {
        /**
         * 累计委托
         */
        int TOTAL_DELEGATED = 0;
        /**
         * 累计提取奖励
         */
        int TOTAL_REWARD = 1;
        /**
         * 累计未提取的奖励
         */
        int TOTAL_UNCLAIMED_REWARD = 2;
    }

    @BindView(R.id.layout_refresh)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.tv_total_delegated_amount)
    TextView totalDelegatedAmountTv;
    @BindView(R.id.tv_total_unclaimed_reward_amount)
    TextView totalUnclaimedRewardAmountTv;
    @BindView(R.id.tv_total_reward_amount)
    TextView totalRewardAmountTv;
    @BindView(R.id.tv_delegation_rec)
    TextView delegationRecTv;
    @BindView(R.id.tv_claim_rec)
    TextView claimRecTv;
    @BindView(R.id.ll_no_data)
    LinearLayout noDataLl;
    @BindView(R.id.tv_no_data_tips)
    TextView noDataTipsTv;
    @BindView(R.id.list_my_delegate)
    RecyclerView myDelegateList;
    @BindView(R.id.ll_problem)
    LinearLayout problemLl;
    @BindView(R.id.ll_tutorial)
    LinearLayout tutorialLl;
    @BindString(R.string.msg_no_delegated_data_tips)
    String noDelegatedDataTipsMsg;
    @BindString(R.string.msg_delegate)
    String delegateMsg;
    @BindView(R.id.layout_my_delegate_top)
    FrameLayout myDelegateTopLayout;

    private Unbinder unbinder;
    private MyDelegateAdapter mMyDelegateAdapter;

    @Override
    public MyDelegatePresenter createPresenter() {
        return new MyDelegatePresenter();
    }

    @Override
    public MyDelegateContract.View createView() {
        return null;
    }

    @Override
    public void init(View rootView) {
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_delegate;
    }

    @Override
    public void onFragmentFirst() {
        super.onFragmentFirst();
        refreshLayout.autoRefresh();
    }

    @Override
    public void onFragmentVisible() {
        super.onFragmentVisible();
        MobclickAgent.onPageStart(Constants.UMPages.MY_DELEGATION);
    }

    @Override
    public void onFragmentInVisible() {
        super.onFragmentInVisible();
        MobclickAgent.onPageEnd(Constants.UMPages.MY_DELEGATION);
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawable(myDelegateTopLayout,
                DensityUtil.dp2px(getContext(), 4),
                ContextCompat.getColor(getContext(), R.color.color_cc9ca7c2),
                DensityUtil.dp2px(getContext(), 10),
                0,
                DensityUtil.dp2px(getContext(), 2));

        CommonTextUtils.richText(noDataTipsTv, noDelegatedDataTipsMsg, delegateMsg, new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((DelegateFragment) getParentFragment()).setCurrentTab(DelegateFragment.DelegateTab.VALIDATORS_TAB);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setFakeBoldText(true);
                ds.setColor(getResources().getColor(R.color.color_105cfe));
            }
        });
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPresenter().loadMyDelegateData();
            }
        });
        mMyDelegateAdapter = new MyDelegateAdapter();
        myDelegateList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        myDelegateList.setAdapter(mMyDelegateAdapter);
        mMyDelegateAdapter.setOnItemClickListener(new MyDelegateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DelegateInfo delegateInfo) {
                DelegateDetailActivity.actionStart(getContext(), delegateInfo);
            }

            @Override
            public void onClaimRewardClick(DelegateInfo delegateInfo, int position) {

                long currentTime = System.currentTimeMillis();

                if (!TransactionManager.getInstance().isAllowSendTransaction(delegateInfo.getWalletAddress(), currentTime)) {
                    showLongToast(string(R.string.msg_wait_finished_transaction_tips, DateUtil.millisecondToMinutes(TransactionManager.getInstance().getSendTransactionTimeInterval(delegateInfo.getWalletAddress(), currentTime))));
                    return;
                }

                getPresenter().withdrawDelegateReward(delegateInfo, position);
            }
        });

        RxView
                .clicks(problemLl)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), getResources().getString(R.string.web_url_common_delegate_problem), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView
                .clicks(tutorialLl)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        CommonHybridActivity.actionStart(getContext(), getResources().getString(R.string.web_url_tutorial), WebType.WEB_TYPE_COMMON);
                    }
                });

        RxView
                .clicks(delegationRecTv)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        DelegateRecordActivity.actionStart(currentActivity());
                    }
                });

        RxView
                .clicks(claimRecTv)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        ClaimRewardRecordActivity.actionStart(currentActivity());
                    }
                });

        initGuide();
    }

    private void initGuide() {
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_MY_DELEGATE, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE_LIST).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_MY_DELEGATE, true);
                }
            }).show(getActivity().getSupportFragmentManager(), "showGuideDialogFragment");
        }
    }


    @Override
    public void showMyDelegateData(List<DelegateInfo> list) {
        refreshLayout.finishRefresh();
        noDataLl.setVisibility(list != null && list.size() > 0 ? View.GONE : View.VISIBLE);
        mMyDelegateAdapter.notifyDataChanged(list);
        SparseArray<String> totalAmountArray = getTotalAmountArray(list);

        String totalDelegated = totalAmountArray.get(TotalAmountType.TOTAL_DELEGATED);

        if (TextUtils.isEmpty(totalDelegated)) {
            totalDelegatedAmountTv.setText(AmountUtil.formatAmountText(totalDelegated));
        } else {
            totalDelegatedAmountTv.setText(CommonTextUtils.getPriceTextWithBold(AmountUtil.formatAmountText(totalAmountArray.get(TotalAmountType.TOTAL_DELEGATED)),
                    ContextCompat.getColor(currentActivity(), R.color.color_f9fbff), ContextCompat.getColor(currentActivity(), R.color.color_f9fbff), DensityUtil.sp2px(currentActivity(), 12), DensityUtil.sp2px(currentActivity(), 22)));
        }
        totalRewardAmountTv.setText(AmountUtil.formatAmountText(totalAmountArray.get(TotalAmountType.TOTAL_REWARD), 8));
        totalUnclaimedRewardAmountTv.setText(AmountUtil.formatAmountText(totalAmountArray.get(TotalAmountType.TOTAL_UNCLAIMED_REWARD), 8));
    }

    @Override
    public void notifyItemChanged(boolean isPending, int position) {
        mMyDelegateAdapter.notifyItemDataChanged(isPending, position);
    }

    private SparseArray<String> getTotalAmountArray(List<DelegateInfo> list) {

        SparseArray<String> sparseArray = new SparseArray<String>(3);

        if (list == null) {
            sparseArray.put(TotalAmountType.TOTAL_DELEGATED, null);
            sparseArray.put(TotalAmountType.TOTAL_REWARD, null);
            sparseArray.put(TotalAmountType.TOTAL_UNCLAIMED_REWARD, null);
            return sparseArray;
        }

        if (list.isEmpty()) {
            sparseArray.put(TotalAmountType.TOTAL_DELEGATED, BigDecimal.ZERO.toPlainString());
            sparseArray.put(TotalAmountType.TOTAL_REWARD, BigDecimal.ZERO.toPlainString());
            sparseArray.put(TotalAmountType.TOTAL_UNCLAIMED_REWARD, BigDecimal.ZERO.toPlainString());
            return sparseArray;
        }


        return Flowable
                .fromIterable(list)
                .reduce(sparseArray, new BiFunction<SparseArray<String>, DelegateInfo, SparseArray<String>>() {
                    @Override
                    public SparseArray<String> apply(SparseArray<String> bigDecimalSparseArray, DelegateInfo delegateInfo) throws Exception {
                        bigDecimalSparseArray.put(TotalAmountType.TOTAL_DELEGATED, BigDecimalUtil.add(bigDecimalSparseArray.get(TotalAmountType.TOTAL_DELEGATED), delegateInfo.getDelegated()).toPlainString());
                        bigDecimalSparseArray.put(TotalAmountType.TOTAL_REWARD, BigDecimalUtil.add(bigDecimalSparseArray.get(TotalAmountType.TOTAL_REWARD), delegateInfo.getCumulativeReward()).toPlainString());
                        bigDecimalSparseArray.put(TotalAmountType.TOTAL_UNCLAIMED_REWARD, BigDecimalUtil.add(bigDecimalSparseArray.get(TotalAmountType.TOTAL_UNCLAIMED_REWARD), delegateInfo.getWithdrawReward()).toPlainString());
                        return bigDecimalSparseArray;
                    }
                })
                .blockingGet();
    }


    //接收event事件然后刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshMyDelegate(Event.UpdateDelegateTabEvent tabEvent) {
        if (PreferenceTool.getBoolean(Constants.Preference.KEY_MYDELEGATETAB, true)) {
            refreshLayout.autoRefresh();
        }

    }

    //接收tab切换到当前页面的时候，刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshTabChange(Event.UpdateTabChangeEvent event) {
        refreshLayout.autoRefresh();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        PreferenceTool.putBoolean(Constants.Preference.KEY_MYDELEGATETAB, isVisibleToUser);
        PreferenceTool.getBoolean(Constants.Preference.KEY_MYDELEGATETAB, true);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTransactionEvent(Event.UpdateTransactionEvent event) {
        getPresenter().loadMyDelegateData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMyDelegateGuideEvent(Event.MyDelegateGuide event) {
        if (!PreferenceTool.getBoolean(Constants.Preference.KEY_SHOW_MY_DELEGATE, false)) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE_LIST).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    PreferenceTool.putBoolean(Constants.Preference.KEY_SHOW_MY_DELEGATE, true);
                }
            }).show(getActivity().getSupportFragmentManager(), "showGuideDialogFragment");
        }
    }
}
