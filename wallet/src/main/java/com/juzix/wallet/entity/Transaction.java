package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.util.TypeUtils;

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
    @JSONField(serializeUsing = TxReceiptStatusCodec.class, deserializeUsing = TxReceiptStatusCodec.class)
    private TxReceiptStatus txReceiptStatus;
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

    public TxReceiptStatus getTxReceiptStatus() {
        return txReceiptStatus;
    }

    public void setTxReceiptStatus(TxReceiptStatus txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Transaction o) {
        return Long.compare(o.createTime, createTime);
    }

    //ObjectSerializer和ObjectDeserializer分别是fastjson的编码器和解码器接口
    public class TxReceiptStatusCodec implements ObjectSerializer, ObjectDeserializer {
        //反序列化过程
        @Override
        public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
            Object value = parser.parse();
            return value == null ? null : (T) TxReceiptStatus.getTxReceiptStatusByName(TypeUtils.castToString(value));
        }

        //暂时还不清楚
        public int getFastMatchToken() {
            return 0;
        }

        //序列化过程
        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
            serializer.write((object).toString());
        }
    }

    public enum TxReceiptStatus {

        TRANSFER, MPCTRANSACTION, CONTRACTCREATE, VOTE, TRANSACTIONEXECUTE, AUTHORIZATION, CANDIDATEDEPOSIT, CANDIDATEAPPLYWITHDRAW, CANDIDATEWITHDRAW, UNKNOWN;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }

        private static Map<String, TxReceiptStatus> map = new HashMap<>();

        static {
            for (TxReceiptStatus status : values()) {
                map.put(status.toString(), status);
            }
        }

        public static TxReceiptStatus getTxReceiptStatusByName(String name) {
            return map.get(name);
        }
    }

}
