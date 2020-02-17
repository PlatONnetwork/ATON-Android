package com.juzix.wallet.component.ui.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ReceiveTransationContract;
import com.juzix.wallet.component.ui.presenter.ReceiveTransactionPresenter;
import com.juzix.wallet.component.widget.CustomImageSpan;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class ReceiveTransactionFragment extends MVPBaseFragment<ReceiveTransactionPresenter> implements ReceiveTransationContract.View {

    @BindView(R.id.iv_wallet_address_qr_code)
    ImageView ivWalletAddressQrCode;
    @BindView(R.id.iv_avatar)
    ImageView shareWalletAvatar;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.sbtn_save)
    ShadowButton btnSave;
    @BindView(R.id.fl_share)
    View view;
    @BindView(R.id.tv_network_tips)
    TextView tvNetworkTips;

    private Unbinder unbinder;

    @Override
    protected ReceiveTransactionPresenter createPresenter() {
        return new ReceiveTransactionPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.loadData();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_receive_transaction, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    private void initViews() {

        tvNetworkTips.setText(buildNetworkTipsText());

        RxView.clicks(btnSave)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        mPresenter.shareView();
                    }
                });

        RxView.clicks(tvAddress)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mPresenter.copy();
                    }
                });
    }

    private SpannableStringBuilder buildNetworkTipsText() {
        String text = "  " + string(R.string.msg_network_tips, getNodeName(NodeManager.getInstance().getCurNode()));
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        spannableStringBuilder.setSpan(new CustomImageSpan(getContext(), R.drawable.icon_no_delegate_tips), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private String getNodeName(Node node) {
        if (BuildConfig.URL_MAIN_SERVER.equals(node.getNodeAddress())) {
            return string(R.string.newbaleyworld);
        } else if (BuildConfig.URL_TEST_MAIN_SERVER.equals(node.getNodeAddress())) {
            return string(R.string.uat_net);
        } else if (TextUtils.equals(BuildConfig.URL_TEST_SERVER, node.getNodeAddress()) || TextUtils.equals(BuildConfig.URL_TEST_OUTER_SERVER, node.getNodeAddress())) {
            return string(R.string.test_net);
        } else if (TextUtils.equals(BuildConfig.URL_DEVELOP_SERVER, node.getNodeAddress())) {
            return string(R.string.develop_net);
        }
        return "";
    }

    @Override
    public Wallet getWalletFromIntent() {
        return getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void setWalletInfo(Wallet entity) {
        int resId = RUtils.drawable(entity.getAvatar());
        if (resId < 0) {
            resId = R.drawable.avatar_15;
        }
        shareWalletAvatar.setImageResource(resId);
        tvAddress.setText(entity.getPrefixAddress());
    }

    @Override
    public void setWalletAddressQrCode(Bitmap bitmap) {
        ivWalletAddressQrCode.setImageBitmap(bitmap);
    }

    @Override
    public android.view.View shareView(String name, String address, Bitmap bitmap) {
        ((TextView) view.findViewById(R.id.tv_share_wallet_name)).setText(name);
        ((TextView) view.findViewById(R.id.tv_share_wallet_address)).setText(address);
        ((ImageView) view.findViewById(R.id.iv_share_qrcode)).setImageBitmap(bitmap);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        mPresenter.loadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
