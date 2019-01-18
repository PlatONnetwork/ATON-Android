package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.utils.ShareUtil;
import com.juzix.wallet.utils.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class MeFragment extends BaseFragment {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;

    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_me, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        commonTitleBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToastUtil.showLongToast(getContext(), BuildConfig.VERSION_NAME);
                return true;
            }
        });
        return rootView;
    }

    @OnClick({R.id.layout_wallet_manage, R.id.layout_wallet_transations, R.id.layout_wallet_address_book, R.id.layout_settings
            , R.id.layout_help, R.id.layout_about, R.id.iv_telegram, R.id.iv_wechat, R.id.iv_github,
            R.id.iv_twitter, R.id.iv_facebook, R.id.iv_rabbit, R.id.iv_medium})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_wallet_manage:
                WalletManagerActivity.actionStart(getActivity());
                break;
            case R.id.layout_wallet_transations:
                TransactionRecordsActivity.actionStart(getActivity());
                break;
            case R.id.layout_wallet_address_book:
                AddressBookActivity.actionStart(getActivity(), Constants.Action.ACTION_NONE);
                break;
            case R.id.layout_settings:
                SettingsActiivty.actionStart(getActivity());
                break;
            case R.id.layout_help:
                break;
            case R.id.layout_about:
                AboutActivity.actionStart(getActivity());
                break;
            case R.id.iv_telegram:
                ShareUtil.shareUrl(getActivity(), "https://t.me/PlatONHK");
                break;
            case R.id.iv_wechat:
                break;
            case R.id.iv_github:
                ShareUtil.shareUrl(getActivity(), "https://github.com/PlatONnetwork");
                break;
            case R.id.iv_twitter:
                ShareUtil.shareUrl(getActivity(), "https://twitter.com/PlatON_Network");
                break;
            case R.id.iv_facebook:
                ShareUtil.shareUrl(getActivity(), "https://www.facebook.com/PlatONNetwork/");
                break;
            case R.id.iv_rabbit:
                ShareUtil.shareUrl(getActivity(), "https://www.reddit.com/user/PlatON_Network");
                break;
            case R.id.iv_medium:
                ShareUtil.shareUrl(getActivity(), "https://medium.com/@PlatON_Network");
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
