package com.platon.aton.component.adapter.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.entity.Wallet;

import java.util.ArrayList;
import java.util.List;

public class CommonSidebarItemDecoration2 extends RecyclerView.ItemDecoration {

        private Rect textBound;//文字的范围i
        private Paint mPaint;
        private List<Wallet> walletList = new ArrayList<>();
        private List<Wallet> walletListHD = new ArrayList<>();
        private int mTitleHeight;//title的高
        private int unTitleHeight;//没有标题title的高
        private final int COLOR_BG;
        private final int COLOR_FONT = Color.BLACK;
        private final int decoration;
        private Context mContext;

        public CommonSidebarItemDecoration2(Context context, List<Wallet> wallets, int decoration, List<Wallet> walletsHD) {
            this.mContext = context;
            this.COLOR_BG = context.getResources().getColor(R.color.color_f9fbff);
            this.decoration = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, decoration, context.getResources().getDisplayMetrics());
            this.walletList.clear();
            this.walletList.addAll(wallets);
            this.walletListHD.clear();
            this.walletListHD.addAll(walletsHD);
            mTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
            unTitleHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, context.getResources().getDisplayMetrics());
            //title字体大小
            final float mFontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, context.getResources().getDisplayMetrics());
            textBound = new Rect();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setTextSize(mFontSize);
        }

        public void setDataSource(List<Wallet> wallets){
            this.walletList.clear();
            this.walletList.addAll(wallets);
        }

        public void setWalletListHDDataSource(List<Wallet> walletsHD){
            this.walletListHD.clear();
            this.walletListHD.addAll(walletsHD);
        }

        /**
         * 设置Title的空间
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildLayoutPosition(view);
            if(walletList.size() > 0){

                if (position == 0) {//position为0，第一个item有title
                    if(walletList.get(0).isHD()){
                        outRect.set(0, mTitleHeight, 0, 0);//留下title空间
                    }else{
                        outRect.set(0, unTitleHeight , 0, 0);
                    }

                } else {//对是否需要留白 进行判断

                    if(walletList.size() > 0){
                        Wallet resourceBean = walletList.get(position);
                        Wallet lastBean = (position - 1) >= 0 ? walletList.get(position - 1) : null;
                        if (resourceBean == null || lastBean == null) return;

                        if(!TextUtils.isEmpty(resourceBean.getParentWalletName()) && !TextUtils.isEmpty(lastBean.getParentWalletName())){//子钱包比对
                            if (!resourceBean.getParentWalletName().equals(lastBean.getParentWalletName())) {
                                outRect.set(0, mTitleHeight, 0, 0);//留下title空间
                            } else {
                                outRect.set(0, 0, 0, 0);
                            }
                        }else if(TextUtils.isEmpty(resourceBean.getParentWalletName()) && TextUtils.isEmpty(lastBean.getParentWalletName())){//普通钱包比对

                        }else{//普通钱包与子钱包比对
                            outRect.set(0, mTitleHeight, 0, 0);//留下title空间
                        }
                    }
                }
            }
        }

        /**
         * 绘制Title
         */
        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            final int left = parent.getPaddingLeft() + (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, mContext.getResources().getDisplayMetrics());
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int current = parent.getChildCount();//屏幕当前item数量

            if(walletList.size() > 0){
                for (int i = 0; i < current; i++) {
                    final View child = parent.getChildAt(i);//获取childView
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int position = parent.getChildLayoutPosition(child);//child的adapter position




                     if (position == 0) {//为0  绘制title
                        Wallet resourceBean = walletList.get(position);
                        if (resourceBean == null) return;
                        String title = resourceBean.getParentWalletName();
                        String newTitle = TextUtils.isEmpty(title) ? "" : title;
                        if(resourceBean.isHD()){
                            drawTitle(c, left, right, child, layoutParams, newTitle, getSubWalletBGColor(walletListHD,resourceBean));//进行绘制
                        }else{
                            drawTitle(c, left, right, child, layoutParams, newTitle, R.color.color_f9fbff);//进行绘制
                        }

                    } else {//不为0时 判断 是否需要绘制title
                        Wallet resourceBean = walletList.get(position);
                        Wallet lastBean = walletList.get(position - 1);
                        if (resourceBean == null || lastBean == null) return;

                         if(!TextUtils.isEmpty(resourceBean.getParentWalletName()) && !TextUtils.isEmpty(lastBean.getParentWalletName())){//子钱包比对
                             if (!resourceBean.getParentWalletName().equals(lastBean.getParentWalletName())) {
                                 drawTitle(c, left, right, child, layoutParams, resourceBean.getParentWalletName(),getSubWalletBGColor(walletListHD,resourceBean));
                             }
                         }else if(TextUtils.isEmpty(resourceBean.getParentWalletName()) && TextUtils.isEmpty(lastBean.getParentWalletName())){//普通钱包比对

                         }else{//普通钱包与子钱包比对
                             drawTitle(c, left, right, child, layoutParams, resourceBean.getParentWalletName(),getSubWalletBGColor(walletListHD,resourceBean));
                         }
                    }

                }
            }
        }


    /**
     * 获取子钱包标题背景色
     * @param walletListHD
     * @param wallet
     * @return
     */
        private int getSubWalletBGColor(List<Wallet> walletListHD, Wallet wallet){
            int walletIndex = 0;
            for (int i = 0; i < walletListHD.size(); i++) {
                Wallet walletHD = walletListHD.get(i);
                if(walletHD.getUuid().equals(wallet.getParentId())){
                    //由于数据源已经进行了排序，所以遍历时，同一组子钱包的walletIndex是相同的
                    walletIndex = i;
                    break;
                }
            }

            if(walletIndex % 2 == 0){
                return R.color.color_eff4fd;
            }else{
                return R.color.color_f9fbff;
            }
        }

        /**
         * 绘制文字
         */
        private void drawTitle(Canvas c, int left, int right, View child, RecyclerView.LayoutParams layoutParams, String titleText, int colorBg) {
            //绘制背景色
            mPaint.setColor(mContext.getResources().getColor(colorBg));
            int value = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 2, mContext.getResources().getDisplayMetrics());
            c.drawRect(0, child.getTop() - layoutParams.topMargin - mTitleHeight, right, (child.getTop() - layoutParams.topMargin), mPaint);
            //绘制文字，没有使用计算baseline的方式
            mPaint.setColor(COLOR_FONT);
            mPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
            mPaint.getTextBounds(titleText, 0, (TextUtils.isEmpty(titleText) ? 0 : titleText.length()), textBound);
            //c.drawText(titleText, child.getPaddingLeft(), child.getTop() - layoutParams.topMargin - (mTitleHeight / 2 - textBound.height() / 2), mPaint);
            c.drawText(titleText, child.getPaddingLeft(), child.getTop() - layoutParams.topMargin - (mTitleHeight / 2 - textBound.height() / 2) - value, mPaint);
        }

        /**
         * 绘制蒙层
         */
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
           /* final LinearLayoutManager manager = (LinearLayoutManager) parent.getLayoutManager();
            final int firstPosition = manager.findFirstVisibleItemPosition();
            final Wallet resourceBean = walletList.get(firstPosition);
            if (resourceBean == null) return;
            final String subject = walletList.get(firstPosition).getParentWalletName();
            final View child = parent.findViewHolderForAdapterPosition(firstPosition).itemView;
            final Wallet secondBean = walletList.get(firstPosition + 1);
            boolean flag = false;
            if (!subject.equals(secondBean.getParentWalletName())) {
                if (child.getHeight() + child.getTop() < mTitleHeight) {//两个tile开始接触，第一item在屏幕剩余的高度小于title高度
                    c.save();
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHeight+decoration);//将画布向上平移
                    flag = true;
                }
            }
            mPaint.setColor(COLOR_BG);
            c.drawRect(0, parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(), parent.getPaddingTop() + mTitleHeight, mPaint);
            mPaint.setColor(COLOR_FONT);
            mPaint.getTextBounds(subject, 0, subject.length(), textBound);
            c.drawText(subject, child.getPaddingLeft(), mTitleHeight - (mTitleHeight / 2 - textBound.height() / 2), mPaint);
            if (flag)c.restore();*/
        }
    }
