package com.juzix.wallet.component.ui.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.juzhen.framework.network.NetState;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.IndividualWalletListAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.IndividualWalletContract;
import com.juzix.wallet.component.ui.presenter.IndividualWalletPresenter;
import com.juzix.wallet.component.widget.AutofitTextView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class IndividualWalletFragment extends MVPBaseFragment<IndividualWalletPresenter> implements IndividualWalletContract.View {

    @BindView(R.id.list_individual_wallet)
    ListView listIndividualWallet;
    @BindView(R.id.rtv_create_wallet)
    RoundedTextView rtvCreateWallet;
    @BindView(R.id.rtv_import_wallet)
    RoundedTextView rtvImportWallet;
    @BindView(R.id.layout_empty)
    ConstraintLayout layoutEmpty;
    @BindView(R.id.tv_no_wallet)
    TextView tvNoWallet;
    private AutofitTextView tvTotalBalance;

    private Unbinder                    unbinder;
    private IndividualWalletListAdapter walletListAdapter;
    private View                        headerView;

    public static IndividualWalletFragment newInstance() {
        IndividualWalletFragment fragment = new IndividualWalletFragment();
        return fragment;
    }

    @Override
    protected IndividualWalletPresenter createPresenter() {
        return new IndividualWalletPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.fetchIndividualWalletList();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_individual_wallet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        EventPublisher.getInstance().register(this);
        return rootView;
    }

    private void initViews() {
        tvNoWallet.setText(R.string.msg_no_individual_wallet);
        rtvCreateWallet.setText(R.string.createIndividualWallet);
        rtvImportWallet.setText(R.string.importIndividualWallet);
        walletListAdapter = new IndividualWalletListAdapter(R.layout.item_wallet_list, null);
        setHeaderView();
        listIndividualWallet.setAdapter(walletListAdapter);
        listIndividualWallet.setEmptyView(layoutEmpty);
    }

    private View getHeaderVew() {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list_header, null);
            tvTotalBalance = headerView.findViewById(R.id.tv_total_balance);
        }
        return headerView;
    }

    private void setHeaderView() {
        if (listIndividualWallet.getHeaderViewsCount() == 0) {
            try {
                listIndividualWallet.addHeaderView(getHeaderVew());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.rtv_create_wallet, R.id.rtv_import_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
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

    @OnItemClick({R.id.list_individual_wallet})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        IndividualWalletDetailActivity.actionStart(getContext(), walletListAdapter.getItem(position - 1));
    }

    @Override
    public void notifyWalletListChanged(List<IndividualWalletEntity> walletEntityList) {
        walletListAdapter.notifyDataChanged(walletEntityList);
    }

    @Override
    public void updateItem(IndividualWalletEntity walletEntity) {
        walletListAdapter.updateItem(getActivity(), listIndividualWallet, walletEntity);
    }

    @Override
    public void updateWalletBalance(double balance) {
        tvTotalBalance.setText(string(R.string.amount_with_unit,NumberParserUtils.getPrettyBalance(balance)));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        if (event.netState == NetState.CONNECTED) {
            mPresenter.fetchIndividualWalletList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateIndividualWalletTransactionEvent(Event.UpdateIndividualWalletTransactionEvent event) {
        mPresenter.fetchIndividualWalletList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateIndividualWalletBalanceEvent(Event.UpdateIndividualWalletBalanceEvent event) {
        mPresenter.fetchIndividualWalletList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }
}
