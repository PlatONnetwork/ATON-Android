package com.platon.wallet.component.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.jakewharton.rxbinding2.widget.RxAdapterView;
import com.platon.wallet.R;
import com.platon.wallet.app.Constants;
import com.platon.wallet.component.adapter.CommonAdapter;
import com.platon.wallet.component.adapter.base.ViewHolder;
import com.platon.wallet.component.ui.OnItemClickListener;
import com.platon.wallet.component.ui.SortType;
import com.platon.wallet.utils.DensityUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

public class SelectSortTypeDialogFragment extends BaseDialogFragment {


    @BindView(R.id.list_sort_type)
    ListView listSortType;

    private Unbinder unbinder;
    private OnItemClickListener mItemClickListener;

    public static SelectSortTypeDialogFragment newInstance(SortType sortType) {
        SelectSortTypeDialogFragment dialogFragment = new SelectSortTypeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.Bundle.BUNDLE_DATA, sortType);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }


    public SelectSortTypeDialogFragment setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mItemClickListener = onItemClickListener;
        return this;
    }

    @Override
    protected Dialog onCreateDialog(Dialog baseDialog) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_fragment_select_sort_type, null, false);
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


        SortType sortType = getArguments().getParcelable(Constants.Bundle.BUNDLE_DATA);

        SortTypeAdapter sortTypeAdapter = new SortTypeAdapter(R.layout.item_select_sort_type, Arrays.asList(SortType.values()), listSortType);

        listSortType.setAdapter(sortTypeAdapter);

        listSortType.setItemChecked(Arrays.asList(SortType.values()).indexOf(sortType), true);

        RxAdapterView.itemClicks(listSortType)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer position) throws Exception {
                        sortTypeAdapter.notifyDataSetChanged();
                        dismiss();
                        if (mItemClickListener != null) {
                            mItemClickListener.onItemClick(sortTypeAdapter.getItem(position));
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

    static class SortTypeAdapter extends CommonAdapter<SortType> {

        private ListView mListView;

        public SortTypeAdapter(int layoutId, List<SortType> datas, ListView listView) {
            super(layoutId, datas);
            this.mListView = listView;
        }

        @Override
        protected void convert(Context context, ViewHolder viewHolder, SortType item, int position) {
            viewHolder.setText(R.id.tv_sort_type, context.getResources().getString(item.getTextRes()));
            viewHolder.setVisible(R.id.iv_selected, mListView.getCheckedItemPosition() == position);
        }
    }
}
