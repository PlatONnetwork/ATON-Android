package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.component.widget.table.PagerItem;
import com.juzix.wallet.component.widget.table.PagerItemAdapter;
import com.juzix.wallet.component.widget.table.PagerItems;
import com.juzix.wallet.component.widget.table.SmartTabLayout;

import java.util.ArrayList;

/**
 * 委托记录
 */

public class DelegateRecordActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegate_record);
        initView();
    }

    private void initView() {
        SmartTabLayout stbBar = mRootView.findViewById(R.id.stb_bar);
        int indicatorThickness = AndroidUtil.dip2px(getContext(), 2.0f);
        stbBar.setIndicatorThickness(indicatorThickness);//设置指示器的厚度
        stbBar.setIndicatorCornerRadius(indicatorThickness / 2);//指示器圆角半径
        ArrayList<Class<? extends BaseFragment>> fragments = getFragments();
        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
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

        ViewPagerSlide vpContent = mRootView.findViewById(R.id.vp_content);
        vpContent.setSlide(true);//设置是否可滑动
//        vpContent.setOffscreenPageLimit(fragments.size());//设置预加载
        vpContent.setAdapter(new PagerItemAdapter(getSupportFragmentManager(), pages));
        stbBar.setViewPager(vpContent);

        setTableView(stbBar.getTabAt(0), 0);
        vpContent.setCurrentItem(0);
    }

    private View getTableView(int position, ViewGroup container) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item2, container, false);
        setTableView(contentView, position);
        return contentView;
    }

    private void setTableView(View contentView, int position) {
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setTextSize(15);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text2));
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.all_record));
        titleList.add(getString(R.string.delegate));
        titleList.add(getString(R.string.undelegate));
        return titleList;
    }

    private ArrayList<Class<? extends BaseFragment>> getFragments() {
        ArrayList<Class<? extends BaseFragment>> list = new ArrayList<>();
        list.add(AllDelegateRecordFragment.class);
        list.add(DelegateRecordFragment.class);
        list.add(UnDelegateRecordFragment.class);
        return list;

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DelegateRecordActivity.class);
        context.startActivity(intent);
    }

}
