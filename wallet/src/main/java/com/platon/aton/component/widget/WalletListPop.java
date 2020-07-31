package com.platon.aton.component.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.platon.aton.entity.NullTransactionWallet;
import com.platon.aton.entity.TransactionWallet;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.RUtils;
import com.platon.aton.R;
import com.platon.aton.entity.Wallet;
import com.platon.aton.utils.DensityUtil;
import com.platon.framework.utils.ToastUtil;

import java.util.List;

public class WalletListPop extends PopupWindow {

    private List<TransactionWallet> mTransactionWallettList;

    private ListView mWalletListView;
    private SelectWalletListAdapter mWalletListAdapter;
    private Context mContext;
    private AnimatorSet mOpenAnimator;
    private AnimatorSet mCloseAnimator;

    private boolean mDismissed = false;
    private int mDuration = 100;
    private int mContentHeight;
    private int mSelectedWalletPosition = -1;
    private View mContentView;
    private OnWalletItemClickListener mWalletItemClickListener;
    private OnSubWalletItemClickListener mSubWalletItemClickListener;
    private OnSelectWalletItemInfoListener mSelectWalletItemInfoListener;

    public WalletListPop(Context context, List<TransactionWallet> transactionWallettList,
                         OnWalletItemClickListener walletItemClickListener,
                         OnSubWalletItemClickListener subWalletItemClickListener,
                         int selectedWalletPosition,
                         OnSelectWalletItemInfoListener mSelectWalletItemInfoListener) {
        super(context);
        this.mTransactionWallettList = transactionWallettList;
        this.mContext = context;
        this.mWalletItemClickListener = walletItemClickListener;
        this.mSubWalletItemClickListener = subWalletItemClickListener;
        this.mSelectWalletItemInfoListener = mSelectWalletItemInfoListener;
        this.mContentHeight = mTransactionWallettList.size() > 5 ? DensityUtil.dp2px(context, 390) + DensityUtil.dp2px(context, 20) : DensityUtil.dp2px(context, 65) * mTransactionWallettList.size() + DensityUtil.dp2px(context, 20);
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

        mWalletListAdapter = new SelectWalletListAdapter(mTransactionWallettList, mWalletListView,mContext);

        mWalletListView.setAdapter(mWalletListAdapter);
       /* if(mSelectedWalletPosition != -1){
            mWalletListView.setItemChecked(mSelectedWalletPosition, true);
        }*/

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




    class SelectWalletListAdapter extends BaseAdapter {

        private final int VIEW_TYPE_ALL_WALLET = 0;
        private final int VIEW_TYPE_SINGLE_WALLET = 1;

        private ListView mListView;
        private List<TransactionWallet> mTransactionWallettList;
        private Context context;

        public SelectWalletListAdapter(List<TransactionWallet> transactionWallettList, ListView mListView,Context context) {
            this.mTransactionWallettList = transactionWallettList;
            this.mListView = mListView;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (mTransactionWallettList != null) {
                return mTransactionWallettList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (mTransactionWallettList != null) {
                return mTransactionWallettList.get(position);
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
            TransactionWallet transactionWallet = mTransactionWallettList.get(position);
           /* if(position >= 0){
                LogUtils.e("postion:"+ position + "--------item transactionWallet" + mTransactionWallettList.get(position).toString());
                //LogUtils.e("--------item transactionWallet.getSubWallets.size" + mTransactionWallettList.get(position).getSubWallets().size() + "position:" + position);

            }*/

            int viewType = getItemViewType(position);

            viewHolder.walletNameTv.setText(viewType == VIEW_TYPE_ALL_WALLET ? parent.getContext().getString(R.string.msg_all_wallets) : transactionWallet.getWallet().getName());

            int avatar = viewType == VIEW_TYPE_ALL_WALLET ? R.drawable.icon_all_wallets : RUtils.drawable(transactionWallet.getWallet().getAvatar());
            if (avatar != -1) {
                viewHolder.walletAvatarIv.setImageResource(avatar);
            }
            //boolean isSelected = mListView != null && mListView.getCheckedItemPosition() == position;
            //设置选中框
            boolean isSelected = false;
            if(transactionWallet.getWallet() != null ){
                if(mSelectWalletItemInfoListener != null){
                    String selectedWalletUuid =  mSelectWalletItemInfoListener.onSelectWalletItemInfo();
                    if((selectedWalletUuid != null && !TextUtils.isEmpty(selectedWalletUuid)) && selectedWalletUuid.equals(transactionWallet.getWallet().getUuid())){
                        isSelected = true;
                    }else{
                        isSelected = false;
                    }
                }
            }else{
                isSelected = false;
            }
            viewHolder.selectedIv.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            viewHolder.walletNameTv.setTextAppearance(parent.getContext(),isSelected ? R.style.Text_000000_16:R.style.Text_898c9e_16);
            viewHolder.walletNameTv.setTypeface(isSelected ? Typeface.DEFAULT_BOLD:Typeface.DEFAULT);



            //--------------------------  为母钱包设置子钱包布局  ------------------------------------

            if(transactionWallet.getWallet() != null && transactionWallet.getWallet().isHD() && transactionWallet.getWallet().getDepth() == 0){

                //子钱包布局开关
                viewHolder.subwalletIv.setVisibility((transactionWallet.getWallet().isHD() && transactionWallet.getWallet().getDepth() == 0) ? View.VISIBLE : View.GONE);
                //viewHolder.subwalletIv.setEnabled((!wallet.isHD() && wallet.getDepth() == 0) ? true : false);
                ViewHolder finalViewHolder = viewHolder;
                viewHolder.subwalletIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(finalViewHolder.layoutSubwallet.getVisibility() == View.VISIBLE){
                            finalViewHolder.layoutSubwallet.setVisibility(View.GONE);
                        }else{
                            finalViewHolder.layoutSubwallet.setVisibility(View.VISIBLE);
                        }

                        LogUtils.e("------onclick");

                    }
                });

                //添加子布局
                if(transactionWallet.getSubWallets() != null && transactionWallet.getSubWallets().size() > 0){

                    viewHolder.layoutSubwallet.removeAllViews();
                    for (int i = 0; i < transactionWallet.getSubWallets().size() ; i++) {
                        View subWalletView = LayoutInflater.from(context).inflate(R.layout.item_pop_subwallets,null,false);
                        View subView = addSubWalletView(subWalletView,transactionWallet.getSubWallets().get(i));
                        viewHolder.layoutSubwallet.addView(subView);
                    }
                }
            }else{
                viewHolder.subwalletIv.setVisibility(View.INVISIBLE);
            }
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

         class ViewHolder {

            private TextView walletNameTv;
            private ImageView walletAvatarIv;
            private ImageView selectedIv;
            private ImageView subwalletIv;
            private LinearLayout layoutSubwallet;
            private LinearLayout constraintLayout;

            public ViewHolder(View convertView) {
                walletNameTv = convertView.findViewById(R.id.tv_wallet_name);
                walletAvatarIv = convertView.findViewById(R.id.iv_wallet_avatar);
                selectedIv = convertView.findViewById(R.id.iv_selected);
                subwalletIv = convertView.findViewById(R.id.iv_subwallet);
                layoutSubwallet = convertView.findViewById(R.id.layout_subwallet);
                constraintLayout = convertView.findViewById(R.id.layout_container);
            }
        }
    }

    public View addSubWalletView(View subWalletView,Wallet subWallet){

        ImageView ivSubwalletAvatar = subWalletView.findViewById(R.id.iv_subwallet_avatar);
        ImageView ivSubselected = subWalletView.findViewById(R.id.iv_subselected);
        TextView tvSubwalletName = subWalletView.findViewById(R.id.tv_subwallet_name);
        ConstraintLayout layout_subwallet = subWalletView.findViewById(R.id.layout_subwallet);
        layout_subwallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivSubselected.setVisibility(View.VISIBLE);
                if(mSubWalletItemClickListener != null){
                    mSubWalletItemClickListener.onSubWalletItemClick(subWallet);
                }
            }
        });

        if(mSelectWalletItemInfoListener != null){
           String selectedWalletUuid =  mSelectWalletItemInfoListener.onSelectWalletItemInfo();
            if((selectedWalletUuid != null && !TextUtils.isEmpty(selectedWalletUuid)) && selectedWalletUuid.equals(subWallet.getUuid())){
                ivSubselected.setVisibility(View.VISIBLE);
                tvSubwalletName.setTextAppearance(mContext,R.style.Text_000000_16);
                tvSubwalletName.setTypeface(Typeface.DEFAULT_BOLD);
            }else{
                ivSubselected.setVisibility(View.INVISIBLE);
                tvSubwalletName.setTextAppearance(mContext,R.style.Text_898c9e_16);
                tvSubwalletName.setTypeface(Typeface.DEFAULT);
            }
        }

        tvSubwalletName.setText(subWallet.getName());
        int avatar = RUtils.drawable(subWallet.getAvatar());
        if (avatar != -1) {
            ivSubwalletAvatar.setImageResource(avatar);
        }


        return subWalletView;
    }

    //listview点击回调
    public interface OnWalletItemClickListener {
        void onWalletItemClick(int position);
    }

    //listview嵌套子list点击回调
    public interface OnSubWalletItemClickListener {
        void onSubWalletItemClick(Wallet selectedWallet);
    }
    //选中钱包获取回调
    public interface OnSelectWalletItemInfoListener {
        String onSelectWalletItemInfo();
    }
}
