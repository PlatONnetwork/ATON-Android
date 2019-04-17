package com.juzix.wallet;


import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class Test {


    public static void main(String[] args) {
        String[] array = {"64ba18ce01172da6a95b0d5b0a93aee727d77e5b2f04255a532a9566edaee7808383812a860acf5e43efeca3d9321547bfcdefd89e9d0c605dcdb65ce0bbb617", "哈哈"};

        Observable.fromArray(array).contains("哈哈")
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        System.out.println(aBoolean);
                    }
                });
    }


}
