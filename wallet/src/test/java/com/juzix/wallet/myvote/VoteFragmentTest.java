package com.juzix.wallet.myvote;

import android.app.Application;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.R;
import com.juzix.wallet.RobolectricApp;
import com.juzix.wallet.component.adapter.VoteListAdapter;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.entity.Node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config( sdk = 27,application = RobolectricApp.class)
public class VoteFragmentTest {
//    private VoteFragment voteFragment;
//    @Mock
//    private MainActivity mainActivity;
//    private RecyclerView recyclerView;
//
//
//    @Mock
//    public NodeManager nodeManager;
//    @Mock
//    public Node node;
//
//    public Application application;
//    @Before
//    public void setup() throws Exception {
//        application = RuntimeEnvironment.application;
//        AppSettings appSettings = AppSettings.getInstance();
//        nodeManager = NodeManager.getInstance();
//        node = new Node.Builder().build();
//        nodeManager.setCurNode(node);
//        appSettings.init(RuntimeEnvironment.application);
//        ApiResponse.init(application);
//        mainActivity =Robolectric.setupActivity(MainActivity.class);
//
//
////        voteFragment = new VoteFragment();
////        //把fragment添加到Activity中
////        //此api可以主动添加Fragment到Activity中，因此会触发Fragment的onCreateView()
//////        SupportFragmentTestUtil.startFragment(voteFragment);
////
////        voteFragment.onCreateView(LayoutInflater.from(mainActivity), (ViewGroup) mainActivity.findViewById(R.id.realTabContent), null);
////        voteFragment.onAttach(mainActivity);
////        voteFragment.onActivityCreated(null);
////
////        View view = LayoutInflater.from(mainActivity).inflate(R.layout.fragment_vote, null);
//////        recyclerView =view.findViewById()
//
//    }
//
//
////    @Test
////    public void addFragment(MainActivity activity, int fragmentContent) {
//////        SupportFragmentTestUtil.startFragment(activity.getSupportFragmentManager().findFragmentById(fragmentContent));
////        Fragment fragment = activity.getSupportFragmentManager().findFragmentById(fragmentContent);
////        assertNotNull(fragment);
////    }
//
//    /**
//     * 验证fragment
//     */
//    @Test
//    public void testFragment() throws Exception {
//        //通过activity对象获取Fragment
//        //获取Fragment
////        List<Fragment> fragmentList = mainActivity.getSupportFragmentManager().getFragments();
////        VoteFragment mFragment = null;
////        if (fragmentList.get(1) instanceof VoteFragment) {
////            mFragment = (VoteFragment) fragmentList.get(1);
////        }
////
////        assertNotNull(mFragment);
////        assertNotNull(voteFragment.getView());
//        VoteFragment voteFragment =new VoteFragment();
//        SupportFragmentTestUtil.startVisibleFragment(voteFragment);
////        assertThat(voteFragment.getView()).isNotNull();
//        assertNotNull(voteFragment.getView());
//        //验证fragment中点击跳转
//        voteFragment.getView().findViewById(R.id.tv_my_vote_open).performClick();
//        Intent expectedIntent = new Intent(voteFragment.getContext(),MyVoteActivity.class);
////        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
//        Intent actual = shadowOf(voteFragment.getActivity().getApplication()).getNextStartedActivity();
//        assertEquals(expectedIntent.getComponent().getClassName(),actual.getComponent().getClassName());
//
//    }
//
//    /**
//     * 验证fragment中item点击响应
//     * @throws Exception
//     */
//    @Test
//    public  void testRecyclerViewItemClick() throws  Exception{
//        VoteFragment voteFragment =new VoteFragment();
//        SupportFragmentTestUtil.startVisibleFragment(voteFragment);
////        assertThat(voteFragment.getView()).isNotNull();
//        assertNotNull(voteFragment.getView());
//        RecyclerView recyclerView =voteFragment.getView().findViewById(R.id.recycler_view);
//       //验证RecyclerView不为空
//         assertNotNull(recyclerView);
//        RecyclerView.Adapter adapter = recyclerView.getAdapter();
//        //验证Adapter不为空
//        assertNotNull(adapter);
//        //验证Adapter类型
//        assertTrue(adapter instanceof VoteListAdapter);
//
//        //验证Adapter数据量
////        assertEquals(20, ((VoteListAdapter) adapter).getItemCount());
//
////        Candidate item = ((VoteListAdapter) adapter).getItem();
////        //验证Adapter某一条数据
////        assertNotNull(item);
//        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//        //验证recyclerview点击某一个item的响应
//        layoutManager.findViewByPosition(0).performClick();
//        assertEquals("xxx",ShadowToast.getTextOfLatestToast());
//
//    }
//
//
//    //验证跳转
//    @Test
//    public void testOnClick() throws Exception {
////        Button nextButton = (Button) sampleActivity.findViewById(R.id.main_button);
////        nextButton.performClick(); //按钮点击后跳转到下一个Activity
////        Intent expectedIntent = new Intent(sampleActivity, LoginActivity.class);
////        Intent actualIntent = ShadowApplication.getInstance().getNextStartedActivity();
////        assertEquals(expectedIntent, actualIntent);
//
//        //========================================================
//
//
////          //创建Activity
////        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
////        Assert.assertNotNull(mainActivity);
////
////        //模拟点击
////        mainActivity.findViewById(R.id.btn_login).performClick();
////
////        Intent expectedIntent = new Intent(mainActivity, LoginActivity.class);
////        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
////        //验证是否启动了期望的Activity
////        Assert.assertEquals(expectedIntent.getComponent(), actual.getComponent());
//
//
//    }
//
//    //验证intent传值
//    @Test
//    public void testIntent() throws Exception {
//
////        Intent intent = new Intent();
////        intent.putExtra("test", "HelloWorld");
////        Activity activity = Robolectric.buildActivity(MainActivity.class, intent).create().get();
////        Bundle extras = activity.getIntent().getExtras();
////        assertNotNull(extras);
////        assertEquals("HelloWorld", extras.getString("test"));
//
//    }
//
//    //验证listview/recyclerView 点击
////    @Test
////    public void testRecyclerViewClick() throws Exception {
//////        RecyclerView recyclerView = (RecyclerView) housesLayout.findViewById(R.id.housesView);
////        //需要添加下面这两句代码，RecyclerView才会加载布局
////        recyclerView.measure(0, 0);
////        recyclerView.layout(0, 0, 100, 10000);
////        //指向Item点击事件
////        recyclerView.findViewHolderForAdapterPosition(0).itemView.performClick();
////
////
////    }
//
//
//
//
//    /**
//     * 验证访问资源文件
//     *
//     * @throws Exception
//     */
////    @Test
////    public void testResource() throws Exception {
////        Application application = RuntimeEnvironment.application;
////        String appName = application.getString(R.string.app_name);
////        Assert.assertEquals("ATON", appName);
////
////    }


}


