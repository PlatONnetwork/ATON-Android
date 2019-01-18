package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.component.widget.CommonTitleBar;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class SelectWalletInfoDialogFragment extends BaseDialogFragment {

    @BindView(R.id.commonTitleBar)
    CommonTitleBar commonTitleBar;
    @BindView(R.id.list_wallet)
    ListView       listWallet;

    private        Unbinder            mUnbinder;
    private        OnItemClickListener mListener;
    private static int                 mSelectedPosition;

    public static SelectWalletInfoDialogFragment newInstance(int selectedPos) {
        mSelectedPosition = selectedPos;
        SelectWalletInfoDialogFragment dialogFragment = new SelectWalletInfoDialogFragment();
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_select_wallet, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setAnimation(R.style.Animation_slide_in_bottom);
        mUnbinder = ButterKnife.bind(this, contentView);
        return baseDialog;
    }

    @OnItemClick({R.id.list_wallet})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mSelectedPosition = position;
            dismiss();
            mListener.onItemClick(position);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            mListener = (OnItemClickListener) context;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        commonTitleBar.setLeftImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        listWallet.setAdapter(new CommonAdapter<IndividualWalletEntity>(R.layout.item_select_wallet_list, IndividualWalletManager.getInstance().getWalletList()) {
            @Override
            protected void convert(Context context, ViewHolder viewHolder, IndividualWalletEntity item, int position) {
                viewHolder.setText(R.id.tv_wallet_name, item.getName());
                viewHolder.setText(R.id.tv_wallet_balance, item.getPrefixAddress());
                viewHolder.setImageResource(R.id.iv_wallet_pic, RUtils.drawable(item.getAvatar()));
                viewHolder.setVisible(R.id.iv_selected, position == mSelectedPosition);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    public interface OnItemClickListener {

        void onItemClick(int selectedPosition);
    }
}
