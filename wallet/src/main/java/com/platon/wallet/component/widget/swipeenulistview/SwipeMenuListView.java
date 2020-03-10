package com.platon.wallet.component.widget.swipeenulistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;

public class SwipeMenuListView extends ListView {

    private final static String TAG = SwipeMenuListView.class.getSimpleName();

    /**
     * 默认的滑动方向
     */
    private SwipeDirection mSwipeDirection;
    /**
     * 默认的滑动状态
     */
    private TouchState mTouchState;
    /**
     * y轴上系统认可的最小滑动距离
     */
    private int minTouchSlopY;
    /**
     * x轴上系统认可的最小滑动距离
     */
    private int minTouchSlopX;
    /**
     * 当前down事件的x
     */
    private float mDownX;
    /**
     * 当前down事件的y
     */
    private float mDownY;
    /**
     * 当前操作view的position
     */
    private int mTouchPosition;

    private SwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;
    private SwipeMenuCreator mMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuStateChangeListener mOnMenuStateChangeListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;

    enum TouchState {
        TOUCH_STATE_NONE, TOUCH_STATE_X, TOUCH_STATE_Y
    }

    public enum SwipeDirection {

        DIRECTION_LEFT(1), DIRECTION_RIGHT(-1);

        private int value;

        SwipeDirection(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public SwipeMenuListView(Context context) {
        super(context);
        init();
    }

    public SwipeMenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SwipeMenuListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mSwipeDirection = SwipeDirection.DIRECTION_LEFT;

        mTouchState = TouchState.TOUCH_STATE_NONE;

        minTouchSlopY = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        minTouchSlopX = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new SwipeMenuAdapter(getContext(), adapter) {
            @Override
            public void createMenu(SwipeMenu menu) {
                if (mMenuCreator != null) {
                    mMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(SwipeMenuView view, SwipeMenu menu,
                                    int index) {
                boolean flag = false;
                if (mOnMenuItemClickListener != null) {
                    flag = mOnMenuItemClickListener.onMenuItemClick(
                            view.getPosition(), menu, index);
                }
                if (mTouchView != null && !flag) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //在拦截处处理，在滑动设置了点击事件的地方也能swip，点击时又不能影响原来的点击事件
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                Log.e(TAG, "onInterceptTouchEvent ACTION_DOWN is run....");

                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TouchState.TOUCH_STATE_NONE;
                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                //如果摸在另外个view
                if (mTouchView != null && mTouchView.isOpen() && view != mTouchView) {
                    return true;
                }

                //只在空的时候赋值 以免每次触摸都赋值，会有多个open状态
                if (view instanceof SwipeMenuLayout) {
                    //如果有打开了 判断点击事件是否在菜单view中，不是的话就拦截
                    if (mTouchView != null && mTouchView.isOpen() && !inRangeOfView(mTouchView.getMenuView(), ev)) {
                        return true;
                    }
                    mTouchView = (SwipeMenuLayout) view;
                    mTouchView.setSwipeDirection(mSwipeDirection);
                }

                if (mTouchView != null) {
                    mTouchView.onSwipe(ev);
                }

                Log.e(TAG, "super.onInterceptTouchEvent(ev) is: " + super.onInterceptTouchEvent(ev));

                return super.onInterceptTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:

                Log.e(TAG, "onInterceptTouchEvent ACTION_MOVE is run....");

                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (Math.abs(dy) > minTouchSlopY || Math.abs(dx) > minTouchSlopX) {
                    //每次拦截的down都把触摸状态设置成了TOUCH_STATE_NONE 只有返回true才会走onTouchEvent 所以写在这里就够了
                    if (mTouchState == TouchState.TOUCH_STATE_NONE) {
                        if (dx > minTouchSlopX) {
                            Log.e(TAG, " dx > minTouchSlopX onInterceptTouchEvent ACTION_MOVE mTouchState is " + mTouchState);
                            mTouchState = TouchState.TOUCH_STATE_X;
                            if (mOnSwipeListener != null) {
                                mOnSwipeListener.onSwipeStart(mTouchPosition);
                            }
                        } else if (Math.abs(dy) > minTouchSlopY) {
                            Log.e(TAG, " Math.abs(dy) > minTouchSlopY onInterceptTouchEvent ACTION_MOVE mTouchState is " + mTouchState);
                            mTouchState = TouchState.TOUCH_STATE_Y;
                        }

                    }
                    return true;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();

        if (action != MotionEvent.ACTION_DOWN && mTouchView == null) {
            return super.onTouchEvent(ev);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                Log.e(TAG, "onTouchEvent ACTION_DOWN is run....");

                int oldPos = mTouchPosition;

                mDownX = ev.getX();
                mDownY = ev.getY();

                mTouchState = TouchState.TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null
                        && mTouchView.isOpen()) {
                    // 操作的是同一个view
                    if (mTouchView == view) {
                        mTouchState = TouchState.TOUCH_STATE_X;
                        mTouchView.onSwipe(ev);
                        return true;
                    } else {
                        //关掉当前打开的view
                        mTouchView.smoothCloseMenu();
                        if (view instanceof SwipeMenuLayout) {
                            mTouchView = (SwipeMenuLayout) view;
                            mTouchView.setSwipeDirection(mSwipeDirection);
                        }
                        //记录当前view的down事件
                        if (mTouchView != null) {
                            mTouchView.onSwipe(ev);
                        }

                        if (mOnMenuStateChangeListener != null) {
                            mOnMenuStateChangeListener.onMenuClose(oldPos);
                        }
                        return true;
                    }

                } else {
                    if (view instanceof SwipeMenuLayout) {
                        mTouchView = (SwipeMenuLayout) view;
                        mTouchView.setSwipeDirection(mSwipeDirection);
                    }
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }
                }
                break;
            //因为ACTION_MOVE会被调用多次，第一次的是后x轴和y轴的偏移量都为0，这时候去修改mTouchState是不合理的
            case MotionEvent.ACTION_MOVE:
                //有些可能有header,要减去header再判断
                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY()) - getHeaderViewsCount();
                //如果滑动了一下没完全展现，就收回去，这时候mTouchView已经赋值，再滑动另外一个不可以swip的view
                //会导致mTouchView swip 。 所以要用位置判断是否滑动的是一个view
//                if (!mTouchView.getSwipEnable() || mTouchPosition != mTouchView.getPosition()) {
//                    break;
//                }

                Log.e(TAG, "onTouchEvent ACTION_MOVE is run....");

                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));

                Log.e(TAG, "dx is " + dx);
                Log.e(TAG, "dy is " + Math.abs(dy));

                Log.e(TAG, "ACTION_MOVE mTouchState is " + mTouchState);


                if (mTouchState == TouchState.TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev);
                    }
                    getSelector().setState(new int[]{0});
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TouchState.TOUCH_STATE_NONE) {

                    if (dx > minTouchSlopX || Math.abs(dy) > minTouchSlopY) {
                        if (dx > Math.abs(dy)) {
                            mTouchState = TouchState.TOUCH_STATE_X;
                            if (mOnSwipeListener != null) {
                                mOnSwipeListener.onSwipeStart(mTouchPosition);
                            }
                            Log.e(TAG, "x > y ACTION_MOVE mTouchState 被修改为 " + mTouchState);
                        } else {
                            //这里当Math.abs(dy)>=dx时执行
                            mTouchState = TouchState.TOUCH_STATE_Y;
                            Log.e(TAG, "y > x ACTION_MOVE mTouchState 被修改为 " + mTouchState);
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:

                Log.e(TAG, "onTouchEvent ACTION_UP is run....");

                Log.e(TAG, "ACTION_UP mTouchState is " + mTouchState);

                if (mTouchState == TouchState.TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        boolean isBeforeOpen = mTouchView.isOpen();
                        Log.e(TAG, "ev is null" + (ev == null ? "true" : "false"));
                        mTouchView.onSwipe(ev);
                        boolean isAfterOpen = mTouchView.isOpen();
                        Log.e(TAG, "isBeforeOpen is " + isBeforeOpen + "    isAfterOpen is " + isAfterOpen);
                        if (isBeforeOpen != isAfterOpen && mOnMenuStateChangeListener != null) {
                            if (isAfterOpen) {
                                mOnMenuStateChangeListener.onMenuOpen(mTouchPosition);
                            } else {
                                mOnMenuStateChangeListener.onMenuClose(mTouchPosition);
                            }
                        }
                        if (!isAfterOpen) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void smoothOpenMenu(int position) {
        if (position >= getFirstVisiblePosition()
                && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof SwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (SwipeMenuLayout) view;
                mTouchView.setSwipeDirection(mSwipeDirection);
                mTouchView.smoothOpenMenu();
            }
        }
    }

    public void smoothCloseMenu() {
        if (mTouchView != null && mTouchView.isOpen()) {
            mTouchView.smoothCloseMenu();
        }
    }

    public void setMenuCreator(SwipeMenuCreator menuCreator) {
        this.mMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(
            OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public void setOnMenuStateChangeListener(OnMenuStateChangeListener onMenuStateChangeListener) {
        mOnMenuStateChangeListener = onMenuStateChangeListener;
    }

    public static interface OnMenuItemClickListener {
        boolean onMenuItemClick(int position, SwipeMenu menu, int index);
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }

    public static interface OnMenuStateChangeListener {
        void onMenuOpen(int position);

        void onMenuClose(int position);
    }

    public void setSwipeDirection(SwipeDirection direction) {
        mSwipeDirection = direction;
    }

    /**
     * 判断点击事件是否在某个view内
     *
     * @param view
     * @param ev
     * @return
     */
    public static boolean inRangeOfView(View view, MotionEvent ev) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        if (ev.getRawX() < x || ev.getRawX() > (x + view.getWidth()) || ev.getRawY() < y || ev.getRawY() > (y + view.getHeight())) {
            return false;
        }
        return true;
    }
}
