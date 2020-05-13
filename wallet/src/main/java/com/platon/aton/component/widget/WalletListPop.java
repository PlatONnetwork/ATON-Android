package com.platon.aton.component.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.DensityUtil;

import java.util.List;

public class WalletListPop extends PopupWindow {

    private List<Wallet> mWalletList;

    private ListView mWalletListView;
    private SelectWalletListAdapter mWalletListAdapter;
    private Context mContext;
    private AnimatorSet mOpenAnimator;
    private AnimatorSet mCloseAnimator;

    private boolean mDismissed = false;
    private int mDuration = 100;
    private int mContentHeight;
    private int mSelectedWalletPosition = 0;
    private View mContentView;
    private OnWalletItemClickListener mWalletItemClickListener;

    public WalletListPop(Context context, List<Wallet> walletList, OnWalletItemClickListener walletItemClickListener,int selectedWalletPosition) {
        super(context);
        this.mWalletList = walletList;
        this.mContext = context;
        this.mWalletItemClickListener = walletItemClickListener;
        this.mContentHeight = walletList.size() > 5 ? DensityUtil.dp2px(context, 390) + DensityUtil.dp2px(context, 20) : DensityUtil.dp2px(context, 65) * walletList.size() + DensityUtil.dp2px(context, 20);
        this.mSelectedWalletPosition = selectedWalletPosition;

        View rootView = LayoutInflater.from(context).inflate(R.layout.pop_select_wallets, null);

        setClippingEnabled(false);
        setContentView(rootView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(mContentHeight);
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(false);
        setOutsideTouchable(true);

        initViews(rootView);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);

        mOpenAnimator.start();
    }

    @Override
    public void dismiss() {
        if (mDismissed) {
            super.dismiss();
            mDismissed = false;
        } else {
            if (mCloseAnimator != null) {
                mCloseAnimator.start();
            }
        }
    }

    private void initViews(View rootView) {

        mWalletListView = rootView.findViewById(R.id.list_wallet);

        mContentView = rootView.findViewById(R.id.layout_content);

        mOpenAnimator = createOpenAnimation();
        mCloseAnimator = createCloseAnimation();

        ShadowDrawable.setShadowDrawableWithShadowMode(mContentView,
                ContextCompat.getColor(mContext, R.color.color_ffffff),
                DensityUtil.dp2px(mContext, 4),
                ContextCompat.getColor(mContext, R.color.color_cc9ca7c2),
                DensityUtil.dp2px(mContext, 10),
                0,
                0, ShadowDrawable.SHADOW_BOTTOM | ShadowDrawable.SHADOW_LEFT | ShadowDrawable.SHADOW_RIGHT);

        mWalletListAdapter = new SelectWalletListAdapter(mWalletList, mWalletListView);

        mWalletListView.setAdapter(mWalletListAdapter);
        mWalletListView.setItemChecked(mSelectedWalletPosition, true);
        mWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mWalletListAdapter.notifyDataSetChanged();
                if (mWalletItemClickListener != null) {
                    mWalletItemClickListener.onWalletItemClick(position);
                    dismiss();
                }
            }
        });
    }

    private static ValueAnimator createDropAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private AnimatorSet createOpenAnimation() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(mDuration);
        ValueAnimator downAnimator = createDropAnimator(mContentView, 0, mContentHeight);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this.getContentView(), View.ALPHA, 0.0f, 1.0f);
        set.playTogether(downAnimator, alphaAnimator);
        set.setDuration(mDuration);
        return set;
    }

    private AnimatorSet createCloseAnimation() {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(mDuration);
        ValueAnimator upAnimator = createDropAnimator(mContentView, mContentHeight, 0);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(this.getContentView(), View.ALPHA, 1.0f, 0.0f);
        set.playTogether(upAnimator, alphaAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDismissed = true;
                dismiss();
            }

        });
        set.setDuration(mDuration);
        return set;
    }

    static class SelectWalletListAdapter extends BaseAdapter {

        private final int VIEW_TYPE_ALL_WALLET = 0;
        private final int VIEW_TYPE_SINGLE_WALLET = 1;

        private ListView mListView;
        private List<Wallet> mWalletList;

        public SelectWalletListAdapter(List<Wallet> walletList, ListView mListView) {
            this.mWalletList = walletList;
            this.mListView = mListView;
        }

        @Override
        public int getCount() {
            if (mWalletList != null) {
                return mWalletList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mWalletList != null) {
                return mWalletList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pop_wallets, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Wallet wallet = mWalletList.get(position);
            int viewType = getItemViewType(position);

            viewHolder.walletNameTv.setText(viewType == VIEW_TYPE_ALL_WALLET ? parent.getContext().getString(R.string.msg_all_wallets) : wallet.getName());

            int avatar = viewType == VIEW_TYPE_ALL_WALLET ? R.drawable.icon_all_wallets : RUtils.drawable(wallet.getAvatar());
            if (avatar != -1) {
                viewHolder.walletAvatarIv.setImageResource(avatar);
            }

            boolean isSelected = mListView != null && mListView.getCheckedItemPosition() == position;

            viewHolder.selectedIv.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            viewHolder.walletNameTv.setTextAppearance(parent.getContext(),isSelected ? R.style.Text_000000_16:R.style.Text_898c9e_16);
            viewHolder.walletNameTv.setTypeface(isSelected ? Typeface.DEFAULT_BOLD:Typeface.DEFAULT);

            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return VIEW_TYPE_ALL_WALLET;
            } else {
                return VIEW_TYPE_SINGLE_WALLET;
            }
        }

        static class ViewHolder {

            private TextView walletNameTv;
            private ImageView walletAvatarIv;
            private ImageView selectedIv;

            public ViewHolder(View convertView) {
                walletNameTv = convertView.findViewById(R.id.tv_wallet_name);
                walletAvatarIv = convertView.findViewById(R.id.iv_wallet_avatar);
                selectedIv = convertView.findViewById(R.id.iv_selected);
            }
        }
    }

    public interface OnWalletItemClickListener {
        void onWalletItemClick(int position);
    }
}
