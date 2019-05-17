package com.juzix.wallet.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.juzix.wallet.R;

/**
 * @author matrixelement
 */
public abstract class TransactionEntity implements Comparable<TransactionEntity> {

    /**
     * 交易hash
     */
    private String hash;
    /**
     * 交易实际花费值(手续费)，单位：wei
     */
    private String actualTxCost;
    /**
     * 区块高度
     */
    private String blockNumber;
    /**
     * 链id
     */
    private String chainId;
    /**
     * 交易发起方地址
     */
    private String from;
    /**
     * 排列序号：由区块号和交易索引拼接而成
     */
    private String sequence;
    /**
     * 交易时间（单位：毫秒）
     */
    private String timestamp;
    /**
     * 交易接收方地址
     */
    private String to;
    /**
     * 交易在区块中位置
     */
    private String transactionIndex;
    /**
     * 交易详细信息
     */
    private String txInfo;
    /**
     * 交易状态 1 成功 0 失败
     */
    private String txReceiptStatus;
    /**
     * 交易类型
     * transfer：转账
     * MPCtransaction：MPC交易
     * contractCreate：合约创建
     * voteTicket：投票
     * transactionExecute：合约执行
     * candidateDeposit：质押
     * candidateApplyWithdraw：减持质押
     * candidateWithdraw：提取质押
     * unknown：其他
     */
    private String txType;
    /**
     * 交易金额，单位E
     */
    private String value;
}

