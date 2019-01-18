package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.SelectIndividualWalletListAdapter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;
import com.juzix.wallet.db.sqlite.IndividualWalletInfoDao;
import com.juzix.wallet.engine.IndividualWalletTransactionManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class SelectIndividualWalletDialogFragment extends BaseDialogFragment {

    public final static String CREATE_SHARED_WALLET = "create_shared_wallet";
    public final static String SELECT_SHARED_WALLET_OWNER = "create_shared_wallet_owner";
    public final static String SELECT_TRANSACTION_WALLET = "select_transaction_wallet";

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.list_wallet)
    ListView listWallet;

    private Unbinder unbinder;
    private SelectIndividualWalletListAdapter selectWalletListAdapter;
    private OnItemClickListener mListener;

    public static SelectIndividualWalletDialogFragment newInstance(String uuid) {
        SelectIndividualWalletDialogFragment dialogFragment = new SelectIndividualWalletDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_UUID, uuid);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_select_wallet, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        String uuid = getArguments().getString(Constants.Bundle.BUNDLE_UUID);

        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        selectWalletListAdapter = new SelectIndividualWalletListAdapter(R.layout.item_select_wallet_list, null, listWallet, getTag());
        listWallet.setAdapter(selectWalletListAdapter);

        Flowable
                .fromIterable(IndividualWalletInfoDao.getInstance().getWalletInfoList())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .map(new Function<IndividualWalletInfoEntity, IndividualWalletEntity>() {
                    @Override
                    public IndividualWalletEntity apply(IndividualWalletInfoEntity walletInfoEntity) throws Exception {
                        return walletInfoEntity.buildWalletEntity();
                    }
                })
                .map(new Function<IndividualWalletEntity, IndividualWalletEntity>() {

                    @Override
                    public IndividualWalletEntity apply(IndividualWalletEntity walletEntity) throws Exception {
                        return IndividualWalletTransactionManager.getInstance().getBalanceByAddress(walletEntity);
                    }
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<IndividualWalletEntity>, Throwable>() {
                    @Override
                    public void accept(List<IndividualWalletEntity> objects, Throwable throwable) throws Exception {
                        if (objects != null && !objects.isEmpty()) {
                            selectWalletListAdapter.notifyDataChanged(objects);
                            listWallet.setItemChecked(objects.indexOf(new IndividualWalletEntity.Builder().uuid(uuid).build()), true);
                        }
                    }
                });
    }

    @OnItemClick({R.id.list_wallet})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            IndividualWalletEntity walletEntity = (IndividualWalletEntity) parent.getAdapter().getItem(position);
            selectWalletListAdapter.notifyDataSetChanged();
            mListener.onItemClick(walletEntity);
            dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            mListener = (OnItemClickListener) context;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnItemClickListener {

        void onItemClick(IndividualWalletEntity walletEntity);
    }
}
