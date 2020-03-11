package com.platon.aton.entity;


public @interface GuideType {

    //导入钱包
    int IMPORT_WALLET = 0;
    //交易记录
    int TRANSACTION_LIST = 1;
    //默认委托列表
    int DELEGATE_LIST = 2;
    //委托验证节点
    int DELEGATE_VALIDATORS = 3;
    //委托节点详情
    int DELEGATE_NODE_DETAIL = 4;
    //委托
    int DELEGATE = 5;
    //赎回委托
    int WITHDRAW_DELEGATE = 6;
}
