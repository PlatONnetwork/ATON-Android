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
import com.juzix.wallet.component.adapter.SharedWalletListAdapter;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.SharedWalletContract;
import com.juzix.wallet.component.ui.presenter.SharedWalletPresenter;
import com.juzix.wallet.component.widget.AutofitTextView;
import com.juzix.wallet.component.widget.RoundedTextView;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.SharedWalletEntity;
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
public class SharedWalletFragment extends MVPBaseFragment<SharedWalletPresenter> implements SharedWalletContract.View {

    private static final String TAG = SharedWalletFragment.class.getSimpleName();

    @BindView(R.id.list_shared_wallet)
    ListView listSharedWallet;
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
    private SharedWalletListAdapter walletListAdapter;
    private View                        headerView;

    public static SharedWalletFragment newInstance() {
        SharedWalletFragment fragment = new SharedWalletFragment();
        return fragment;
    }

    @Override
    protected SharedWalletPresenter createPresenter() {
        return new SharedWalletPresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.fetchSharedWalletList();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shared_wallet, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        initViews();
        EventPublisher.getInstance().register(this);
        return rootView;
    }

    private void initViews() {
        tvNoWallet.setText(R.string.msg_no_shared_wallet);
        rtvCreateWallet.setText(R.string.create_shared_wallet);
        rtvImportWallet.setText(R.string.add_shared_wallet);
        walletListAdapter = new SharedWalletListAdapter(R.layout.item_wallet_list, null);
        setHeaderView();
        listSharedWallet.setAdapter(walletListAdapter);
        listSharedWallet.setEmptyView(layoutEmpty);
    }

    private View getHeaderVew() {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.item_wallet_list_header, null);
            tvTotalBalance = headerView.findViewById(R.id.tv_total_balance);
        }
        return headerView;
    }

    private void setHeaderView() {
        if (listSharedWallet.getHeaderViewsCount() == 0) {
            try {
                listSharedWallet.addHeaderView(getHeaderVew());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @OnItemClick({R.id.list_shared_wallet})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }

        SharedWalletEntity sharedWalletEntity = walletListAdapter.getItem(position - 1);

        if (sharedWalletEntity.isFinished()) {
            SharedWalletDetailActivity.actionStart(getContext(), sharedWalletEntity);
        }
    }

    @OnClick({R.id.rtv_create_wallet, R.id.rtv_import_wallet})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtv_create_wallet:
                mPresenter.createWallet();
                break;
            case R.id.rtv_import_wallet:
                mPresenter.addWallet();
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetWorkStateChangedEvent(Event.NetWorkStateChangedEvent event) {
        if (event.netState == NetState.CONNECTED) {
            mPresenter.fetchSharedWalletList();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateSharedWalletTransactionEvent(Event.UpdateSharedWalletTransactionEvent event) {
        mPresenter.fetchSharedWalletList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateCreateJointWalletProgressEvent(Event.UpdateCreateJointWalletProgressEvent event) {
        SharedWalletEntity sharedWalletEntity = event.sharedWalletEntity;
        if (sharedWalletEntity != null) {
            walletListAdapter.updateItem(getActivity(), listSharedWallet, sharedWalletEntity);
            if (sharedWalletEntity.getProgress() == 100) {
                listSharedWallet.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedWalletManager.getInstance().updateWalletFinished(sharedWalletEntity.getUuid(), true);
                        sharedWalletEntity.updateFinished(true);
                        walletListAdapter.updateItem(getActivity(), listSharedWallet, sharedWalletEntity);
                    }
                }, 500);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }

    @Override
    public void notifyWalletListChanged(List<SharedWalletEntity> walletEntityList) {
        walletListAdapter.notifyDataChanged(walletEntityList);
    }

    @Override
    public void updateItem(SharedWalletEntity walletEntity) {
        walletListAdapter.updateItem(getActivity(), listSharedWallet, walletEntity);
    }

    @Override
    public void updateWalletBalance(double balance) {
        tvTotalBalance.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyBalance(balance)));
    }


}
