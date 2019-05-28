package com.juzix.wallet.component.ui.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxRadioGroup;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.component.adapter.VoteListAdapter;
import com.juzix.wallet.component.ui.SortType;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.MVPBaseFragment;
import com.juzix.wallet.component.ui.contract.VoteContract;
import com.juzix.wallet.component.ui.presenter.VotePresenter;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DensityUtil;
import com.juzix.wallet.utils.RxUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author matrixelement
 */
public class VoteFragment extends MVPBaseFragment<VotePresenter> implements VoteContract.View {

    private final static String TAG_DEFAULT = "tag_default";
    private final static String TAG_REWARD = "tag_reward";
    private final static String TAG_REGION = "tag_region";

    @BindView(R.id.layout_fresh)
    SmartRefreshLayout smartRefreshLayout;
    @BindView(R.id.tv_vote_rate)
    TextView tvVoteRate;
    @BindView(R.id.pb_vote_rate)
    ProgressBar pbVoteRate;
    @BindView(R.id.tv_voted)
    TextView tvVoted;
    @BindView(R.id.tv_vote_price)
    TextView tvVotePrice;
    @BindView(R.id.cl_vote_info)
    ConstraintLayout clVoteInfo;
    @BindView(R.id.rb_default)
    RadioButton rbDefault;
    @BindView(R.id.rb_reward)
    RadioButton rbReward;
    @BindView(R.id.rb_location)
    RadioButton rbLocation;
    @BindView(R.id.rg_select_condition)
    RadioGroup rgSelectCondition;
    @BindView(R.id.iv_search_vote)
    ImageView ivSearchVote;
    @BindView(R.id.et_search_vote)
    EditText etSearchVote;
    @BindView(R.id.iv_clear)
    ImageView ivClear;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.layout_toolbar_open)
    RelativeLayout layoutToolbarOpen;
    @BindView(R.id.layout_toolbar_close)
    RelativeLayout layoutToolbarClose;
    @BindView(R.id.bg_toolbar_open)
    View bgToolbarOpen;
    @BindView(R.id.bg_toolbar_close)
    View bgToolbarClose;
    @BindView(R.id.bg_tabbar_open)
    View bgTabbarOpen;
    @BindView(R.id.bg_tabbar_close)
    View bgTabbarClose;
    @BindView(R.id.tv_vote)
    TextView tvVote;
    @BindView(R.id.tv_my_vote_open)
    TextView tvMyVoteOpen;
    @BindView(R.id.tv_my_vote_close)
    TextView tvMyVoteClose;
    @BindView(R.id.layout_tabbar_open)
    FrameLayout layoutTabbarOpen;
    @BindView(R.id.layout_tabbar_close)
    FrameLayout layoutTabbarClose;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.layout_search)
    LinearLayout layoutSearch;

    private Unbinder unbinder;
    private VoteListAdapter mVoteListAdapter;
    private int mSearchLayoutWidth;
    private boolean mSearchEditOpened;

    @Override
    protected VotePresenter createPresenter() {
        return new VotePresenter(this);
    }

    @Override
    protected void onFragmentPageStart() {
        mPresenter.getCandidateList();
    }

    @Override
    protected View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(container.getContext()).inflate(R.layout.fragment_vote, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        initViews();
        return rootView;
    }

    private void initViews() {

        mSearchLayoutWidth = DensityUtil.getScreenWidth(getContext()) - DensityUtil.dp2px(getContext(), 16f) * 2;

        tabLayout.addTab(tabLayout.newTab().setText(string(R.string.action_default)).setTag(TAG_DEFAULT));
        tabLayout.addTab(tabLayout.newTab().setText(string(R.string.reward)).setTag(TAG_REWARD));
        tabLayout.addTab(tabLayout.newTab().setText(string(R.string.location)).setTag(TAG_REGION));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.divider_vote_list));
        recyclerView.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mVoteListAdapter = new VoteListAdapter();
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mVoteListAdapter);
        recyclerView.setEnabled(false);
        recyclerView.setBackgroundResource(R.color.color_ffffff);

        mVoteListAdapter.setOnItemClickListener(new VoteListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Candidate candidateEntity) {
                NodeDetailActivity.actionStart(currentActivity(), candidateEntity);
            }
        });

        mVoteListAdapter.setOnVoteTicketClickListener(new VoteListAdapter.OnVoteTicketClickListener() {
            @Override
            public void onVoteTicketClick(Candidate candidateEntity) {
                mPresenter.voteTicket(candidateEntity);
            }
        });

        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //垂直方向偏移量
                int offset = Math.abs(verticalOffset);
                //最大偏移距离
                int scrollRange = appBarLayout.getTotalScrollRange();
                if (offset <= scrollRange / 2) {//当滑动没超过一半，展开状态下toolbar显示内容，根据收缩位置，改变透明值
                    layoutToolbarOpen.setVisibility(View.VISIBLE);
                    layoutToolbarClose.setVisibility(View.GONE);
                    layoutTabbarOpen.setVisibility(View.VISIBLE);
                    layoutTabbarClose.setVisibility(View.GONE);
                    //根据偏移百分比 计算透明值
                    float scale2 = (float) offset / (scrollRange / 2);
                    int alpha2 = (int) (255 * scale2);
                    bgToolbarOpen.setAlpha(alpha2);
                    bgTabbarOpen.setAlpha(alpha2);
                    recyclerView.setBackgroundResource(R.color.color_ffffff);
                    mVoteListAdapter.setVoteListSpread(false);
                } else {//当滑动超过一半，收缩状态下toolbar显示内容，根据收缩位置，改变透明值
                    layoutToolbarClose.setVisibility(View.VISIBLE);
                    layoutToolbarOpen.setVisibility(View.GONE);
                    layoutTabbarOpen.setVisibility(View.GONE);
                    layoutTabbarClose.setVisibility(View.VISIBLE);
                    float scale3 = (float) (scrollRange - offset) / (scrollRange / 2);
                    int alpha3 = (int) (255 * scale3);
                    bgToolbarClose.setAlpha(alpha3);
                    bgTabbarClose.setAlpha(alpha3);
                    recyclerView.setBackgroundResource(R.color.color_f9fbff);
                    mVoteListAdapter.setVoteListSpread(true);
                }
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateRadioButton(getCheckedIdByTabTag((String) tab.getTag()));
                updateCandidateListByTabTag((String) tab.getTag());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //添加未选中Tab的逻辑
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //再次选中tab的逻辑

            }
        });

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getCandidateList();
            }
        });

        RxRadioGroup
                .checkedChanges(rgSelectCondition)
                .skipInitialValue()
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Integer>() {
                    @Override
                    public void accept(Integer checkedId) {
                        tabLayout.getTabAt(getTabIndexByCheckedId(checkedId)).select();
                    }
                });

        RxView.clicks(ivClear)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mSearchEditOpened = false;
                        etSearchVote.setText("");
                        ((BaseActivity) getActivity()).hideSoftInput();
                        toggleSearchEditText(mSearchEditOpened);
                    }
                });

        RxView.clicks(tvMyVoteOpen)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        MyVoteActivity.actionStart(currentActivity());
                    }
                });

        RxView.clicks(tvMyVoteClose)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        MyVoteActivity.actionStart(currentActivity());
                    }
                });

        RxView.clicks(ivSearchVote)
                .compose(RxUtils.getClickTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .subscribe(new CustomObserver<Object>() {
                    @Override
                    public void accept(Object o) {
                        mSearchEditOpened = !mSearchEditOpened;
                        toggleSearchEditText(mSearchEditOpened);
                    }
                });

        RxTextView.textChanges(etSearchVote)
                .skipInitialValue()
                .compose(RxUtils.getSearchTransformer())
                .compose(RxUtils.bindToLifecycle(this))
                .compose(bindToLifecycle())
                .subscribe(new CustomObserver<CharSequence>() {
                    @Override
                    public void accept(CharSequence charSequence) {
                        mPresenter.search(charSequence.toString().trim());
                    }
                });
    }

    @Override
    public void setVotedInfo(long sumVoteNum, long votedNum, String ticketPrice) {
        pbVoteRate.setMax(NumberParserUtils.parseInt(sumVoteNum));
        pbVoteRate.setProgress(NumberParserUtils.parseInt(votedNum));

        String voteRate = String.format("%s%%", NumberParserUtils.getPrettyNumber(BigDecimalUtil.mul(BigDecimalUtil.div(votedNum, sumVoteNum), 100D), 2));

        tvVoteRate.setText(string(R.string.vote_rate_with_colon_and_value, voteRate));
        tvVoted.setText(string(R.string.voted_num_with_colon, votedNum));
        tvVotePrice.setText(string(R.string.vote_price_with_colon, NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(NumberParserUtils.parseDouble(ticketPrice), 1E18), 4)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventPublisher.getInstance().unRegister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void notifyDataSetChanged(List<Candidate> candidateList) {
        mVoteListAdapter.notifyDataChanged(candidateList);
    }

    @Override
    public void finishRefresh() {
        smartRefreshLayout.finishRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        mPresenter.clearCandidateList();
    }

    private int getCheckedIdByTabTag(String tag) {
        switch (tag) {
            case TAG_DEFAULT:
                return R.id.rb_default;
            case TAG_REWARD:
                return R.id.rb_reward;
            case TAG_REGION:
                return R.id.rb_location;
            default:
                return 0;
        }
    }


    private void updateCandidateListByTabTag(String tag) {
        switch (tag) {
            case TAG_DEFAULT:
                mPresenter.sort(SortType.SORTED_BY_DEFAULT);
                break;
            case TAG_REWARD:
                mPresenter.sort(SortType.SORTED_BY_REWARD);
                break;
            case TAG_REGION:
                mPresenter.sort(SortType.SORTED_BY_REGION);
                break;
            default:
                break;
        }
    }

    private void updateCandidateListByCheckedId(Integer checkedId) {
        switch (checkedId) {
            case R.id.rb_default:
                mPresenter.sort(SortType.SORTED_BY_DEFAULT);
                break;
            case R.id.rb_reward:
                mPresenter.sort(SortType.SORTED_BY_REWARD);
                break;
            case R.id.rb_location:
                mPresenter.sort(SortType.SORTED_BY_REGION);
                break;
            default:
                break;
        }
    }

    private static Animator createOpenAnimation(final View targetView, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = targetView.getLayoutParams();
                layoutParams.width = value;
                targetView.setLayoutParams(layoutParams);
            }
        });
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(targetView, View.ALPHA, 0.0f, 1.0f);
        set.playTogether(animator, alphaAnimator);
        set.setDuration(300);
        return animator;
    }

    private void toggleSearchEditText(boolean searchEditTextOpened) {
        etSearchVote.setVisibility(searchEditTextOpened ? View.VISIBLE : View.GONE);
        ivSearchVote.setVisibility(searchEditTextOpened ? View.GONE : View.VISIBLE);
        rgSelectCondition.setVisibility(searchEditTextOpened ? View.GONE : View.VISIBLE);
        layoutSearch.setVisibility(searchEditTextOpened ? View.VISIBLE : View.GONE);
        if (searchEditTextOpened) {
            createOpenAnimation(layoutSearch, 0, mSearchLayoutWidth).start();
        }
    }

    private void updateRadioButton(int checkedId) {
        switch (checkedId) {
            case R.id.rb_default:
                rbDefault.setTextColor(ContextCompat.getColor(getContext(), R.color.color_105cfe));
                rbDefault.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                rbReward.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbReward.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbLocation.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbDefault.setChecked(true);
                rbReward.setChecked(false);
                rbLocation.setChecked(false);

                break;
            case R.id.rb_reward:
                rbDefault.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbDefault.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbReward.setTextColor(ContextCompat.getColor(getContext(), R.color.color_105cfe));
                rbReward.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                rbLocation.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbDefault.setChecked(false);
                rbReward.setChecked(true);
                rbLocation.setChecked(false);
                break;
            case R.id.rb_location:
                rbDefault.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbDefault.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbReward.setTextColor(ContextCompat.getColor(getContext(), R.color.color_000000));
                rbReward.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                rbLocation.setTextColor(ContextCompat.getColor(getContext(), R.color.color_105cfe));
                rbLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                rbDefault.setChecked(false);
                rbReward.setChecked(false);
                rbLocation.setChecked(true);
                break;
            default:
                break;

        }
    }

    private int getTabIndexByCheckedId(int checkedId) {
        switch (checkedId) {
            case R.id.rb_default:
                return 0;
            case R.id.rb_reward:
                return 1;
            case R.id.rb_location:
                return 2;
            default:
                break;
        }
        return 0;
    }

}
