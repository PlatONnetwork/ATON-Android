package com.juzix.wallet.component.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.component.adapter.CommonAdapter;
import com.juzix.wallet.component.adapter.base.ViewHolder;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.DensityUtil;

import java.util.List;

import jnr.constants.platform.PRIO;

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
    private View mContentView;

    public WalletListPop(Context context, List<Wallet> walletList) {
        super(context);
        this.mWalletList = walletList;
        this.mContext = context;
        this.mContentHeight = walletList.size() > 5 ? DensityUtil.dp2px(context, 390) : DensityUtil.dp2px(context, 65) * (walletList.size() + 1);

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

        mWalletListAdapter = new SelectWalletListAdapter(R.layout.item_pop_wallets, mWalletList, mWalletListView);

        mWalletListView.setAdapter(mWalletListAdapter);

        mWalletListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    static class SelectWalletListAdapter extends CommonAdapter<Wallet> {

        private final int VIEW_TYPE_ALL_WALLET = 0;
        private final int VIEW_TYPE_SINGLE_WALLET = 1;

        private ListView mListView;

        public SelectWalletListAdapter(int layoutId, List<Wallet> datas, ListView listView) {
            super(layoutId, datas);
            this.mListView = listView;
        }

        @Override
        protected void convert(Context context, ViewHolder viewHolder, Wallet item, int position) {
            int viewType = getItemViewType(position);
            if (viewType == VIEW_TYPE_ALL_WALLET){
                viewHolder.setText(R.id.tv_wallet_name, "");
                int avatar = RUtils.id(item.getAvatar());
                if (avatar != -1) {
                    viewHolder.setImageResource(R.id.iv_wallet_avatar, avatar);
                }
                viewHolder.setVisible(R.id.iv_selected, mListView != null && mListView.getCheckedItemPosition() == position);
            }else{
                viewHolder.setText(R.id.tv_wallet_name, item.getName());
                int avatar = RUtils.id(item.getAvatar());
                if (avatar != -1) {
                    viewHolder.setImageResource(R.id.iv_wallet_avatar, avatar);
                }
                viewHolder.setVisible(R.id.iv_selected, mListView != null && mListView.getCheckedItemPosition() == position);
            }
        }

        @Override
        public int getCount() {
            return getList().size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0){
                return VIEW_TYPE_ALL_WALLET;
            }else{
                return VIEW_TYPE_SINGLE_WALLET;
            }
        }
    }
}
