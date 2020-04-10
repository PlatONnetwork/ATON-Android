package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.component.widget.ViewPagerSlide;
import com.platon.aton.component.widget.table.PagerItem;
import com.platon.aton.component.widget.table.PagerItemAdapter;
import com.platon.aton.component.widget.table.PagerItems;
import com.platon.aton.component.widget.table.SmartTabLayout;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BaseFragment;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.utils.AndroidUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * 委托记录
 *
 * @author ziv
 */

public class DelegateRecordActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_delegate_record;
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    @Override
    public BaseViewImp createView() {
        return null;
    }

    @Override
    public void init() {
        initView();
    }

    @Override
    public void onResume() {
        MobclickAgent.onPageStart(Constants.UMPages.DELEGATE_NODE_RECORD);
        super.onResume();
    }

    @Override
    public void onPause() {
        MobclickAgent.onPageEnd(Constants.UMPages.DELEGATE_NODE_RECORD);
        super.onPause();
    }

    private void initView() {
        SmartTabLayout stbBar = getContentView().findViewById(R.id.stb_bar);
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

        ViewPagerSlide vpContent = getContentView().findViewById(R.id.vp_content);
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
        list.add(WithdrawDelegateRecordFragment.class);
        return list;

    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, DelegateRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }
}
