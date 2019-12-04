package com.juzix.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxAbsListView;
import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.RxUtils;
import com.juzix.wallet.utils.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReminderThresholdAmountDialogFragment extends BaseDialogFragment {

    @BindView(R.id.list_reminder_threshold_amount)
    ListView listReminderThresholdAmount;
    @BindArray(R.array.reminder_threshold_amount)
    String[] reminderThresholdAmountArray;

    private Unbinder unbinder;
    private OnReminderThresholdAmountItemClickListener reminderThresholdAmountItemClickListener;

    public static ReminderThresholdAmountDialogFragment newInstance(String reminderThresholdAmount) {
        ReminderThresholdAmountDialogFragment dialogFragment = new ReminderThresholdAmountDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.Bundle.BUNDLE_DATA, reminderThresholdAmount);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    public ReminderThresholdAmountDialogFragment setOnReminderThresholdAmountItemClickListener(OnReminderThresholdAmountItemClickListener reminderThresholdAmountItemClickListener) {
        this.reminderThresholdAmountItemClickListener = reminderThresholdAmountItemClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_reminder_threhold_amount, null, false);
        baseDialog.setContentView(contentView);
        setFullWidthEnable(true);
        setGravity(Gravity.BOTTOM);
        setAnimation(R.style.Animation_slide_in_bottom);
        setHorizontalMargin(DensityUtil.dp2px(getContext(), 24));
        setyOffset(DensityUtil.dp2px(getContext(), 16));
        unbinder = ButterKnife.bind(this, contentView);
        initViews();
        return baseDialog;
    }

    private void initViews() {

        String reminderThresholdAmount = getArguments().getString(Constants.Bundle.BUNDLE_DATA);
        List<String> reminderThresholdAmountList = Arrays.asList(reminderThresholdAmountArray);

        ReminderThresholdAmountAdapter reminderThresholdAmountAdapter = new ReminderThresholdAmountAdapter(R.layout.item_reminder_threshold_amount, reminderThresholdAmountList, listReminderThresholdAmount);

        listReminderThresholdAmount.setAdapter(reminderThresholdAmountAdapter);
        listReminderThresholdAmount.setItemChecked(reminderThresholdAmountList.indexOf(reminderThresholdAmount), true);

        RxAdapterView
                .itemClicks(listReminderThresholdAmount)
                .compose(RxUtils.getClickTransformer())
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        reminderThresholdAmountAdapter.notifyDataSetChanged();
                        if (reminderThresholdAmountItemClickListener != null) {
                            listReminderThresholdAmount.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    reminderThresholdAmountItemClickListener.onReminderThresholdAmountItemClick(reminderThresholdAmountAdapter.getItem(integer));
                                    dismiss();
                                }
                            }, 200);
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

    static class ReminderThresholdAmountAdapter extends CommonAdapter<String> {

        private ListView mListView;

        public ReminderThresholdAmountAdapter(int layoutId, List<String> datas, ListView listView) {
            super(layoutId, datas);
            this.mListView = listView;
        }

        @Override
        protected void convert(Context context, ViewHolder viewHolder, String item, int position) {
            viewHolder.setText(R.id.tv_reminder_threshold_amount, context.getString(R.string.amount_with_unit, StringUtil.formatBalanceWithoutMinFraction(item)));
            viewHolder.setVisible(R.id.iv_selected, mListView.getCheckedItemPosition() == position);
        }
    }

    public interface OnReminderThresholdAmountItemClickListener {

        void onReminderThresholdAmountItemClick(String reminderThresholdAmount);
    }
}
