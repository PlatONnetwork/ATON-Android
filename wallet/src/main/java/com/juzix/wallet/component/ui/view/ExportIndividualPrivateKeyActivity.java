package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.widget.ViewPagerSlide;
import com.juzix.wallet.component.widget.table.PagerItem;
import com.juzix.wallet.component.widget.table.PagerItemAdapter;
import com.juzix.wallet.component.widget.table.PagerItems;
import com.juzix.wallet.component.widget.table.SmartTabLayout;

import java.util.ArrayList;

public class ExportIndividualPrivateKeyActivity extends BaseActivity {

    private final static String TAG = ExportIndividualPrivateKeyActivity.class.getSimpleName();

    public static void actionStart(Context context, String password) {
        Intent intent = new Intent(context, ExportIndividualPrivateKeyActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_PASSWORD, password);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_individual_private_key);
        showTipsDialog();
        initView();
    }

    private void initView() {
        int indicatorThickness = AndroidUtil.dip2px(getContext(), 2.0f);
        SmartTabLayout stbBar = mRootView.findViewById(R.id.stb_bar);
        stbBar.setIndicatorThickness(indicatorThickness);
        stbBar.setIndicatorCornerRadius(indicatorThickness / 2);
        ArrayList<Class<? extends BaseFragment>> fragments = getFragments();
        stbBar.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                return getTableView(position, container);
            }
        });
        PagerItems pages  = new PagerItems(getContext());
        int        tabNum = fragments.size();
        for (int i = 0; i < tabNum; i++) {
            pages.add(PagerItem.of(getTitles().get(i), fragments.get(i)));
        }
        ViewPagerSlide vpContent = mRootView.findViewById(R.id.vp_content);
        vpContent.setSlide(true);
        vpContent.setOffscreenPageLimit(fragments.size());
        vpContent.setAdapter(new PagerItemAdapter(getSupportFragmentManager(), pages));
        stbBar.setViewPager(vpContent);
        setTableView(stbBar.getTabAt(0), 0);
        vpContent.setCurrentItem(0);
    }

    private ArrayList<String> getTitles() {
        ArrayList<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.privateKey));
        titleList.add(getString(R.string.qrCode));
        return titleList;
    }

    private ArrayList<Class<? extends BaseFragment>> getFragments() {
        ArrayList<Class<? extends BaseFragment>> list = new ArrayList<>();
        list.add(ExportIndividualPrivateKeyFragment.class);
        list.add(ExportIndividualPrivateKeyQRCodeFragment.class);
        return list;
    }

    private void showTipsDialog() {
        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), string(R.string.donotScreenshot), string(R.string.backupPrivateKey), string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                if (fragment != null) {
                    fragment.dismiss();
                }
            }
        }).show(getSupportFragmentManager(), "showTips");
    }

    private View getTableView(int position, ViewGroup container) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.layout_app_tab_item1, container, false);
        setTableView(contentView, position);
        return contentView;
    }

    private void setTableView(View contentView, int position) {
        contentView.findViewById(R.id.iv_icon).setVisibility(View.GONE);
        TextView tvTitle = contentView.findViewById(R.id.tv_title);
        tvTitle.setText(getTitles().get(position));
        tvTitle.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.color_app_tab_text2));
    }
}
