package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.adapter.SelectSharedWalletListAdapter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SelectSharedWalletDialogFragment extends BaseDialogFragment {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.list_wallet)
    ListView listWallet;

    private Unbinder                          unbinder;
    private SelectSharedWalletListAdapter selectWalletListAdapter;
    private OnItemClickListener               mListener;

    public static SelectSharedWalletDialogFragment newInstance(String uuid) {
        SelectSharedWalletDialogFragment dialogFragment = new SelectSharedWalletDialogFragment();
        Bundle                           bundle         = new Bundle();
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
            mListener.onItemClick((SharedWalletEntity) parent.getAdapter().getItem(position));
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

        selectWalletListAdapter = new SelectSharedWalletListAdapter(R.layout.item_select_wallet_list, null, listWallet);
        listWallet.setAdapter(selectWalletListAdapter);

//        Flowable.fromIterable(SharedWalletInfoDao.getInstance().getWalletInfoList())
//                .compose(bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .map(new Function<SharedWalletInfoEntity, SharedWalletEntity>() {
//                    @Override
//                    public SharedWalletEntity apply(SharedWalletInfoEntity walletInfoEntity) throws Exception {
//                        return walletInfoEntity.buildWalletEntity();
//                    }
//                }).map(new Function<SharedWalletEntity, SharedWalletEntity>() {
//
//            @Override
//            public SharedWalletEntity apply(SharedWalletEntity walletEntity) throws Exception {
//                return SharedWalletTransactionManager.getInstance().getBalanceByAddress(walletEntity);
//            }
//        }).toList().observeOn(AndroidSchedulers.mainThread()).subscribe(new BiConsumer<List<SharedWalletEntity>, Throwable>() {
//            @Override
//            public void accept(List<SharedWalletEntity> objects, Throwable throwable) throws Exception {
//                if (objects != null && !objects.isEmpty()) {
//                    selectWalletListAdapter.notifyDataChanged(objects);
//                    listWallet.setItemChecked(objects.indexOf(new SharedWalletEntity.Builder().uuid(uuid).build()), true);
//                }
//            }
//        });

        ArrayList<SharedWalletEntity> walletEntityList = SharedWalletManager.getInstance().getWalletList();
        new Thread(){
            @Override
            public void run() {
                for (SharedWalletEntity walletEntity : walletEntityList) {
                    double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixContractAddress());
                    walletEntity.setBalance(balance);
                }
                Message msg = mHandler.obtainMessage();
                msg.what = MSG_UPDATE_DATA;
                msg.obj = walletEntityList;
                mHandler.sendMessage(msg);
            }
        }.start();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private static final int MSG_UPDATE_DATA = 1;
    private Handler mHandler  = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_UPDATE_DATA:
                String uuid = getArguments().getString(Constants.Bundle.BUNDLE_UUID);
                ArrayList<SharedWalletEntity> objects = (ArrayList<SharedWalletEntity>) msg.obj;
                if (objects != null && !objects.isEmpty()) {
                    selectWalletListAdapter.notifyDataChanged(objects);
                    listWallet.setItemChecked(objects.indexOf(new SharedWalletEntity.Builder().uuid(uuid).build()), true);
                }
                break;
            }
        }
    };

    public interface OnItemClickListener {

        void onItemClick(SharedWalletEntity walletEntity);
    }
}
