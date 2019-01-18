package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.component.ui.presenter.TransactionRecordsPresenter;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionEntity;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.DateUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class TransactionRecordsActivity extends MVPBaseActivity<TransactionRecordsPresenter> implements TransactionRecordsContract.View {

    private final static String                        TAG = TransactionRecordsActivity.class.getSimpleName();
    private CommonAdapter<TransactionEntity> mAdapter;

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, TransactionRecordsActivity.class));
    }

    @Override
    protected TransactionRecordsPresenter createPresenter() {
        return new TransactionRecordsPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventPublisher.getInstance().register(this);
        setContentView(R.layout.activity_transaction_records);
        mPresenter.fetchTransactions();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSharedWalletChangedEvent(Event.UpdateSharedWalletTransactionEvent event) {
        mPresenter.refreshRecords();
    }

    @Override
    protected void onDestroy() {
        EventPublisher.getInstance().unRegister(this);
        super.onDestroy();
    }

    @Override
    public void showTransactions(ArrayList<TransactionEntity> transactionEntities) {
        View emptyView = findViewById(R.id.layout_no_data);
        ListView listView = findViewById(R.id.list_transactions);
        mAdapter = new CommonAdapter<TransactionEntity>(R.layout.item_transaction_record, transactionEntities) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, TransactionEntity item, int position) {
                TransactionEntity.TransactionStatus status = item.getTransactionStatus();
                if (item instanceof SharedTransactionEntity){
                    SharedTransactionEntity entity = (SharedTransactionEntity) item;
                    SharedTransactionEntity.TransactionStatus transactionStatus = item.getTransactionStatus();
                    boolean isReceiver = item.isReceiver(entity.getContractAddress());
                    viewHolder.setText(R.id.tv_amount, String.format("%s%s", isReceiver ? "+" : "-", NumberParserUtils.getPrettyBalance(item.getValue())));
                    viewHolder.setText(R.id.tv_name, isReceiver ? string(R.string.action_receive_transation) : string(R.string.action_send_transation));
                    viewHolder.setText(R.id.tv_desc, transactionStatus.getStatusDesc(context, entity.getConfirms(), entity.getRequiredSignNumber()));
                }else{
                    IndividualTransactionEntity entity = (IndividualTransactionEntity) item;
                    viewHolder.setText(R.id.tv_name, string(R.string.action_send_transation));
                    viewHolder.setText(R.id.tv_desc, status.getStatusDesc(context, item.getSignedBlockNumber(), 12));
                }
                viewHolder.setText(R.id.tv_amount, context.getString(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(item.getValue(), 4)));
                viewHolder.setText(R.id.tv_time, DateUtil.format(item.getCreateTime(), DateUtil.DATETIME_FORMAT_PATTERN));
                viewHolder.setTextColor(R.id.tv_desc, ContextCompat.getColor(context, status.getStatusDescTextColor()));
            }
        };
        listView.setAdapter(mAdapter);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TransactionEntity item = (TransactionEntity) parent.getAdapter().getItem(position);
                    if (item instanceof  IndividualTransactionEntity){
                        IndividualTransactionDetailActivity.actionStart(currentActivity(), (IndividualTransactionEntity) item, null);
                    }else if (item instanceof  SharedTransactionEntity){
                        SharedTransactionEntity sharedTransactionEntity = (SharedTransactionEntity) item;
                        SharedWalletEntity mSharedWalletEntity = SharedWalletManager.getInstance().getWalletByContractAddress(sharedTransactionEntity.getContractAddress());
                        if (!sharedTransactionEntity.isRead()){
                            sharedTransactionEntity.setRead(true);
                            SharedWalletTransactionManager.getInstance().updateTransactionForRead(mSharedWalletEntity, sharedTransactionEntity);
                        }
                        BaseActivity activity = currentActivity();
                        if (sharedTransactionEntity.transfered()){
                            SharedTransactionDetailActivity.actionStart(activity, sharedTransactionEntity);
                        }else {
                            SigningActivity.actionStart(activity, sharedTransactionEntity);
                        }
                    }
            }
        });
    }
}
