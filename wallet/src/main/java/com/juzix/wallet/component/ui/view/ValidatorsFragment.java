package com.juzix.wallet.component.ui.view;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewEditorActionEvent;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.ValidatorsAdapter;
import com.juzix.wallet.component.adapter.VerifyNodeDiffCallback;
import com.juzix.wallet.component.ui.OnItemClickListener;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.component.ui.dialog.BaseDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonGuideDialogFragment;
import com.juzix.wallet.component.ui.dialog.SelectSortTypeDialogFragment;
import com.juzix.wallet.component.ui.presenter.ValidatorsPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.GuideType;
import com.juzix.wallet.entity.NodeStatus;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * 验证节点页面
 */

public class ValidatorsFragment extends MVPBaseFragment<ValidatorsPresenter> implements ValidatorsContract.View {

    @IntDef({
            Tab.ALL,
            Tab.ACTIVE,
            Tab.CANDIDATE
    })
    public @interface Tab {
        /**
         * 所有
         */
        int ALL = 0;
        /**
         * 活跃中
         */
        int ACTIVE = 1;
        /**
         * 候选中
         */
        int CANDIDATE = 2;
    }

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;
    @BindView(R.id.btn_all)
    RadioButton allBtn;
    @BindView(R.id.btn_active)
    RadioButton activeBtn;
    @BindView(R.id.btn_candidate)
    RadioButton candidateBtn;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.list_validators)
    RecyclerView validatorsList;
    @BindView(R.id.layout_no_data)
    LinearLayout noDataLayout;
    @BindView(R.id.iv_search)
    ImageView searchIv;
    @BindView(R.id.iv_rank)
    ImageView rankIv;
    @BindView(R.id.et_search)
    EditText searchEt;
    @BindView(R.id.layout_search)
    ConstraintLayout searchLayout;
    @BindView(R.id.tv_hide_search)
    TextView hideSearchTv;
    @BindView(R.id.iv_clear)
    ImageView clearIv;

    private Unbinder unbinder;

    private SortType mSortType = SortType.SORTED_BY_NODE_RANKINGS;
    private int mTab = Tab.ALL;

    private ValidatorsAdapter mValidatorsAdapter;

    @Override

    protected ValidatorsPresenter createPresenter() {
        return new ValidatorsPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {

    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_validators, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventPublisher.getInstance().register(this);
        initView();
        return view;
    }

    private void initView() {

        mValidatorsAdapter = new ValidatorsAdapter();
        validatorsList.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        validatorsList.setAdapter(mValidatorsAdapter);

        RxRadioGroup
                .checkedChanges(radioGroup)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void accept(Integer id) {
                        tabStateChanged(getTabById(id));
                    }
                });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.loadValidatorsData(getNodeStatusByTab(mTab), mSortType, searchEt.getText().toString().trim(), true);
            }
        });

        mValidatorsAdapter.setOnItemClickListener(new OnItemClickListener<VerifyNode>() {
            @Override
            public void onItemClick(VerifyNode verifyNode) {
                ValidatorsDetailActivity.actionStart(getContext(), verifyNode.getNodeId());
            }
        });

        RxView
                .clicks(rankIv)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        SelectSortTypeDialogFragment
                                .newInstance(mSortType)
                                .setOnItemClickListener(new OnItemClickListener<SortType>() {
                                    @Override
                                    public void onItemClick(SortType sortType) {
                                        mSortType = sortType;
                                        mPresenter.loadValidatorsData(getNodeStatusByTab(mTab), mSortType, searchEt.getText().toString().trim(), false);
                                    }
                                })
                                .show(getActivity()
                                        .getSupportFragmentManager(), "showSelectSortTypeDialogFragment");
                    }
                });

        RxView
                .clicks(searchIv)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        searchLayout.setVisibility(View.VISIBLE);
                    }
                });

        RxView
                .clicks(hideSearchTv)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        searchLayout.setVisibility(View.GONE);
                        searchEt.setText("");
                    }
                });

        RxTextView
                .textChanges(searchEt)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        clearIv.setVisibility(TextUtils.isEmpty(charSequence) ? View.GONE : View.VISIBLE);
                    }
                });

        RxView
                .clicks(clearIv)
                .compose(RxUtils.bindToLifecycle(this))
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        searchEt.setText("");
                        mPresenter.loadValidatorsData(getNodeStatusByTab(mTab), mSortType, searchEt.getText().toString().trim(), false);
                    }
                });

        RxTextView
                .editorActionEvents(searchEt, new Predicate<TextViewEditorActionEvent>() {
                    @Override
                    public boolean test(TextViewEditorActionEvent textViewEditorActionEvent) throws Exception {
                        return textViewEditorActionEvent.actionId() == EditorInfo.IME_ACTION_SEARCH;
                    }
                })
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<TextViewEditorActionEvent>() {
                    @Override
                    public void accept(TextViewEditorActionEvent textViewEditorActionEvent) {
                        mPresenter.loadValidatorsData(getNodeStatusByTab(mTab), mSortType, searchEt.getText().toString().trim(), false);
                    }
                });

        tabStateChanged(mTab);

    }

    private @NodeStatus
    String getNodeStatusByTab(@Tab int tab) {

        switch (tab) {
            case Tab.ACTIVE:
                return NodeStatus.ACTIVE;
            case Tab.CANDIDATE:
                return NodeStatus.CANDIDATE;
            default:
                return NodeStatus.ALL;

        }

    }

    private void tabStateChanged(@Tab int tab) {
        mTab = tab;
        mPresenter.loadValidatorsData(getNodeStatusByTab(mTab), mSortType, searchEt.getText().toString().trim(), false);
        tabCheckedChanged(tab);
    }

    private @Tab
    int getTabById(int id) {
        switch (id) {
            case R.id.btn_active:
                return Tab.ACTIVE;
            case R.id.btn_candidate:
                return Tab.CANDIDATE;
            default:
                return Tab.ALL;
        }
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart(Constants.UMPages.VERIFY_NODE);
        super.onResume();
        refreshLayout.autoRefresh();
    }


    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.VERIFY_NODE);
        super.onPause();
    }

    @Override
    public void loadValidatorsDataResult(List<VerifyNode> oldVerifyNodeList, List<VerifyNode> newVerifyNodeList) {
        refreshLayout.finishRefresh();
        noDataLayout.setVisibility(newVerifyNodeList == null || newVerifyNodeList.isEmpty() ? View.VISIBLE : View.GONE);
        VerifyNodeDiffCallback diffCallback = new VerifyNodeDiffCallback(oldVerifyNodeList, newVerifyNodeList);
        mValidatorsAdapter.setDatas(newVerifyNodeList);
        DiffUtil.calculateDiff(diffCallback, true).dispatchUpdatesTo(mValidatorsAdapter);
    }


    public void tabCheckedChanged(@Tab int tab) {

        switch (tab) {
            case Tab.ALL:
                allBtn.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                activeBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                candidateBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                allBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
                allBtn.setTypeface(Typeface.DEFAULT_BOLD);
                activeBtn.setTypeface(Typeface.DEFAULT);
                candidateBtn.setTypeface(Typeface.DEFAULT);
                activeBtn.setTextColor(getResources().getColor(R.color.color_000000));
                candidateBtn.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case Tab.ACTIVE:
                activeBtn.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                candidateBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                allBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                allBtn.setTypeface(Typeface.DEFAULT);
                activeBtn.setTypeface(Typeface.DEFAULT_BOLD);
                candidateBtn.setTypeface(Typeface.DEFAULT);
                activeBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
                allBtn.setTextColor(getResources().getColor(R.color.color_000000));
                candidateBtn.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case Tab.CANDIDATE:
                candidateBtn.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                allBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                activeBtn.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                allBtn.setTypeface(Typeface.DEFAULT);
                activeBtn.setTypeface(Typeface.DEFAULT);
                candidateBtn.setTypeface(Typeface.DEFAULT_BOLD);
                candidateBtn.setTextColor(getResources().getColor(R.color.color_ffffff));
                allBtn.setTextColor(getResources().getColor(R.color.color_000000));
                activeBtn.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            default:
                break;
        }
    }

    //接收event事件然后刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshValidators(Event.UpdateValidatorsTabEvent tabEvent) {
        if (AppSettings.getInstance().getValidatorsTab()) {
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
        if (isVisibleToUser) {
            AppSettings.getInstance().setValidatorsTab(true);
        } else {
            AppSettings.getInstance().setValidatorsTab(false);
        }
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
    public void showValidatorsGuide(Event.ValidatorsGuide event) {

        if (!AppSettings.getInstance().getValidatorsBoolean()) {
            CommonGuideDialogFragment.newInstance(GuideType.DELEGATE_VALIDATORS).setOnDissmissListener(new BaseDialogFragment.OnDissmissListener() {
                @Override
                public void onDismiss() {
                    AppSettings.getInstance().setValidatorsBoolean(true);
                }
            }).show(getActivity().getSupportFragmentManager(), "showGuideDialogFragment");
        }
    }
}
