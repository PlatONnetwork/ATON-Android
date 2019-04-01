package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.juzhen.framework.util.AndroidUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.utils.LanguageUtil;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SwitchLanguageActivity extends BaseActivity {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_switch_chinese)
    TextView tvSwitchChinese;
    @BindView(R.id.tv_switch_english)
    TextView tvSwitchEnglish;
    @BindString(R.string.language)
    String language;
    @BindString(R.string.save)
    String save;

    private Unbinder unbinder;
    private Locale locale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_language);
        unbinder = ButterKnife.bind(this);
        initViews();
    }

    private void initViews() {
        TextView tvRignt = commonTitleBar.findViewById(R.id.tv_right);
        tvRignt.setTextSize(13);
        tvRignt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchLanguage(locale);
            }
        });

        locale = LanguageUtil.getLocale(this);

        updateSelectedLanguageStatus(locale);
    }

    private void switchLanguage(Locale locale) {
        LanguageUtil.switchLanguage(this, locale);
    }

    private void updateSelectedLanguageStatus(Locale locale) {

        if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
            tvSwitchChinese.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_hook_s, 0);
            tvSwitchEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else {
            tvSwitchChinese.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            tvSwitchEnglish.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_hook_s, 0);
        }
    }

    @OnClick({R.id.tv_switch_chinese, R.id.tv_switch_english})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_switch_chinese:
                locale = Locale.CHINESE;
                updateSelectedLanguageStatus(Locale.CHINESE);
                break;
            case R.id.tv_switch_english:
                locale = Locale.ENGLISH;
                updateSelectedLanguageStatus(Locale.ENGLISH);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SwitchLanguageActivity.class);
        context.startActivity(intent);
    }
}
