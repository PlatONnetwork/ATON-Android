package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.component.ui.presenter.ValidatorsPresenter;
import com.juzix.wallet.component.widget.CustomRefreshFooter;
import com.juzix.wallet.component.widget.CustomRefreshHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 验证节点页面
 */

public class ValidatorsFragment extends MVPBaseFragment<ValidatorsPresenter> implements ValidatorsContract.View, OnClickListener {
    private Unbinder unbinder;

    @BindView(R.id.btn_all)
    Button all;
    @BindView(R.id.btn_active)
    Button active;
    @BindView(R.id.btn_candidate)
    Button candidate;
    @BindView(R.id.tv_rank)
    TextView tv_rank;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rlv_list)
    ListView rlv_list;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_validators, container, false);
//        initView(view);
//        return view;
//
//    }

//    private void initView(View view) {
//        SmartTabLayout stbBar = view.findViewById(R.id.stb_bar);
//        ViewPagerSlide vpContent = view.findViewById(R.id.vp_content);
////        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
////            @Override
////            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
////                return getTableView(position, container);
////            }
////        });
//        PagerItems pages = new PagerItems(getContext());
//        int size = getTitles().size();
//        for (int i = 0; i < size; i++) {
//            pages.add(PagerItem.of(getTitles().get(i), ValidatorNodeListFragment.class));
//        }
//        PagerItemAdapter adapter = new PagerItemAdapter(getChildFragmentManager(), pages);
//        vpContent.setAdapter(adapter);
//        stbBar.setViewPager(vpContent);
//        vpContent.setSlide(false);
//        vpContent.setCurrentItem(0);
//    }

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
        initView(view);
        return view;
    }

    private void initView(View view) {
        changeBtnState(R.id.btn_all);
        refreshLayout.setRefreshHeader(new CustomRefreshHeader(getContext()));
        refreshLayout.setRefreshFooter(new CustomRefreshFooter(getContext()));
        refreshLayout.setEnableLoadMore(true);//启用上拉加载功能
        refreshLayout.setEnableAutoLoadMore(false);
        all.setOnClickListener(this);
        active.setOnClickListener(this);
        candidate.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_all:
                changeBtnState(id);
                showLongToast("所有");
                break;
            case R.id.btn_active:
                changeBtnState(id);
                showLongToast("活跃中");
                break;
            case R.id.btn_candidate:
                changeBtnState(id);
                showLongToast("候选中");
                break;
            default:
                break;
        }


    }

    public void changeBtnState(int id) {
        switch (id) {
            case R.id.btn_all:
                all.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                active.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                all.setTextColor(getResources().getColor(R.color.color_ffffff));
                active.setTextColor(getResources().getColor(R.color.color_000000));
                candidate.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case R.id.btn_active:
                active.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                all.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                active.setTextColor(getResources().getColor(R.color.color_ffffff));
                all.setTextColor(getResources().getColor(R.color.color_000000));
                candidate.setTextColor(getResources().getColor(R.color.color_000000));
                break;
            case R.id.btn_candidate:
                candidate.setBackgroundResource(R.drawable.bg_delegate_textview_choosed);
                all.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                active.setBackgroundResource(R.drawable.bg_delegate_textview_unchoosed);
                candidate.setTextColor(getResources().getColor(R.color.color_ffffff));
                all.setTextColor(getResources().getColor(R.color.color_000000));
                active.setTextColor(getResources().getColor(R.color.color_000000));
                break;
        }
    }

}
