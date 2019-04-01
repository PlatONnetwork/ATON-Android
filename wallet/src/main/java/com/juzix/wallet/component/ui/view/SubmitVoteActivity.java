package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.SubmitVoteContract;
import com.juzix.wallet.component.ui.presenter.SubmitVotePresenter;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.AddressFormatUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import kotlin.Unit;

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
                .skipInitialValue()
                .subscribe(new Consumer<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) throws Exception {
                        mTicketNum = NumberParserUtils.parseInt(charSequence);
                        mPresenter.updateVotePayInfo();
                    }
                });

        RxView.clicks(sbtnVote)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
                        mPresenter.submitVote();
                    }
                });

        RxView.clicks(tvChangeWallet)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Unit>() {
                    @Override
                    public void accept(Unit unit) throws Exception {
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
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
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
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public CandidateEntity getCandidateFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_CANDIDATE);
    }

    @Override
    public void showNodeInfo(String nodeName, String nodeId) {
        tvNodeName.setText(nodeName);
        tvNodeId.setText(nodeId);
    }

    @Override
    public void showSelectedWalletInfo(IndividualWalletEntity individualWalletEntity) {
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

    public static void actionStart(Context context, CandidateEntity candidateEntity) {
        Intent intent = new Intent(context, SubmitVoteActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE, candidateEntity);
        context.startActivity(intent);
    }
}
