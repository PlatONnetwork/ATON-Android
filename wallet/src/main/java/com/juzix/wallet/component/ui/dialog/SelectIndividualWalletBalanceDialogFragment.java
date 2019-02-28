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
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SelectIndividualWalletBalanceDialogFragment extends BaseDialogFragment {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.list_wallet)
    ListView       listWallet;

    private Unbinder                          unbinder;
    private SelectIndividualWalletListAdapter selectWalletListAdapter;
    private OnItemClickListener               mListener;

    public static SelectIndividualWalletBalanceDialogFragment newInstance(String uuid) {
        SelectIndividualWalletBalanceDialogFragment dialogFragment = new SelectIndividualWalletBalanceDialogFragment();
        Bundle                                      bundle         = new Bundle();
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
        return baseDialog;
    }

    @OnItemClick({R.id.list_wallet})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            selectWalletListAdapter.notifyDataSetChanged();
            mListener.onItemClick((IndividualWalletEntity) parent.getAdapter().getItem(position));
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
    public void onStart() {
        super.onStart();

        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        List<IndividualWalletEntity> walletEntityList    = IndividualWalletManager.getInstance().getWalletList();
//        List<IndividualWalletEntity> newWalletEntityList = new ArrayList<>();
//        for (IndividualWalletEntity walletEntity : walletEntityList){
//            if (walletEntity.getBalance() > 0){
//                newWalletEntityList.add(walletEntity);
//            }
//        }
        selectWalletListAdapter = new SelectIndividualWalletListAdapter(R.layout.item_select_wallet_list, walletEntityList, listWallet, SelectIndividualWalletDialogFragment.SELECT_TRANSACTION_WALLET);
        listWallet.setAdapter(selectWalletListAdapter);
        if (walletEntityList != null && !walletEntityList.isEmpty()){
            String uuid = getArguments().getString(Constants.Bundle.BUNDLE_UUID);
            listWallet.setItemChecked(walletEntityList.indexOf(new IndividualWalletEntity.Builder().uuid(uuid).build()), true);
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
