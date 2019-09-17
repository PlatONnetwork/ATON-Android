package com.juzix.wallet.myvote;

import com.juzix.wallet.entity.Country;
import com.juzix.wallet.entity.VotedCandidate;

import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

@Implements(VotedCandidate.class)
public class ShadowVoteBean {
    /**
     * 通过@RealObject注解可以访问原始对象，但注意，通过@RealObject注解的变量调用方法，依然会调用Shadow类的方法，而不是原始类的方法
     * 只能用来访问原始类的field
     */
    @RealObject
    VotedCandidate candidate;

    /**
     * 需要一个无参构造方法
     */
    public ShadowVoteBean(){

    }

    /**
     * 对应原始类的有参构造方法，必须保持参数一致
     */
    public void __constructor__() {

    }




}
