package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juzix.wallet.R;
import com.juzix.wallet.component.widget.ShadowButton;
import com.juzix.wallet.utils.DensityUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ClaimRewardsDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_claim_rewards_amount)
    TextView tvClaimRewardsAmount;
    @BindView(R.id.tv_claim_rewards_tips)
    TextView tvClaimRewardsTips;
    @BindView(R.id.tv_fee_amount)
    TextView tvFeeAmount;
    @BindView(R.id.tv_claim_wallet)
    TextView tvClaimWallet;
    @BindView(R.id.tv_balance_amount)
    TextView tvBalanceAmount;
    @BindView(R.id.sbtn_confirm)
    ShadowButton sbtnConfirm;

    private Unbinder unbinder;

    public static ClaimRewardsDialogFragment newInstance() {
        ClaimRewardsDialogFragment dialogFragment = new ClaimRewardsDialogFragment();
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_claim_rewards, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14f));
        setyOffset(DensityUtil.dp2px(getContext(), 4f));
        setAnimation(R.style.Animation_slide_in_bottom);
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
