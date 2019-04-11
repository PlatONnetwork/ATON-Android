package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxAdapterView;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.SharedWalletMemberAdapter;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.CommonEditDialogFragment;
import com.juzix.wallet.component.ui.dialog.CommonTipsDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.presenter.ManageSharedWalletPresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.component.widget.ListViewForScrollView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

/**
 * @author matrixelement
 */
public class ManageSharedWalletActivity extends MVPBaseActivity<ManageSharedWalletPresenter> implements ManageSharedWalletContract.View {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar        commonTitleBar;
    @BindView(R.id.rl_rename)
    RelativeLayout        rlRename;
    @BindView(R.id.tv_rename)
    TextView              tvWalletName;
    @BindView(R.id.tv_delete)
    TextView              tvDelete;
    @BindView(R.id.list_member)
    ListViewForScrollView listMember;

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
        RxView.clicks(rlRename)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        showModifyWalletNameDialog();
                    }
                });
        RxView.clicks(tvDelete)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.deleteAction(TYPE_DELETE_WALLET);
                    }
                });

        mAdapter = new SharedWalletMemberAdapter(R.layout.item_manage_shared_wallet_members, null);

        listMember.setAdapter(mAdapter);

        RxAdapterView.itemClicks(listMember)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer position) throws Exception {
                if (position != 0) {
                    showModifyMemberNameDialog(position);
                }
            }
        });
    }

    @Override
    public void showWallet(SharedWalletEntity walletEntity) {
        tvWalletName.setText(walletEntity.getName());
        commonTitleBar.setTitle(walletEntity.getName());
    }

    @Override
    public void showMember(List<OwnerEntity> addressEntityList) {
        mAdapter.notifyDataChanged(addressEntityList);
    }

    @Override
    public void showErrorDialog(String title, String content, int type, IndividualWalletEntity walletEntity) {

        CommonTipsDialogFragment.createDialogWithTitleAndOneButton(ContextCompat.getDrawable(this, R.drawable.icon_dialog_tips), title, content, string(R.string.understood), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                showPasswordDialog(TYPE_DELETE_WALLET, -1, walletEntity);
            }
        }).show(getSupportFragmentManager(), "showPasswordError");
    }

    @Override
    public void showModifyWalletNameDialog() {

        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.changeWalletName), InputType.TYPE_CLASS_TEXT, string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                if (mPresenter.isExists(text)) {
                    showLongToast(string(R.string.wallet_name_exists));
                } else {
                    mPresenter.modifyWalletName(text);
                }
            }
        }, string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
            }
        }).show(getSupportFragmentManager(), "modifyMemberName");
    }

    @Override
    public void showModifyMemberNameDialog(int memberIndex) {
        CommonEditDialogFragment.createCommonEditDialogFragment(string(R.string.changeWalletName), InputType.TYPE_CLASS_TEXT, string(R.string.confirm), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                String text = extra.getString(Constants.Bundle.BUNDLE_TEXT);
                mPresenter.modifyMemberName(memberIndex, text);

            }
        }, string(R.string.cancel), new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
            }
        }).show(getSupportFragmentManager(), "modifyMemberName");
    }

    @Override
    public void showPasswordDialog(int type, int index, IndividualWalletEntity walletEntity) {
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                mPresenter.validPassword(type, credentials, index);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void updateWalletName(String walletName) {
        tvWalletName.setText(walletName);
        commonTitleBar.setTitle(walletName);
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
