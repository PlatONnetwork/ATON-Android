package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.PhotoUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class AddWalletAcitivity extends BaseActivity {


    @BindView(R.id.tv_create_individual_wallet)
    TextView         tvCreateIndividualWallet;
    @BindView(R.id.tv_create_shared_wallet)
    TextView         tvCreateSharedWallet;
    @BindView(R.id.tv_import_individual_wallet)
    TextView         tvImportIndividualWallet;
    @BindView(R.id.tv_add_shared_wallet)
    TextView         tvAddSharedWallet;
    @BindView(R.id.iv_close)
    ImageView        ivClose;
    @BindView(R.id.layout_body)
    ConstraintLayout layoutBody;

    Unbinder unbinder;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, AddWalletAcitivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        unbinder = ButterKnife.bind(this);
        initView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDrawable bitmapDrawable = new BitmapDrawable(PhotoUtil.blurBitmap(AddWalletAcitivity.this, PhotoUtil.screenShot(MainActivity.sInstance, false)));
                layoutBody.setBackgroundDrawable(bitmapDrawable);
            }
        });
    }

    protected View statusBarView() {
        return buildEmptyView();
    }

    private View buildEmptyView() {
        View                      view         = new View(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    protected boolean immersiveBarInitEnabled() {
        return false;
    }

    private void initView() {
        layoutBody.setBackgroundColor(ContextCompat.getColor(this, R.color.color_1b2137));
    }

    @OnClick({R.id.tv_create_individual_wallet, R.id.tv_create_shared_wallet, R.id.tv_import_individual_wallet, R.id.tv_add_shared_wallet,
            R.id.iv_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create_individual_wallet:
                CreateIndividualWalletActivity.actionStart(getContext());
                finish();
                break;
            case R.id.tv_create_shared_wallet:
                createWallet();
                break;
            case R.id.tv_import_individual_wallet:
                ImportIndividualWalletActivity.actionStart(getContext());
                finish();
                break;
            case R.id.tv_add_shared_wallet:
                addWallet();
                break;
            case R.id.iv_close:
                layoutBody.setBackgroundColor(ContextCompat.getColor(this, R.color.color_00000000));
                finish();
                break;
            default:
                break;

        }
    }

    private void createWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0){
            showLongToast(R.string.insufficientBalanceTips);
            return;
        }
        CreateSharedWalletActivity.actionStart(this);
        this.finish();
    }

    private void addWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        AddSharedWalletActivity.actionStart(this);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        layoutBody.setBackgroundColor(ContextCompat.getColor(this, R.color.color_00000000));
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
