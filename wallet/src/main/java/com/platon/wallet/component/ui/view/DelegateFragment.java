package com.platon.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.platon.framework.util.AndroidUtil;
import com.platon.wallet.R;
import com.platon.wallet.component.ui.base.BaseFragment;
import com.platon.wallet.component.widget.ViewPagerSlide;
import com.platon.wallet.component.widget.table.PagerItem;
import com.platon.wallet.component.widget.table.PagerItemAdapter;
import com.platon.wallet.component.widget.table.PagerItems;
import com.platon.wallet.component.widget.table.CustomTabLayout;

import java.util.ArrayList;


/**
 * 委托模块
 */
public class DelegateFragment extends BaseFragment {

    @IntDef({
            DelegateTab.MY_DELEGATE_TAB,
            DelegateTab.VALIDATORS_TAB
    })
    public @interface DelegateTab {
        int MY_DELEGATE_TAB = 0;
        int VALIDATORS_TAB = 1;
    }

    private ViewPagerSlide vpContent;

    private CustomTabLayout stbBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delegate, container, false);
        initView(view);
        return view;
    }

    public void setCurrentTab(@DelegateTab int delegateTab) {
        vpContent.setCurrentItem(delegateTab);
    }

    private void initView(View view) {
        stbBar = view.findViewById(R.id.stb_bar);
        int indicatorThickness = AndroidUtil.dip2px(getContext(), 2.0f);
        stbBar.setIndicatorThickness(indicatorThickness + 4);
        indicatorThickness = indicatorThickness + 4;

        stbBar.setIndicatorCornerRadius(indicatorThickness / 2);
        ArrayList<Class<? extends BaseFragment>> fragments = getFragments();
        stbBar.setCustomTabView(new CustomTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });

        PagerItems pages = new PagerItems(getContext());
        int tabNum = fragments.size();
        for (int i = 0; i < tabNum; i++) {
            pages.add(PagerItem.of(getTitles().get(i), fragments.get(i)));
        }
        vpContent = view.findViewById(R.id.vp_content);
        vpContent.setSlide(true);
        vpContent.setOffscreenPageLimit(fragments.size());
        vpContent.setAdapter(new PagerItemAdapter(getChildFragmentManager(), pages));
        stbBar.setViewPager(vpContent);
        setTableView(stbBar.getTabAt(0), 0);
        vpContent.setCurrentItem(0);
        initTab();
    }

    private void initTab() {
//        stbBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int i, float v, int i1) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                switch (position) {
//                    case 0:
//                    case 1:
//                        //切换tab刷新页面（0.7.3.1）
//                        EventPublisher.getInstance().sendTabChangeUpdateValidatorsEvent();
//                        break;
//                }
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });

    }

    private View getTableView(int position, ViewGroup container) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item3, container, false);
        setTableView(contentView, position);
        return contentView;
    }

    private void setTableView(View contentView, int position) {
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text3));
    }

    private ArrayList<Class<? extends BaseFragment>> getFragments() {
        ArrayList<Class<? extends BaseFragment>> list = new ArrayList<>();
        list.add(MyDelegateFragment.class);
        list.add(ValidatorsFragment.class);
        return list;
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.tab_my_delegate));
        titleList.add(getString(R.string.tab_validators));
        return titleList;
    }

}
