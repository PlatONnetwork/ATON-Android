package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

public class VoteTrasactionExtra {

    /**
     * 投票时价格，单位E
     */
    @JSONField(name = "price")
    private String ticketPrice;
    /**
     * 投票数
     */
    @JSONField(name = "count")
    private String votedNum;
    /**
     * 节点id
     */
    @JSONField(name = "nodeId")
    private String nodeId;
    /**
     * 节点名称
     */
    @JSONField(name = "nodeName")
    private String nodeName;
    /**
     * 质押金(单位:E)
     */
    @JSONField(name = "deposit")
    private String deposit;

    public VoteTrasactionExtra() {
    }

    public VoteTrasactionExtra(String ticketPrice, String votedNum, String nodeId, String nodeName, String deposit) {
        this.ticketPrice = ticketPrice;
        this.votedNum = votedNum;
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.deposit = deposit;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    @JSONField(deserialize = false,serialize = false)
    public String getShowTicketPrice() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(ticketPrice, "1E18"));
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getVotedNum() {
        return votedNum;
    }

    public void setVotedNum(String votedNum) {
        this.votedNum = votedNum;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getDeposit() {
        return deposit;
    }

    @JSONField(deserialize = false,serialize = false)
    public String getShowDeposit() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(deposit, "1E18"));
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }
}
