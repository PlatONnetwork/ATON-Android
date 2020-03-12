package com.platon.aton.rxjavatest;


//@RunWith(RobolectricTestRunner.class)
//@Config(constants = BuildConfig.class, buildDir = "wallet/build", sdk = 27)

public class RxjavaTest {

//    /**
//     * just
//     */
//    @Test
//    public void testObserver() {
//
//        TestObserver<Integer> testObserver = TestObserver.create();
//        testObserver.onNext(1);
//        testObserver.onNext(2);
//        //断言值是否相等
//        testObserver.assertValues(1, 2);
//
//        testObserver.onComplete();
//        //断言是否完成
//        testObserver.assertComplete();
//    }
//
//
//    /**
//     * from
//     */
//    @Test
//    public void testFrom() {
//
//        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
//        //依次发射list中的数字
//        Flowable.fromIterable(Arrays.asList(1, 2)).subscribe(testSubscriber);
//
//        testSubscriber.assertValues(1, 2);
//        testSubscriber.assertValueCount(2);
//        testSubscriber.assertTerminated();
//    }
//
//    /**
//     * range
//     */
//
//    @Test
//    public void testRange() {
//
//        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
//        //从3开始发射3个连续的int
//        Flowable.range(3, 3).subscribe(testSubscriber);
//
//        testSubscriber.assertValues(3, 4, 5);
//        testSubscriber.assertValueCount(3);
//        testSubscriber.assertTerminated();
//    }
//
//
//    /**
//     * repeat
//     */
//    @Test
//    public void testRepeat() {
//
//        TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();
//        Flowable.fromIterable(Arrays.asList(1, 2))
//                .repeat(2) //重复发送2次
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertValues(1, 2, 1, 2);
//        testSubscriber.assertValueCount(4);
//        testSubscriber.assertTerminated();
//    }
//
//
//    /**
//     * buffer
//     */
//    @Test
//    public void testBuffer() {
//
//        TestSubscriber<List<String>> testSubscriber = new TestSubscriber<>();
//        //缓冲2个发射一次
//        Flowable.just("A", "B", "C", "D")
//                .buffer(2)
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertResult(Arrays.asList("A", "B"), Arrays.asList("C", "D"));
//        testSubscriber.assertValueCount(2);
//        testSubscriber.assertTerminated();
//    }
//
//    /**
//     * error
//     */
//    @Test
//    public void testError() {
//        TestSubscriber testSubscriber = new TestSubscriber();
//        Exception exception = new RuntimeException("error");
//
//        Flowable.error(exception).subscribe(testSubscriber);
//        //断言错误是否一致
//        testSubscriber.assertError(exception);
//        //断言错误信息是否一致
//        testSubscriber.assertErrorMessage("error");
//    }
//
//    /**
//     * interval
//     * 在测试有关时间的操作符时，可能我们的事件还没有执行完，因此无法得到预期的输出结果，
//     * 断言就无效了。当然我们可以使用上一篇说到的将异步转为同步思路，如下：
//     */
//
//    @Rule
//    public RxJavaRule2 rule = new RxJavaRule2();
//
//    @Test
//    public void testInterval() {
//
//        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
//        //隔1秒发射一次，一共10次
//        Flowable.interval(1, TimeUnit.SECONDS)
//                .take(10)
//                .subscribe(testSubscriber);
//
//        testSubscriber.assertValueCount(10);
//        testSubscriber.assertTerminated();
//    }
//
//
//    /**
//     * RxJava 提供了 TestScheduler，通过这个调度器可以实现对时间的操控。那么我们的测试代码就变成了：
//     * 我们不仅不需要等待，还可以操控时间，可以到达任意的时间节点。
//     */
//    @Test
//    public void testInterval2() {
//        TestScheduler mTestScheduler = new TestScheduler();
//        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
//        //隔1秒发射一次，一共10次
//        Flowable.interval(1, TimeUnit.SECONDS, mTestScheduler)
//                .take(10)
//                .subscribe(testSubscriber);
//
//        //时间经过3秒
//        mTestScheduler.advanceTimeBy(3, TimeUnit.SECONDS);
//        testSubscriber.assertValues(0L, 1L, 2L);
//        testSubscriber.assertValueCount(3);
//        testSubscriber.assertNotTerminated();
//
//        //时间再经过2秒
//        mTestScheduler.advanceTimeBy(2, TimeUnit.SECONDS);
//        testSubscriber.assertValues(0L, 1L, 2L, 3L ,4L);
//        testSubscriber.assertValueCount(5);
//        testSubscriber.assertNotTerminated();
//
//        //时间到10秒
//        mTestScheduler.advanceTimeTo(10, TimeUnit.SECONDS);
//        testSubscriber.assertValueCount(10);
//        testSubscriber.assertTerminated();
//    }
//
//    /**
//     * timer
//     */
//
//    @Test
//    public void testTimer() {
//        TestScheduler mTestScheduler = new TestScheduler();
//        TestSubscriber<Long> testSubscriber = new TestSubscriber<>();
//        //延时5秒发射
//        Flowable.timer(5, TimeUnit.SECONDS, mTestScheduler)
//                .subscribe(testSubscriber);
//
//        //时间到5秒
//        mTestScheduler.advanceTimeTo(5, TimeUnit.SECONDS);
//        testSubscriber.assertValueCount(1);
//        testSubscriber.assertTerminated();
//    }


}
