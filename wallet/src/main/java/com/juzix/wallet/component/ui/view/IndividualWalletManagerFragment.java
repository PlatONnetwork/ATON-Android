package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.juzix.wallet.component.ui.contract.IndividualWalletManagerContract;
import com.juzix.wallet.component.ui.presenter.IndividualWalletManagerPresenter;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.ArrayList;

public class IndividualWalletManagerFragment extends MVPBaseFragment<IndividualWalletManagerPresenter> implements IndividualWalletManagerContract.View, View.OnClickListener {

    private CommonAdapter<IndividualWalletEntity> mAdapter;
    @Override
    protected IndividualWalletManagerPresenter createPresenter() {
        return new IndividualWalletManagerPresenter(this);
    }

    public static IndividualWalletManagerFragment newInstance() {
        IndividualWalletManagerFragment fragment = new IndividualWalletManagerFragment();
        return fragment;
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.refresh();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View            view            = inflater.inflate(R.layout.fragment_individual_wallet, container, false);
        ListView        listView        = view.findViewById(R.id.list_individual_wallet);
        RoundedTextView rtvCreateWallet = view.findViewById(R.id.rtv_create_wallet);
        rtvCreateWallet.setText(R.string.createIndividualWallet);
        rtvCreateWallet.setOnClickListener(this);
        RoundedTextView rtvImportWallet = view.findViewById(R.id.rtv_import_wallet);
        rtvImportWallet.setText(R.string.importIndividualWallet);
        rtvImportWallet.setOnClickListener(this);
        TextView tvNoWallet = view.findViewById(R.id.tv_no_wallet);
        tvNoWallet.setText(R.string.msg_no_individual_wallet);
        mAdapter = new CommonAdapter<IndividualWalletEntity>(R.layout.item_individual_wallet_list_manager, null) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, IndividualWalletEntity item, int position) {
                viewHolder.setText(R.id.tv_name, item.getName());
                viewHolder.setText(R.id.tv_address, AddressFormatUtil.formatAddress(item.getPrefixAddress()));
                viewHolder.setImageResource(R.id.iv_wallet_avatar, RUtils.drawable(item.getAvatar()));
                viewHolder.setOnClickListener(R.id.rl_item, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ManageIndividualWalletActivity.actionStart(getContext(), item);
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
                CreateIndividualWalletActivity.actionStart(getActivity());
                break;
            case R.id.rtv_import_wallet:
                ImportIndividualWalletActivity.actionStart(getActivity());
                break;
            default:
                break;
        }
    }

    @Override
    public void showList(ArrayList<IndividualWalletEntity> walletEntityList) {
        mAdapter.notifyDataChanged(walletEntityList);
    }
}
