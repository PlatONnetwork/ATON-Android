package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzhen.framework.util.AndroidUtil;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.SubmitVoteContract;
import com.juzix.wallet.component.ui.presenter.SubmitVotePresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.AddressFormatUtil;
import com.juzix.wallet.utils.CommonUtil;
import com.juzix.wallet.utils.RxUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;

/**
 * @author matrixelement
 */
public class SubmitVoteActivity extends MVPBaseActivity<SubmitVotePresenter> implements SubmitVoteContract.View {

    @BindView(R.id.layout_node_name)
    ConstraintLayout layoutNodeName;
    @BindView(R.id.tv_node_name)
    TextView tvNodeName;
    @BindView(R.id.tv_node_id)
    TextView tvNodeId;
    @BindView(R.id.iv_wallet_image)
    ImageView ivWalletImage;
    @BindView(R.id.tv_wallet_name)
    TextView tvWalletName;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.tv_change_wallet)
    TextView tvChangeWallet;
    @BindView(R.id.layout_voting_wallet)
    ConstraintLayout layoutVotingWallet;
    @BindView(R.id.tv_tickets_desc)
    TextView tvTicketsDesc;
    @BindView(R.id.iv_sub_ticket)
    ImageView ivSubTicket;
    @BindView(R.id.iv_add_ticket)
    ImageView ivAddTicket;
    @BindView(R.id.et_ticket_num)
    EditText etTicketNum;
    @BindView(R.id.view_input_ticket_num)
    View viewInputTicketNum;
    @BindView(R.id.tv_ticket_price_desc)
    TextView tvTicketPriceDesc;
    @BindView(R.id.tv_ticket_price)
    TextView tvTicketPrice;
    @BindView(R.id.tv_estimated_payment)
    TextView tvEstimatedPayment;
    @BindView(R.id.sbtn_vote)
    ShadowButton sbtnVote;

    private final static int DEFAULT_VOTE_NUM = 1;

    Unbinder unbinder;
    int mTicketNum = 1;

    @Override
    protected SubmitVotePresenter createPresenter() {
        return new SubmitVotePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_submit_vote);
        unbinder = ButterKnife.bind(this);
        initViews();
        mPresenter.showVoteInfo();
    }

    private void initViews() {

        setTicketNum(DEFAULT_VOTE_NUM);

        RxTextView
                .textChanges(etTicketNum)
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        mTicketNum = NumberParserUtils.parseInt(charSequence);
                        mPresenter.updateVotePayInfo();
                        sbtnVote.setEnabled(mTicketNum >= 1);
                    }
                });

        RxView.clicks(sbtnVote)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        mPresenter.submitVote();
                    }
                });

        RxView.clicks(tvChangeWallet)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object object) {
                        mPresenter.showSelectWalletDialogFragment();
                    }
                });

        Observable<CharSequence> walletNameObservable = RxTextView.textChanges(tvWalletName).skipInitialValue();
        Observable<CharSequence> walletAddressObservable = RxTextView.textChanges(tvWalletAddress).skipInitialValue();
        Observable
                .combineLatest(walletNameObservable, walletAddressObservable, new BiFunction<CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean apply(CharSequence charSequence, CharSequence charSequence2) throws Exception {
                        return !TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(charSequence2);
                    }
                })
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        sbtnVote.setEnabled(aBoolean);
                    }
                });

        mTicketNum = NumberParserUtils.parseInt(getTicketNum());
    }

    @OnClick({R.id.iv_sub_ticket, R.id.iv_add_ticket})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sub_ticket:
                if (mTicketNum <= 1) {
                    return;
                }
                setTicketNum(--mTicketNum);
                break;
            case R.id.iv_add_ticket:
                setTicketNum(++mTicketNum);
                break;
            default:
                break;

        }
    }

    @Override
    public String getCandidateIdFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_ID);
    }

    @Override
    public String getCandidateNameFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_NAME);
    }

    @Override
    public String getCandidateDepositFromIntent() {
        return getIntent().getStringExtra(Constants.Extra.EXTRA_CANDIDATE_DEPOSIT);
    }

    @Override
    public void showNodeInfo(String nodeName, String nodeId) {
        tvNodeName.setText(nodeName);
        tvNodeId.setText(nodeId);
    }

    @Override
    public void showSelectedWalletInfo(Wallet individualWalletEntity) {
        tvWalletName.setText(individualWalletEntity.getName());
        tvWalletAddress.setText(AddressFormatUtil.formatAddress(individualWalletEntity.getPrefixAddress()));
    }

    @Override
    public void showVotePayInfo(double ticketPrice, double ticketPayAmount) {
        tvTicketPrice.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(ticketPrice, 0)));
        tvEstimatedPayment.setText(string(R.string.amount_with_unit, NumberParserUtils.getPrettyNumber(ticketPayAmount, 0)));
    }

    @Override
    public String getTicketNum() {
        return etTicketNum.getText().toString().trim();
    }

    @Override
    public void setTicketNum(int ticketNum) {
        etTicketNum.setText(String.valueOf(ticketNum));
        etTicketNum.setSelection(String.valueOf(ticketNum).length());
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (AndroidUtil.isOutSizeView(v, ev)) {
                hideSoftInput();
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    public static void actionStart(Context context, String nodeId, String nodeName, String deposit) {
        Intent intent = new Intent(context, SubmitVoteActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_ID, nodeId);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_NAME, nodeName);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE_DEPOSIT, deposit);
        context.startActivity(intent);
    }
}
