package com.juzix.wallet.component.ui.view;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.ReceiveTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.ReceiveTransationPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.CommonUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class ReceiveTransactionFragment extends MVPBaseFragment<ReceiveTransationPresenter> implements ReceiveTransationContract.View{

    @BindView(R.id.iv_wallet_address_qr_code)
    ImageView       ivWalletAddressQrCode;
    @BindView(R.id.iv_avatar)
    ImageView shareWalletAvatar;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.sbtn_save)
    ShadowButton    btnSave;
    @BindString(R.string.warning)
    String          warning;
    @BindString(R.string.understood)
    String          understood;
    @BindString(R.string.test_node_warn)
    String          testNodeWarn;
    private View  rootView;
    private Unbinder unbinder;

    @Override
    protected ReceiveTransationPresenter createPresenter() {
        return new ReceiveTransationPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.loadData();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_receive_transaction, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    private void initViews() {
        RxView.clicks(btnSave)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.shareView();
                    }
                });
    }

    @OnClick({R.id.tv_address})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_address:
                mPresenter.copy();
                break;
        }
    }

    @Override
    public WalletEntity getWalletFromIntent() {
        return getArguments().getParcelable(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void setWalletInfo(WalletEntity entity) {
        shareWalletAvatar.setImageResource(RUtils.drawable("icon_export_" + entity.getAvatar()));
        tvAddress.setText(entity.getPrefixAddress());
    }

    @Override
    public void setWalletAddressQrCode(Bitmap bitmap) {
        ivWalletAddressQrCode.setImageBitmap(bitmap);
    }

    @Override
    public android.view.View shareView(String name, String address, Bitmap bitmap) {
        View view = rootView.findViewById(R.id.fl_share);
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
