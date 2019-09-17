package com.juzix.wallet.view;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.RobolectricApp;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.Node;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;


/**
 * 验证activity
 */
//@RunWith(RobolectricGradleTestRunner.class)表示用Robolectric的TestRunner来跑这些test

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27 ,manifest=Config.NONE,application = RobolectricApp.class)
public class MyVoteActivityTest {

//    public NodeManager nodeManager;
//    public Node node;
//    public Application application;
//
//
//    @Before
//    public void setup() {
//        application = RuntimeEnvironment.application;
//
//        AppSettings appSettings = AppSettings.getInstance();
//        nodeManager = NodeManager.getInstance();
//        node = new Node.Builder().build();
//        nodeManager.setCurNode(node);
//        appSettings.init(RuntimeEnvironment.application);
//        ApiResponse.init(application);
//    }
//
//    @Test
//    public void testMyVoteActivity() {
////        Context context =RuntimeEnvironment.application;
//        MyVoteActivity myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class);
////        MyVoteActivity myVoteActivity = Robolectric.buildActivity(MyVoteActivity.class).create().get();
//        assertNotNull("MyVoteActivity not intstantitated", myVoteActivity);
//
//
////        MyVoteActivity myVoteActivity = Robolectric.setupActivity(MyVoteActivity.class); //这一步已经完成了onCreate、onStart、onResume这几个生命周期的回调了。
////        myVoteActivity.findViewById(R.id.tv_vote).performClick();
//////        Intent  expectedIntent =new Intent(myVoteActivity,SubmitVoteActivity.class);
////
////        ShadowActivity shadowActivity = shadowOf(myVoteActivity);//用来获取mainActivity对应的ShadowActivity的instance。
////
////        Intent actualIntent = shadowActivity.getNextStartedActivity();//用来获取mainActivity调用的startActivity的intent。这也是正常的Activity类里面不具有的一个接口。
////        assertEquals(SubmitVoteActivity.class.getName(), actualIntent.getComponent().getClassName());//最后，调用Assert.assertEquals来assert启动的intent是我们期望的intent。
//
////        VoteFragment voteFragment  =new VoteFragment();
////        View view  =LayoutInflater.from(App.getContext()).inflate(R.layout.fragment_vote,null);
////        view.findViewById(R.id.tv_my_vote_open).performClick();
////        Intent  expectedIntent =new Intent(App.getContext(),MyVoteActivity.class);
////        ShadowActivity shadowActivity =Shadows.shadowOf(voteFragment.getActivity());
////        Intent  actualIntent =shadowActivity.getNextStartedActivity();
////        Assert.assertEquals(expectedIntent.getComponent().getClassName(), actualIntent.getComponent().getClassName());
//
//    }
//
//    //驱动生命周期
//    @Test
//    public void testLifecycle() throws Exception {
//        // 创建Activity控制器
////        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
////        MainActivity activity = controller.get();
////        assertNull(activity.getLifecycleState());
//
//        // 调用Activity的performCreate方法
////          controller.create();
////        assertEquals("onCreate", activity.getLifecycleState());
////
////        // 调用Activity的performStart方法
////        controller.start();
////        assertEquals("onStart", activity.getLifecycleState());
////
////        // 调用Activity的performResume方法
////        controller.resume();
////        assertEquals("onResume", activity.getLifecycleState());
////
////        // 调用Activity的performPause方法
////        controller.pause();
////        assertEquals("onPause", activity.getLifecycleState());
////
////        // 调用Activity的performStop方法
////        controller.stop();
////        assertEquals("onStop", activity.getLifecycleState());
////
////        // 调用Activity的performRestart方法
////        controller.restart();
////        // 注意此处应该是onStart，因为performRestart不仅会调用restart，还会调用onStart
////        assertEquals("onStart", activity.getLifecycleState());
////
////        // 调用Activity的performDestroy方法
////        controller.destroy();
////        assertEquals("onDestroy", activity.getLifecycleState());
//    }
//
//    /**
//     * 启动Activity的时候传递Intent
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testStartActivityWithIntent() throws Exception {
//        Intent intent = new Intent();
//        intent.putExtra("extra_index", 1);
//        Activity activity = Robolectric.buildActivity(MainActivity.class).newIntent(intent).create().get();
//        assertEquals(1, activity.getIntent().getExtras().getString("extra_index"));
//    }
//
//    @Test
//    public  void testStartActivityWithIntent2() throws  Exception{
//        Intent intent =new Intent();
//        intent.putExtra("extra_index",1);
//        Activity activity = Robolectric.buildActivity(MainActivity.class,intent).create().get();
//        Bundle extras =activity.getIntent().getExtras();
//        assertNotNull(extras);
////        assertEquals("HelloWorld", extras.getString("test"));
//    }
//
//
//
//    /**
//     * onRestoreInstanceState回调中传递Bundle：
//     * <p>
//     * savedInstanceState会在onRestoreInstanceState回调中传递给Activity
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testSavedInstanceState() throws Exception {
//        Bundle savedInstanceState = new Bundle();
//        Robolectric.buildActivity(MainActivity.class).create().restoreInstanceState(savedInstanceState).get();
//        // verify something
//    }
//
//
//    @Test
//    public void testVisible() throws Exception {
//////        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
////        MainActivity activity = controller.get();
////
////        // 调用Activity的performCreate并且设置视图visible
////        controller.create().visible();
////        // 触发点击
////        activity.findViewById(R.id.tv_my_vote_open).performClick();
////
////        // 验证
////        assertEquals(shadowOf(activity).getNextStartedActivity().getComponent().getClassName(), MyVoteActivity.class.getName());
//    }
//
//
//    /**
//     * 验证Dialog是否正确弹出
//     *
//     * @throws Exception
//     */
//    @Test
//    public void testDialog() throws Exception {
////        MainActivity activity = Robolectric.setupActivity(MainActivity.class);
////        AlertDialog dialog = ShadowAlertDialog.getLatestAlertDialog();
////        // 判断Dialog尚未弹出
////        assertNull(dialog);
////
////        activity.findViewById(R.id.iv_add).performClick();
////        dialog = ShadowAlertDialog.getLatestAlertDialog();
////        // 判断Dialog已经弹出
////        assertNotNull(dialog);
////        // 获取Shadow类进行验证
////        ShadowAlertDialog shadowDialog = shadowOf(dialog);
////        assertEquals("AlertDialog", shadowDialog.getTitle());
////        assertEquals("Oops, now you see me ~", shadowDialog.getMessage());
//    }


}


