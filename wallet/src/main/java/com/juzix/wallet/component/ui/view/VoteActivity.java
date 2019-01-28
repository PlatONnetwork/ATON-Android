package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.MVPBaseActivity;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletBalanceDialogFragment;
import com.juzix.wallet.component.ui.presenter.VotePresenter;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;

/**
 * @author matrixelement
 */
public class VoteActivity extends MVPBaseActivity<VotePresenter> implements VoteContract.View, View.OnClickListener, TextWatcher, SelectIndividualWalletBalanceDialogFragment.OnItemClickListener {

    private EditText mEtVotes;
    public static void actionStart(Context context, CandidateEntity candidateEntity) {
        Intent intent = new Intent(context, VoteActivity.class);
        intent.putExtra(Constants.Extra.EXTRA_CANDIDATE, candidateEntity);
        context.startActivity(intent);
    }

    @Override
    public CandidateEntity getCandidateFromIntent() {
        return getIntent().getParcelableExtra(Constants.Extra.EXTRA_CANDIDATE);
    }

    @Override
    protected VotePresenter createPresenter() {
        return new VotePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);
        initView();
        mPresenter.init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_sub:
                mPresenter.subVotes();
                break;
            case R.id.iv_add:
                mPresenter.addVotes();
                break;
            case R.id.layout_change_wallet:
                mPresenter.showSelectWalletDialogFragment();
                break;
            case R.id.rtv_vote:
                mPresenter.submit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(IndividualWalletEntity walletEntity) {
        mPresenter.updateSelectOwner(walletEntity);
    }

    @Override
    protected void onDestroy() {
        mEtVotes.removeTextChangedListener(this);
        super.onDestroy();
    }

    private void initView() {
        CommonTitleBar titleBar = findViewById(R.id.commonTitleBar);
        titleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInput();
                finish();
            }
        });
        findViewById(R.id.iv_sub).setOnClickListener(this);
        findViewById(R.id.iv_add).setOnClickListener(this);
        findViewById(R.id.layout_change_wallet).setOnClickListener(this);
        findViewById(R.id.rtv_vote).setOnClickListener(this);
        mEtVotes = findViewById(R.id.et_vote);
        mEtVotes.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        mEtVotes.addTextChangedListener(this);
        showVotes(1);
    }

    @Override
    public void showVoteInfo(int iconRes, String name, String publicKey) {
        ((ImageView) findViewById(R.id.iv_icon)).setImageResource(iconRes);
        ((TextView) findViewById(R.id.tv_name)).setText(name);
        ((TextView) findViewById(R.id.tv_address)).setText(publicKey);
    }

    public void showPayInfo(double price, double expectedPay) {
        ((TextView) findViewById(R.id.tv_ticket_price)).setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(price)));
        ((TextView) findViewById(R.id.tv_expected_pay)).setText(getString(R.string.amount_with_unit, NumberParserUtils.parseDoubleToPrettyNumber(expectedPay)));
    }

    @Override
    public String getVotes() {
        return mEtVotes.getText().toString();
    }

    public void updateSelectOwner(IndividualWalletEntity walletEntity) {
        ((TextView) findViewById(R.id.tv_wallet_name)).setText(walletEntity == null ? "" : walletEntity.getName());
        ((TextView) findViewById(R.id.tv_wallet_address)).setText(walletEntity == null ? "" : string(R.string.msg_balance) + " " + string(R.string.wallet_balance, NumberParserUtils.getPrettyDetailBalance(walletEntity.getBalance())));
    }

    @Override
    public void setNextButtonEnable(boolean enabled) {
        findViewById(R.id.rtv_vote).setEnabled(enabled);
    }

    @Override
    public void showVotes(long votes) {
        String text = String.valueOf(votes);
        mEtVotes.setText(text);
        mEtVotes.setSelection(TextUtils.isEmpty(text) ? 0 : text.length());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mPresenter.setVotes(mEtVotes.getText().toString());
    }
}
