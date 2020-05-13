package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.platon.aton.R;
import com.platon.aton.component.adapter.SelectWalletListAdapter;
import com.platon.aton.component.widget.ShadowDrawable;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.TransactionManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.DensityUtil;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.app.Constants;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

/**
 * @author matrixelement
 */
public class SelectWalletDialogFragment extends BaseDialogFragment {

    public final static String CREATE_SHARED_WALLET = "create_shared_wallet";
    public final static String SELECT_SHARED_WALLET_OWNER = "create_shared_wallet_owner";
    public final static String SELECT_TRANSACTION_WALLET = "select_transaction_wallet";
    public final static String SELECT_UNLOCK_WALLET = "select_unlock_wallet";

    @BindView(R.id.list_wallet)
    ListView listWallet;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Unbinder unbinder;
    private SelectWalletListAdapter selectWalletListAdapter;
    private OnItemClickListener mListener;

    public static SelectWalletDialogFragment newInstance(String uuid) {
        SelectWalletDialogFragment dialogFragment = new SelectWalletDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_UUID, uuid);
        bundle.putBoolean(Constants.Bundle.BUNDLE_FEE_AMOUNT, false);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public static SelectWalletDialogFragment newInstance(String uuid, boolean needAmount) {
        SelectWalletDialogFragment dialogFragment = new SelectWalletDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_UUID, uuid);
        bundle.putBoolean(Constants.Bundle.BUNDLE_FEE_AMOUNT, needAmount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public SelectWalletDialogFragment setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_select_wallet, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        ShadowDrawable.setShadowDrawable(layoutContent,
                ContextCompat.getColor(context, R.color.color_ffffff),
                DensityUtil.dp2px(context, 6f),
                ContextCompat.getColor(getContext(), R.color.color_33616161)
                , DensityUtil.dp2px(context, 10f),
                0,
                DensityUtil.dp2px(context, 2f));

        selectWalletListAdapter = new SelectWalletListAdapter(R.layout.item_select_wallet_list, null, listWallet, getTag());
        listWallet.setAdapter(selectWalletListAdapter);

        RxAdapterView.itemClicks(listWallet)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        Wallet walletEntity = selectWalletListAdapter.getItem(position);
                        selectWalletListAdapter.notifyDataSetChanged();
                        dismiss();
                        if (mListener != null) {
                            mListener.onItemClick(walletEntity);
                        }
                    }
                });

        loadWalletList();

    }

    private void loadWalletList() {

        Flowable.fromCallable(new Callable<List<WalletEntity>>() {
            @Override
            public List<WalletEntity> call() throws Exception {
                return WalletDao.getWalletInfoList();
            }
        })
                .flatMap(new Function<List<WalletEntity>, Publisher<WalletEntity>>() {
                    @Override
                    public Publisher<WalletEntity> apply(List<WalletEntity> individualWalletInfoEntities) throws Exception {
                        return Flowable.fromIterable(individualWalletInfoEntities);
                    }
                })
                .map(new Function<WalletEntity, Wallet>() {
                    @Override
                    public Wallet apply(WalletEntity walletInfoEntity) throws Exception {
                        return walletInfoEntity.buildWallet();
                    }
                })
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return !wallet.isObservedWallet();
                    }
                })
                .map(new Function<Wallet, Wallet>() {
                    @Override
                    public Wallet apply(Wallet walletEntity) throws Exception {
                        Wallet mWallet = TransactionManager.getInstance().getBalanceByAddress(walletEntity);
                        mWallet.setAccountBalance(WalletManager.getInstance().getAccountBalance(mWallet.getPrefixAddress()));
                        return mWallet;
                    }
                })
                .toList()
                .onErrorReturnItem(new ArrayList<>())
                .compose(bindToLifecycle())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<Wallet>, Throwable>() {
                    @Override
                    public void accept(List<Wallet> objects, Throwable throwable) throws Exception {
                        if (objects != null && !objects.isEmpty()) {
                            String uuid = getArguments().getString(Constants.Bundle.BUNDLE_UUID);
                            boolean needAmount = getArguments().getBoolean(Constants.Bundle.BUNDLE_FEE_AMOUNT);
                            List<Wallet> newWalletEntityList = new ArrayList<>();
                            if (needAmount) {
                                for (Wallet walletEntity : objects) {
                                    if (BigDecimalUtil.isBiggerThanZero(walletEntity.getFreeBalance()) || BigDecimalUtil.isBiggerThanZero(walletEntity.getLockBalance())) {
                                        newWalletEntityList.add(walletEntity);
                                    }
                                }
                            } else {
                                newWalletEntityList.addAll(objects);
                            }
                            selectWalletListAdapter.notifyDataChanged(newWalletEntityList);
                            listWallet.setItemChecked(newWalletEntityList.indexOf(new Wallet.Builder().uuid(uuid).build()), true);
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Wallet walletEntity);
    }
}
