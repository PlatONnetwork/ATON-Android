package com.platon.aton.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.platon.aton.R;
import com.platon.aton.app.Constants;
import com.platon.aton.component.adapter.CommonAdapter;
import com.platon.aton.component.adapter.base.ViewHolder;
import com.platon.aton.entity.WithDrawBalance;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.CommonTextUtils;
import com.platon.aton.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ziv
 * date On 2020-02-06
 */
public class SelectDelegationsDialogFragment extends BaseDialogFragment {


    @BindView(R.id.list_delegations)
    ListView listDelegations;

    Unbinder unbinder;
    DelegationsAdapter delegationsAdapter;
    OnInvalidDelegationsClickListener invalidDelegationsClickListener;

    public SelectDelegationsDialogFragment setOnInvalidDelegationsClickListener(OnInvalidDelegationsClickListener onInvalidDelegationsClickListener) {
        invalidDelegationsClickListener = onInvalidDelegationsClickListener;
        return this;
    }

    public static SelectDelegationsDialogFragment newInstance(List<WithDrawBalance> withDrawBalanceList, int selectedPosition) {
        SelectDelegationsDialogFragment dialogFragment = new SelectDelegationsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Constants.Bundle.BUNDLE_DATA_LIST, (ArrayList<? extends Parcelable>) withDrawBalanceList);
        bundle.putInt(Constants.Bundle.BUNDLE_POSITION, selectedPosition);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_select_delegations, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 14));
        setyOffset(DensityUtil.dp2px(getContext(), 4));
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        ArrayList<WithDrawBalance> withDrawBalanceList = getArguments().getParcelableArrayList(Constants.Bundle.BUNDLE_DATA_LIST);
        int selectedPosition = getArguments().getInt(Constants.Bundle.BUNDLE_POSITION, 0);

        delegationsAdapter = new DelegationsAdapter(listDelegations, R.layout.item_select_delegations, withDrawBalanceList);

        listDelegations.setAdapter(delegationsAdapter);
        listDelegations.setItemChecked(selectedPosition, true);

        listDelegations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                delegationsAdapter.notifyDataSetChanged();
                if (invalidDelegationsClickListener != null) {
                    listDelegations.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismiss();
                            invalidDelegationsClickListener.onInvalidDelegationsClick(delegationsAdapter.getItem(position));
                        }
                    }, 500);
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

    static class DelegationsAdapter extends CommonAdapter<WithDrawBalance> {

        private ListView mListView;
        private boolean mExistDelegatedItem;

        public DelegationsAdapter(ListView listView, int layoutId, List<WithDrawBalance> datas) {
            super(layoutId, datas);
            this.mListView = listView;
            this.mExistDelegatedItem = isExistDelegatedItem(datas);
        }

        @Override
        protected void convert(Context context, ViewHolder viewHolder, WithDrawBalance item, int position) {
            TextView textView = viewHolder.getView(R.id.tv_invalid_delegations);
            if (item.isDelegated()) {
                textView.setText(context.getString(R.string.detail_delegated));
                viewHolder.setText(R.id.tv_invalid_delegations_amount, context.getString(R.string.amount_with_unit, AmountUtil.formatAmountText(item.getDelegated())));
            } else {
                int rank = mExistDelegatedItem ? position : position + 1;
                textView.setText(context.getString(R.string.msg_invalid_delegations, rank));
                CommonTextUtils.richText(textView, context.getString(R.string.msg_invalid_delegations, rank), "\\(.*\\)", new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_b6bbd0)));
                viewHolder.setText(R.id.tv_invalid_delegations_amount, context.getString(R.string.amount_with_unit, AmountUtil.formatAmountText(item.getReleased())));
            }

            textView.setFocusable(false);
            textView.setClickable(false);
            textView.setLongClickable(false);

            viewHolder.setVisible(R.id.iv_selected, mListView.getCheckedItemPosition() == position);
        }

        private boolean isExistDelegatedItem(List<WithDrawBalance> withDrawBalanceList) {
            for (int i = 0; i < withDrawBalanceList.size(); i++) {
                WithDrawBalance withDrawBalance = withDrawBalanceList.get(i);
                if (withDrawBalance.isDelegated()) {
                    return true;
                }
                continue;
            }
            return false;
        }
    }

    public interface OnInvalidDelegationsClickListener {

        void onInvalidDelegationsClick(WithDrawBalance withDrawBalance);
    }
}
