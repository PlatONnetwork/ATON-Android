package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageIndividualWalletContract;
import com.juzix.wallet.component.ui.dialog.BaseDialog;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CustomDialog;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.ManageIndividualWalletPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ManageIndividualWalletActivity extends MVPBaseActivity<ManageIndividualWalletPresenter> implements ManageIndividualWalletContract.View {

    private final static String TAG = ManageIndividualWalletActivity.class.getSimpleName();

    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.tv_left)
    TextView tvLeft;
    @BindView(R.id.ll_left)
    LinearLayout llLeft;
    @BindView(R.id.tv_middle)
    TextView tvMiddle;
    @BindView(R.id.tv_right)
    TextView tvRight;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.ll_right)
    LinearLayout llRight;
    @BindView(R.id.rl_title_bar)
    RelativeLayout rlTitleBar;
    @BindView(R.id.iv_icon)
    CircleImageView ivIcon;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.rl_description)
    RelativeLayout rlDescription;
    @BindView(R.id.ll_private_key)
    LinearLayout llPrivateKey;
    @BindView(R.id.v_line1)
    View vLine1;
    @BindView(R.id.ll_keystore)
    LinearLayout llKeystore;
    @BindView(R.id.ll_backup)
    LinearLayout llBackup;
    @BindView(R.id.btn_delete)
    Button btnDelete;

    private BaseDialog mNameDialog;
    private CustomDialog mFailedDialog;
    private BaseDialog mPasswordDialog;
    private Unbinder unbinder;

    @Override
    protected ManageIndividualWalletPresenter createPresenter() {
        return new ManageIndividualWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_individual_wallet);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.showIndividualWalletInfo();
    }

    private void initView() {
        tvMiddle.setText(R.string.manage);
    }

    @OnClick({R.id.ll_left, R.id.rl_description, R.id.ll_private_key, R.id.ll_keystore, R.id.btn_delete})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_left:
                finish();
                break;
            case R.id.rl_description:
                showModifyNameDialog();
                break;
            case R.id.ll_private_key:
                showPasswordDialog(TYPE_EXPORT_PRIVATE_KEY, "");
                break;
            case R.id.ll_keystore:
                showPasswordDialog(TYPE_EXPORT_KEYSTORE, "");
                break;
            case R.id.btn_delete:
                showPasswordDialog(TYPE_DELETE_WALLET, "");
                break;
        }
    }

    @Override
    public void showWalletName(String name) {
        tvName.setText(name);
    }

    @Override
    public void showWalletAddress(String address) {
        tvAddress.setText(AddressFormatUtil.formatAddress(address));
    }

    @Override
    public void showModifyNameDialog() {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.changeWalletName), InputType.TYPE_CLASS_TEXT, string(R.string.cancel), string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.modifyName(text);
            }
        }).show(getSupportFragmentManager(), "showModifyName");
    }


    @Override
    public void showErrorDialog(String title, String content, String preInputInfo,int type) {
        CommonDialogFragment.createCommonTitleWithOneButton(title, content, string(R.string.back), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                showPasswordDialog(type, preInputInfo);
            }
        }).show(getSupportFragmentManager(), "showPasswordError");
    }

    @Override
    public void showWalletAvatar(String avatar) {
        ivIcon.setImageResource(RUtils.drawable(avatar));
    }

    @Override
    public void showPasswordDialog(int type, String preInputInfo) {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.InputWalletPassword), preInputInfo, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, string(R.string.cancel), string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.validPassword(type, text);
            }
        }).show(getSupportFragmentManager(), "vertifyPassword");
    }

    @Override
    public IndividualWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, IndividualWalletEntity walletEntity) {
        Intent intent = new Intent(context, ManageIndividualWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
}
