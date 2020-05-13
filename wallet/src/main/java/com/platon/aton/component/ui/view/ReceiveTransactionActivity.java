package com.platon.aton.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.platon.aton.BuildConfig;
import com.platon.aton.R;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.ReceiveTransationContract;
import com.platon.aton.component.ui.presenter.ReceiveTransactionPresenter;
import com.platon.aton.component.widget.CustomImageSpan;
import com.platon.aton.component.widget.ShadowButton;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.utils.DensityUtil;
import com.platon.framework.utils.LanguageUtil;
import com.platon.framework.utils.RUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReceiveTransactionActivity extends BaseActivity<ReceiveTransationContract.View,ReceiveTransactionPresenter> implements ReceiveTransationContract.View{

    Unbinder unbinder;
    @BindView(R.id.iv_wallet_address_qr_code)
    ImageView ivWalletAddressQrCode;
    @BindView(R.id.iv_avatar)
    ImageView shareWalletAvatar;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_wallet_address_copy)
    ImageView copyWalletAddress;
    @BindView(R.id.sbtn_save)
    ShadowButton btnSave;
    @BindView(R.id.fl_share)
    View view;
    @BindView(R.id.tv_network_tips)
    TextView tvNetworkTips;


    @Override
    public ReceiveTransactionPresenter createPresenter() {
        return new ReceiveTransactionPresenter();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_receive_transaction;
    }

    @Override
    public ReceiveTransationContract.View createView() {
        return this;
    }

    @Override
    public void init() {
        unbinder = ButterKnife.bind(this);
        EventPublisher.getInstance().register(this);
        getPresenter().loadData();
        initViews();
    }

    @Override
    protected boolean immersiveBarViewEnabled() {
        return true;
    }

    private void initViews() {
        tvNetworkTips.setText(buildNetworkTipsText());

        RxView.clicks(btnSave)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        getPresenter().shareView();
                    }
                });

        RxView.clicks(tvAddress)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        getPresenter().copy();
                    }
                });

        RxView.clicks(copyWalletAddress)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        getPresenter().copy();
                    }
                });
    }

    private SpannableStringBuilder buildNetworkTipsText() {
        String nodeNameStr = getNodeName(NodeManager.getInstance().getCurNode());
        String text = "  " + string(R.string.msg_network_tips, nodeNameStr);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        int startIndex = 4;
        int endIndex = startIndex + nodeNameStr.length();
        int startIndexEN = 17;
        int endIndexEN = startIndexEN + nodeNameStr.length();
        Locale locale = LanguageUtil.getLocale();
        if(Locale.CHINESE.getLanguage().equals(locale.getLanguage())){
            spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(this,4.6f),true), startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }else{
            spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), startIndexEN, endIndexEN, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            spannableStringBuilder.setSpan(new AbsoluteSizeSpan(DensityUtil.sp2px(this,4.8f),true), startIndexEN, endIndexEN, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
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
        return null;
    }

    @Override
    public void setWalletInfo(Wallet walletEntity) {
        int resId = RUtils.drawable(walletEntity.getAvatar());
        if (resId < 0) {
            resId = R.drawable.avatar_15;
        }
        shareWalletAvatar.setImageResource(resId);
        tvAddress.setText(walletEntity.getPrefixAddress());
    }

    @Override
    public void setWalletAddressQrCode(Bitmap bitmap) {
        ivWalletAddressQrCode.setImageBitmap(bitmap);
    }

    @Override
    public View shareView(String name, String address, Bitmap bitmap) {
        ((TextView) view.findViewById(R.id.tv_share_wallet_name)).setText(name);
        ((TextView) view.findViewById(R.id.tv_share_wallet_address)).setText(address);
        ((ImageView) view.findViewById(R.id.iv_share_qrcode)).setImageBitmap(bitmap);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSelectedWalletEvent(Event.UpdateSelectedWalletEvent event) {
        getPresenter().loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, ReceiveTransactionActivity.class));
    }


}
