package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzix.wallet.App;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.entity.WebType;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.RxUtils;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_wallet_manage)
    LinearLayout layoutWalletManage;
    @BindView(R.id.layout_wallet_transations)
    LinearLayout layoutWalletTransations;
    @BindView(R.id.layout_wallet_address_book)
    LinearLayout layoutWalletAddressBook;
    @BindView(R.id.layout_settings)
    LinearLayout layoutSettings;
    @BindView(R.id.layout_official_community)
    LinearLayout layoutOfficialCommunity;
    @BindView(R.id.layout_about)
    LinearLayout layoutAbout;
    @BindView(R.id.layout_support_feedback)
    LinearLayout layoutSupportFeedback;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    private void init() {
        RxView.clicks(layoutWalletManage)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        WalletManagerActivity.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutWalletTransations)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        TransactionRecordsActivity.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutWalletAddressBook)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        AddressBookActivity.actionStart(getActivity(), Constants.Action.ACTION_NONE);
                    }
                });
        RxView.clicks(layoutSettings)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        SettingsActiivty.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutAbout)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        AboutActivity.actionStart(getActivity());
                    }
                });

        RxView
                .clicks(layoutOfficialCommunity)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        OfficialCommunityActivity.actionStart(getActivity());
                    }
                });

        RxView
                .clicks(layoutSupportFeedback)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        boolean isChinese = Locale.CHINESE.getLanguage().equals(LanguageUtil.getLocale(App.getContext()).getLanguage());
//                        String supportFeedBackUrl = BuildConfig.URL_SUPPORT_FEEDBACK + (isChinese ? "zh-cn" : "en-001");
                        String supportFeedBackUrl = null;
                        if (isChinese) {
                            supportFeedBackUrl = Constants.WEBURL.WEB_URL_SUPPORT_FEEDBACK_ZH;
                        } else {
                            supportFeedBackUrl = Constants.WEBURL.WEB_URL_SUPPORT_FEEDBACK_EN;
                        }
                        CommonHybridActivity.actionStart(getActivity(), supportFeedBackUrl, WebType.WEB_TYPE_SUPPORT_FEEDBACK);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
