package com.juzix.wallet.myvote;


import android.app.Application;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.R;
import com.juzix.wallet.RobolectricApp;
import com.juzix.wallet.component.adapter.BatchVoteTransactionAdapter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.Node;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import org.robolectric.shadows.ShadowListView;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.util.Scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, application = RobolectricApp.class)
public class MyVoteActivityTest {
//    @Mock
//    private MyVoteActivity myVoteActivity;
//    private ListView listView;
//
////    @Rule
////    public MockitoRule mockitoRule = MockitoJUnit.rule();
////
////    @Rule
////    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();
//
//    @Mock
//    public NodeManager nodeManager;
//    @Mock
//    public Node node;
//
//    public int mDefaultStatusBarColor = R.color.color_ffffff;
//    public Application application;
//
//    public MyVotePresenter myVotePresenter;
////    @Mock
////    VotedCandidate candidate;
//
//    @Before
//    public void setup() throws Exception {
//        application = RuntimeEnvironment.application;
//
//        AppSettings appSettings = AppSettings.getInstance();
//        nodeManager = NodeManager.getInstance();
//        node = new Node.Builder().build();
//        nodeManager.setCurNode(node);
//        appSettings.init(RuntimeEnvironment.application);
//        ApiResponse.init(application);
//        myVotePresenter =new MyVotePresenter();
////        candidate =new VotedCandidate();
//
//
//        //加上这么一句话无论是测试代码中的log或者被测试中的log都会在控制台输出的
//        ShadowLog.stream = System.out;
//    }
//
//
//    @Test
//    public void shouldFindListView() throws Exception {
//        //0x7f0c002a  0x7f0600ab
////        myVotePresenter.loadMyVoteData();
//
//        myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class);
//        assertNotNull("MyVoteActivity not intstantitated", myVoteActivity);
//
//        listView = myVoteActivity.findViewById(R.id.list_vote_info);
//
//        //断言传入的对象是不为空
//        assertNotNull(listView);
//
//        ListAdapter adapter = listView.getAdapter();
//        assertNotNull("adapter is not null",adapter);
//        BatchVoteTransactionAdapter adapter1 = (BatchVoteTransactionAdapter) listView.getAdapter();
//        assertNotNull(adapter1);
//
//
//        //验证Adapter类型
//        Assert.assertTrue(adapter instanceof BatchVoteTransactionAdapter);
//
//        ShadowListView shadowListView = shadowOf(listView); //we need to shadow the list view'
//        shadowListView.populateItems();//填充适配器
//
//        int count =adapter1.getCount();
//
//
//
//        //验证adapter的数量
//        assertEquals(0, ((BatchVoteTransactionAdapter) adapter).getCount());
////        Log.d("number",((BatchVoteTransactionAdapter) adapter).getList().size() +"");
//
////        VotedCandidate candidate =adapter1.getItem(0);
////        //验证listview中某一条数据
////        assertNotNull(candidate);
//
//
//
//
//
////        ShadowListView shadowListView = Shadows.shadowOf(listView); //we need to shadow the list view'
////        shadowListView.populateItems();//填充适配器
//
////        ShadowLog.d("Checking the first country name in adapter ", ((VotedCandidate) listView.getAdapter().getItem(0)).getCountryName(RuntimeEnvironment.application));
//
//        //断言条件为真
////        assertTrue("Country Japan doesnt exist", "Japan".equals(((VotedCandidate) listView.getAdapter().getItem(0)).getCountryName(application)));
////        assertTrue(3 == listView.getChildCount());
//
//    }
//
//    /**
//     * 验证listview中item的点击事件
//     * @throws Exception
//     */
//    @Test
//    public void testListView() throws  Exception{
//        myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class);
//        assertNotNull("MyVoteActivity not intstantitated", myVoteActivity);
//
//        listView = myVoteActivity.findViewById(R.id.list_vote_info);
//        assertNotNull(listView);
//
//
////        listView.performItemClick(listView.getAdapter().getView(0,null,null),0,0);
//
//
//
//        /**
//         *  todo 猜测：因为没有数据，所以目前测试点击跳转不能成功
//         */
//
//        //验证item中某个view的点击事件
////        View view =listView.getChildAt(0);
////        listView.getChildAt(0).findViewById(R.id.tv_vote).performClick();
////        Intent expectedIntent = new Intent(myVoteActivity,SubmitVoteActivity.class);
////        Intent actual = shadowOf(myVoteActivity).getNextStartedActivity();
////        assertEquals(expectedIntent.getComponent(), actual.getComponent());
//
//        //点击item跳转
////        listView.getChildAt(0).performClick();
////        Intent expectedIntentItem = new Intent(myVoteActivity,VoteDetailActivity.class);
////        Intent actualItem = shadowOf(myVoteActivity).getNextStartedActivity();
////        assertEquals(expectedIntentItem.getComponent(), actualItem.getComponent());
//
//    }
//
//
//    /**
//     * 网络请求验证
//     */
////    @Test
////    public  void testGetHttpData()throws Exception{
////        MyVoteActivity myVoteActivity= Robolectric.setupActivity(MyVoteActivity.class);
////        //等待接口请求完毕 再往下执行
////        waitAfterAsyncRequest();
////
////        ListView listView = myVoteActivity.findViewById(R.id.list_vote_info);
////        BatchVoteTransactionAdapter adapter1 = (BatchVoteTransactionAdapter) listView.getAdapter();
////        assertNotNull(adapter1);
////        Assert.assertEquals("请求成功",adapter1.getList().toString());
//////        Assert.assertEquals("请求成功", resultTextView.getText().toString());
////
////    }
////
//    private void waitAfterAsyncRequest() {
//        //获取主线程的消息队列的调度者，通过它可以知道消息队列的情况并驱动主线程主动轮询消息队列
//        Scheduler scheduler = Robolectric.getForegroundThreadScheduler();
//        //因为网络请求是在异步线程中进行, 过一段时间请求完毕才会通知主线程
//        //所以在这里进行等待，直到消息队列里存在消息
//        while (!scheduler.areAnyRunnable()) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        //轮询消息队列，这样就会在主线程进行通知
//        scheduler.runOneTask();
//
//    }
//
//    @Test
//    public  void testgetDataByHttp()throws Exception{
//        MyVoteActivity myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class);
//        MyVotePresenter myVotePresenter =new MyVotePresenter(myVoteActivity);
//        assertNotNull(myVotePresenter);
//        myVotePresenter.loadMyVoteData();
//        //等待接口请求完毕 再往下执行
//        waitAfterAsyncRequest();
//        ListView listView = myVoteActivity.findViewById(R.id.list_vote_info);
//        BatchVoteTransactionAdapter adapter1 = (BatchVoteTransactionAdapter) listView.getAdapter();
//        assertNotNull(adapter1);
//        Assert.assertEquals("请求成功",adapter1.getList().toString()); //todo  暂时没数据，所以这行测试不成功
////        Assert.assertEquals("请求成功", resultTextView.getText().toString());
//    }
//
//
//    @Test
//    public void testGetDataByMock()throws Exception{
//        String[] addressList = {"0xfeee1657553f08fb1d12f35d492b1b8f5aa2fa4e", "0x493301712671ada506ba6ca7891f436d29185821", "0x19a74462197bf1bebbab51c246a948ffb7791b5d"};
//        MyVoteActivity myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class);
//        MyVotePresenter myVotePresenter =new MyVotePresenter(myVoteActivity);
//        myVotePresenter.getBatchVoteTransaction(addressList);
//        waitAfterAsyncRequest();
//        ListView listView = myVoteActivity.findViewById(R.id.list_vote_info);
//        BatchVoteTransactionAdapter adapter1 = (BatchVoteTransactionAdapter) listView.getAdapter();
//        assertNotNull(adapter1);
//        Log.debug("data====================",adapter1.getList().size()+"====");
//
//
//        ShadowListView shadowListView = shadowOf(listView); //we need to shadow the list view'
//        shadowListView.populateItems();//填充适配器
//        listView.getChildAt(0).performClick();
//    }



}
