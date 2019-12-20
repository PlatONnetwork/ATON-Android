package com.juzix.wallet;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Test {


    public static void main(String[] args) {

        System.out.println(Runtime.getRuntime().availableProcessors());


        ExecutorService executorService = Executors.newFixedThreadPool(10);

        executorService.execute(new Runnable() {
            @Override
            public void run() {
            }
        });

    }
}
