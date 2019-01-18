package com.juzhen.framework.app.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.juzhen.framework.app.log.Log;

/**
 * @author ziv
 */
public class CoreFragment extends Fragment {

    private static final String TAG = CoreFragment.class.getSimpleName();

    protected boolean mResumed;
    protected boolean mCreate;
    protected boolean mStopped;
    protected boolean mDestroyed;
    protected boolean mHidden;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestroyed = false;
        mCreate = true;
        mHidden = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onCreate, Hidden = " + mHidden);
    }

    @Override
    public void onStart() {
        super.onStart();
        mStopped = false;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onStart, Hidden = " + mHidden);
    }

    /**
     * 此方法会在通过add/show方式使用fragment中回调
     *
     * @param hidden
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHidden = hidden;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onHiddenChanged " + hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onResume, Hidden = " + mHidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onPause, Hidden = " + mHidden);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHidden = true;
        mStopped = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onStop, Hidden = " + mHidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        mHidden = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onDestroy, Hidden = " + mHidden);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onDetach");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onActivityCreated");
    }

    /**
     * 此方法只会在viewpager+fragment中回掉
     *
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: setUserVisibleHint " + isVisibleToUser);
    }


}
