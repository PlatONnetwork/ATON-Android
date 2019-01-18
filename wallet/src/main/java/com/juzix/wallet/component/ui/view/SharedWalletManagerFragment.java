package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.SharedWalletManagerContract;
import com.juzix.wallet.component.ui.presenter.SharedWalletManagerPresenter;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;

public class SharedWalletManagerFragment extends MVPBaseFragment<SharedWalletManagerPresenter> implements SharedWalletManagerContract.View, View.OnClickListener {

    private CommonAdapter<SharedWalletEntity> mAdapter;
    @Override
    protected SharedWalletManagerPresenter createPresenter() {
        return new SharedWalletManagerPresenter(this);
    }

    public static SharedWalletManagerFragment newInstance() {
        SharedWalletManagerFragment fragment = new SharedWalletManagerFragment();
        return fragment;
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.refresh();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View     view     = inflater.inflate(R.layout.fragment_shared_wallet, container, false);
        ListView listView = view.findViewById(R.id.list_shared_wallet);
        RoundedTextView rtvCreateWallet = view.findViewById(R.id.rtv_create_wallet);
        rtvCreateWallet.setText(R.string.create_shared_wallet);
        rtvCreateWallet.setOnClickListener(this);
        RoundedTextView rtvImportWallet = view.findViewById(R.id.rtv_import_wallet);
        rtvImportWallet.setText(R.string.add_shared_wallet);
        rtvImportWallet.setOnClickListener(this);
        TextView tvNoWallet = view.findViewById(R.id.tv_no_wallet);
        tvNoWallet.setText(R.string.msg_no_shared_wallet);

        mAdapter = new CommonAdapter<SharedWalletEntity>(R.layout.item_shared_wallet_list_manager, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, SharedWalletEntity item, int position) {
                viewHolder.setText(R.id.tv_name, item.getName());
                viewHolder.setText(R.id.tv_address, AddressFormatUtil.formatAddress(item.getPrefixContractAddress()));
                viewHolder.setImageResource(R.id.iv_wallet_avatar, RUtils.drawable(item.getAvatar()));
                viewHolder.setOnClickListener(R.id.rl_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ManageSharedWalletActivity.actionStart(getContext(), item);
                    }
                });
            }
        };
        listView.setAdapter(mAdapter);
        listView.setEmptyView(view.findViewById(R.id.layout_empty));
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rtv_create_wallet:
                mPresenter.createWallet();
                break;
            case R.id.rtv_import_wallet:
                mPresenter.addWallet();
                break;
        }
    }

    @Override
    public void showList(ArrayList<SharedWalletEntity> walletEntityList) {
        mAdapter.notifyDataChanged(walletEntityList);
    }
}
