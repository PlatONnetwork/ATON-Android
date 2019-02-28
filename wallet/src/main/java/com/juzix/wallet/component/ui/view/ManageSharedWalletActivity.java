package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.SharedWalletMemberAdapter;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.ManageSharedWalletPresenter;
import com.juzix.wallet.component.widget.CircleImageView;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ListViewForScrollView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class ManageSharedWalletActivity extends MVPBaseActivity<ManageSharedWalletPresenter> implements ManageSharedWalletContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.iv_wallet_pic)
    CircleImageView ivWalletPic;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.list_member)
    ListViewForScrollView listMember;
    @BindView(R.id.tv_member_name)
    TextView tvMemberName;
    @BindView(R.id.tv_member_address)
    TextView tvMemberAddress;
    @BindView(R.id.rtv_delete_wallet)
    RoundedTextView rtvDeleteWallet;

    private CommonAdapter<OwnerEntity> mAdapter;
    private Unbinder unbinder;

    @Override
    protected ManageSharedWalletPresenter createPresenter() {
        return new ManageSharedWalletPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_shared_wallet);
        unbinder = ButterKnife.bind(this);
        initView();
        mPresenter.showWalletInfo();
    }

    private void initView() {

        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });

        mAdapter = new SharedWalletMemberAdapter(R.layout.item_manage_shared_wallet_members, null);

        listMember.setAdapter(mAdapter);

        RxAdapterView.itemClicks(listMember).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer position) throws Exception {
                showModifyMemberNameDialog(position);
            }
        });
    }

    @OnClick({R.id.layout_modify_member_name, R.id.rtv_delete_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_modify_member_name:
                showModifyWalletNameDialog();
                break;
            case R.id.rtv_delete_wallet:
                mPresenter.deleteAction(TYPE_DELETE_WALLET);
                break;
            default:
                break;
        }
    }

    @Override
    public void showWallet(SharedWalletEntity walletEntity) {
        ivWalletPic.setImageResource(RUtils.drawable(walletEntity.getAvatar()));
        tvWalletName.setText(walletEntity.getName());
        tvWalletAddress.setText(AddressFormatUtil.formatAddress(walletEntity.getPrefixContractAddress()));
    }

    @Override
    public void showMember(ArrayList<OwnerEntity> addressEntityList) {
        mAdapter.notifyDataChanged(addressEntityList);
    }

    @Override
    public void showOwner(String individualWalletName, String individualWalletAddress) {
        tvMemberName.setText(individualWalletName);
        tvMemberAddress.setText(AddressFormatUtil.formatAddress(individualWalletAddress));
    }

    @Override
    public void showErrorDialog(String title, String content, String preInputInfo) {

        CommonDialogFragment.createCommonTitleWithOneButton(title, content, string(R.string.back), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                showPasswordDialog(TYPE_DELETE_WALLET, -1, preInputInfo);
            }
        }).show(getSupportFragmentManager(), "showPasswordError");
    }

    @Override
    public void showModifyWalletNameDialog() {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.changeWalletName), InputType.TYPE_CLASS_TEXT, string(R.string.cancel), string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.modifyWalletName(text);
            }
        }).show(getSupportFragmentManager(), "modifyMemberName");
    }

    @Override
    public void showModifyMemberNameDialog(int memberIndex) {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.changeWalletName), InputType.TYPE_CLASS_TEXT, string(R.string.cancel), string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.modifyMemberName(memberIndex, text);
            }
        }).show(getSupportFragmentManager(), "modifyMemberName");
    }

    @Override
    public void showPasswordDialog(int type, int index, String preInputInfo) {

        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.InputWalletPassword), preInputInfo, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD, string(R.string.cancel), string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.validPassword(type, text, index);
            }
        }).show(getSupportFragmentManager(), "vertifyPassword");
    }

    @Override
    public void updateWalletName(String walletName) {
        tvWalletName.setText(walletName);
    }

    @Override
    public void updateWalletMemberName(String newMemberName, int position) {
        OwnerEntity ownerEntity = mAdapter.getList().get(position);
        ownerEntity.setName(newMemberName);
        mAdapter.updateItem(this, listMember, ownerEntity);
    }

    @Override
    public SharedWalletEntity getWalletEntityFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_WALLET);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, SharedWalletEntity walletEntity) {
        Intent intent = new Intent(context, ManageSharedWalletActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_WALLET, walletEntity);
        context.startActivity(intent);
    }
}
