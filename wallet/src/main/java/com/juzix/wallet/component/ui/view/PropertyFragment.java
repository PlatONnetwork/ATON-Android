package com.juzix.wallet.component.ui.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BaseFragment;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.QRCodeParser;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class PropertyFragment extends BaseFragment {

    public final static int TAB_INDIVIDUAL = 0;
    public final static int TAB_SHARED = 1;

    @BindView(R.id.rb_individual_wallet)
    RadioButton rbIndividualWallet;
    @BindView(R.id.rb_shared_wallet)
    RadioButton rbSharedWallet;
    @BindView(R.id.v_new_msg)
    View viewNewMsg;

    Unbinder unbinder;

    enum Page {
        INDIVIDUAL_WALLET, SHARED_WALLET
    }
    private static final int REQ_QR_CODE = 101;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_property, container,false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        updateMsgTips(SharedWalletTransactionManager.getInstance().unRead());
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == PropertyFragment.REQ_QR_CODE) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(ScanQRCodeActivity.EXTRA_SCAN_QRCODE_DATA);
            QRCodeParser.parseMainQRCode(currentActivity(), scanResult);
        }
    }

    private void initViews() {
        showPage(Page.INDIVIDUAL_WALLET);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateMessageTipsEvent(Event.UpdateMessageTipsEvent event) {
        updateMsgTips(event.unRead);
    }

    private void updateMsgTips(boolean unRead){
        viewNewMsg.setVisibility(unRead ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.iv_scan, R.id.iv_add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                scanQRCode();
                break;
            case R.id.iv_add:
                AddWalletAcitivity.actionStart(getContext());
                break;
            default:
                break;
        }
    }

    @OnCheckedChanged({R.id.rb_individual_wallet, R.id.rb_shared_wallet})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (!isChecked) {
            return;
        }

        switch (buttonView.getId()) {
            case R.id.rb_individual_wallet:
                rbIndividualWallet.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_ffffff));
                rbIndividualWallet.setTextSize(16);
                rbSharedWallet.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cdcdcd));
                rbSharedWallet.setTextSize(12);
                showPage(Page.INDIVIDUAL_WALLET);
                break;
            case R.id.rb_shared_wallet:
                rbIndividualWallet.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_cdcdcd));
                rbIndividualWallet.setTextSize(12);
                rbSharedWallet.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_ffffff));
                rbSharedWallet.setTextSize(16);
                showPage(Page.SHARED_WALLET);
                break;
            default:
                break;
        }
    }

    public void showPageWithUnActive(Page page) {

        if (page == Page.SHARED_WALLET) {
            rbSharedWallet.setChecked(true);
        } else {
            rbIndividualWallet.setChecked(true);
        }
    }

    private void showPage(Page page) {

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag(page.toString());

        if (fragment != null) {
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment);
            }
        }

        for (Page p : Page.values()) {
            Fragment f = fragmentManager.findFragmentByTag(p.toString());
            if (p != page && f != null) {
                if (f.isAdded()) {
                    fragmentTransaction.hide(f);
                }
            }
        }

        if (fragment == null) {
            switch (page) {
                case INDIVIDUAL_WALLET:
                    fragmentTransaction.add(R.id.layout_content, IndividualWalletFragment.newInstance(), page.toString());
                    break;
                case SHARED_WALLET:
                    fragmentTransaction.add(R.id.layout_content, SharedWalletFragment.newInstance(), page.toString());
                    break;
                default:
                    break;
            }
        }

        fragmentTransaction.commitNowAllowingStateLoss();

    }

    private void scanQRCode() {
        final BaseActivity activity = currentActivity();
        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                Intent intent = new Intent(currentActivity(), ScanQRCodeActivity.class);
                startActivityForResult(intent, REQ_QR_CODE);
            }

            @Override
            public void onHasPermission(int what) {
                Intent intent = new Intent(currentActivity(), ScanQRCodeActivity.class);
                startActivityForResult(intent, REQ_QR_CODE);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().register(this);
    }
}
