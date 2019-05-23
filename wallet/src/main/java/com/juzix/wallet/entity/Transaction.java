package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Transaction implements Comparable<Transaction> {

    /**
     * 交易hash
     */
    private String hash;
    /**
     * 当前交易所在快高
     */
    private long blockNumber;
    /**
     * 当前交易的链id
     */
    private String chainId;
    /**
     * 交易创建时间
     */
    @JSONField(name = "timestamp")
    private long createTime;
    /**
     * 交易实际花费值(手续费)，单位：wei
     * “21168000000000”
     */
    private String actualTxCost;
    /**
     * 交易发送方
     */
    private String from;
    /**
     * 交易接收方
     */
    private String to;
    /**
     * 交易接收者类型（to是合约还是账户）contract合约、 account账户
     * "account"
     */
    private String receiveType;
    /**
     * 交易序列号
     */
    private long sequence;
    /**
     * 交易状态
     */
    private String txReceiptStatus;
    /**
     * 交易类型
     * transfer ：转账
     * MPCtransaction ： MPC交易
     * contractCreate ： 合约创建
     * vote ： 投票
     * transactionExecute ： 合约执行
     * authorization ： 权限
     * candidateDeposit：竞选质押
     * candidateApplyWithdraw：减持质押
     * candidateWithdraw：提取质押
     * unknown：未知
     */
    private String txType;
    /**
     * 交易金额
     */
    private String value;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getActualTxCost() {
        return actualTxCost;
    }

    public void setActualTxCost(String actualTxCost) {
        this.actualTxCost = actualTxCost;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getTxReceiptStatus() {
        return txReceiptStatus;
    }

    public void setTxReceiptStatus(String txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public TxType getTxType() {
        return TxType.getTxTypeByName(txType);
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getValue() {
        return value;
    }

    public String getShowValue() {
        return NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(value, "1E18"), 4);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isSuccess() {
        return "1".equals(txReceiptStatus);
    }

    @Override
    public int compareTo(Transaction o) {
        return Long.compare(o.sequence, sequence);
    }

    public enum TxType {

        TRANSFER("transfer", "转账"), MPCTRANSACTION("MPCtransaction：MPC", "MPC交易"), CONTRACTCREATE("contractCreate", "合约创建"), VOTETICKET("voteTicket", "投票"), TRANSACTIONEXECUTE("transactionExecute", "合约执行"), CANDIDATEDEPOSIT("candidateDeposit", "质押"), CANDIDATEAPPLYWITHDRAW("candidateApplyWithdraw", "减持质押"), CANDIDATEWITHDRAW("candidateWithdraw", "提取质押"), UNKNOWN("unknown", "其他");

        private String name;
        private String desc;

        TxType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        private static Map<String, TxType> map = new HashMap<>();

        static {
            for (TxType status : values()) {
                map.put(status.name, status);
            }
        }

        public static TxType getTxTypeByName(String name) {
            return map.get(name);
        }

        public String getTxTypeDesc() {
            return desc;
        }

    }

}
