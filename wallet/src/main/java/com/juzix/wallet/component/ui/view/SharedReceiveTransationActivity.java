package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.SharedReceiveTransationContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.dialog.ShareDialogFragment;
import com.juzix.wallet.component.ui.presenter.SharedReceiveTransationPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.entity.ShareAppInfo;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;
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
public class SharedReceiveTransationActivity extends MVPBaseActivity<SharedReceiveTransationPresenter> implements SharedReceiveTransationContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.tv_wallet_name_title)
    TextView tvWalletNameTitle;
    @BindView(R.id.iv_wallet_address_qr_code)
    ImageView ivWalletAddressQrCode;
    @BindView(R.id.iv_copy_public_key)
    ImageView ivCopyPublicKey;
    @BindView(R.id.iv_copy_wallet_address)
    ImageView ivCopyWalletAddress;
    @BindView(R.id.tv_public_key_title)
    TextView tvPublicKeyTitle;
    @BindView(R.id.tv_public_key)
    TextView tvPublicKey;
    @BindView(R.id.layout_public_key)
    RelativeLayout layoutPublicKey;
    @BindView(R.id.layout_transation_detail)
    ConstraintLayout layoutTransactionDetail;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.iv_avatar)
    CircleImageView shareWalletAvatar;
    @BindView(R.id.rtv_save)
    RoundedTextView rtvSave;
    @BindString(R.string.warning)
    String warning;
    @BindString(R.string.understood)
    String understood;
    @BindString(R.string.test_node_warn)
    String testNodeWarn;

    private Unbinder unbinder;

    @Override
    protected SharedReceiveTransationPresenter createPresenter() {
        return new SharedReceiveTransationPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_transation);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.loadData();
    }

    private void initView() {

        ImageView ivRight = commonTitleBar.findViewById(R.id.iv_right);

        RxView.clicks(ivRight)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        List<ShareAppInfo> shareAppInfoList = AppUtil.getShareAppInfoList(getContext());
                        if (!shareAppInfoList.isEmpty()) {
                            ShareDialogFragment.newInstance((ArrayList<ShareAppInfo>) shareAppInfoList).show(getSupportFragmentManager(), "share");
                        }
                    }
                });

        RxView.clicks(rtvSave)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.shareView();
                    }
                });

        tvPublicKeyTitle.setVisibility(View.GONE);
        layoutPublicKey.setVisibility(View.GONE);

        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layoutTransactionDetail.getLayoutParams();
        layoutParams.height = DensityUtil.dp2px(this, 115f);
        layoutTransactionDetail.setLayoutParams(layoutParams);

    }

    @OnClick({R.id.iv_copy_wallet_address})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy_wallet_address:
                CommonUtil.copyTextToClipboard(this, tvWalletAddress.getText().toString());
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

    public static void actionStart(Context context, SharedWalletEntity walletEntity) {
        Intent intent = new Intent(context, SharedReceiveTransationActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }

    @Override
    public SharedWalletEntity getWalletFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    public void setWalletInfo(SharedWalletEntity walletInfo) {
//        tvPublicKey.setText(walletInfo.getPrefixAddress());
//        tvWalletAddress.setText(walletInfo.getPrefixAddress());
        tvWalletNameTitle.setText(walletInfo.getName());
        tvWalletAddress.setText(walletInfo.getPrefixAddress());
        shareWalletAvatar.setImageResource(RUtils.drawable(walletInfo.getAvatar()));
    }

    @Override
    public void showWarnDialogFragment() {
        CommonDialogFragment.createWarnTitleWithOneButton(warning, testNodeWarn, understood, new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                if (fragment != null) {
                    fragment.dismiss();
                }
            }
        }).show(getSupportFragmentManager(), "showTestNodeWarn");
    }

    @Override
    public void setWalletAddressQrCode(Bitmap bitmap) {
        ivWalletAddressQrCode.setImageBitmap(bitmap);
    }

    @Override
    public View shareView(String name, String address, Bitmap bitmap) {
        View view = findViewById(R.id.fl_share);
        ((TextView) view.findViewById(R.id.tv_share_wallet_name)).setText(name);
        ((TextView) view.findViewById(R.id.tv_share_wallet_address)).setText(address);
        ((ImageView) view.findViewById(R.id.iv_share_qrcode)).setImageBitmap(bitmap);
        return view;
    }
}
