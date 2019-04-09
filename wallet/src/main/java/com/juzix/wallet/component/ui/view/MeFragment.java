package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.utils.ShareUtil;
import com.juzix.wallet.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.tv_title)
    TextView     tvTitle;
    @BindView(R.id.layout_wallet_manage)
    LinearLayout layoutWalletManage;
    @BindView(R.id.layout_wallet_transations)
    LinearLayout layoutWalletTransations;
    @BindView(R.id.layout_wallet_address_book)
    LinearLayout layoutWalletAddressBook;
    @BindView(R.id.layout_settings)
    LinearLayout layoutSettings;
    @BindView(R.id.layout_help)
    LinearLayout layoutHelp;
    @BindView(R.id.layout_about)
    LinearLayout layoutAbout;
    @BindView(R.id.iv_telegram)
    ImageView    ivTelegram;
    @BindView(R.id.iv_wechat)
    ImageView    ivWechat;
    @BindView(R.id.iv_github)
    ImageView    ivGithub;
    @BindView(R.id.iv_twitter)
    ImageView    ivTwitter;
    @BindView(R.id.iv_facebook)
    ImageView    ivFacebook;
    @BindView(R.id.iv_rabbit)
    ImageView    ivRabbit;
    @BindView(R.id.iv_medium)
    ImageView    ivMedium;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToastUtil.showLongToast(getContext(), BuildConfig.VERSION_NAME);
                return true;
            }
        });
        init();
        return rootView;
    }

    private void init(){
        RxView.clicks(layoutWalletManage)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        WalletManagerActivity.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutWalletTransations)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        TransactionRecordsActivity.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutWalletAddressBook)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        AddressBookActivity.actionStart(getActivity(), Constants.Action.ACTION_NONE);
                    }
                });
        RxView.clicks(layoutSettings)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        SettingsActiivty.actionStart(getActivity());
                    }
                });
        RxView.clicks(layoutAbout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        AboutActivity.actionStart(getActivity());
                    }
                });
        RxView.clicks(ivTelegram)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://t.me/PlatONHK");
                    }
                });
        RxView.clicks(ivGithub)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://github.com/PlatONnetwork");
                    }
                });
        RxView.clicks(ivTwitter)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://twitter.com/PlatON_Network");
                    }
                });
        RxView.clicks(ivFacebook)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://www.facebook.com/PlatONNetwork/");
                    }
                });
        RxView.clicks(ivRabbit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://www.reddit.com/user/PlatON_Network");
                    }
                });
        RxView.clicks(ivMedium)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        ShareUtil.shareUrl(getActivity(), "https://medium.com/@PlatON_Network");
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
