package com.juzix.wallet.entity;

import android.support.annotation.StringRes;

import com.juzix.wallet.R;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {

    /**
     * 转账
     */
    TRANSFER(0, R.string.transfer),
    /**
     * 合约创建
     */
    CONTRACT_CREATION(1, R.string.contract_creation),
    /**
     * 合约执行
     */
    CONTRACT_EXECUTION(2, R.string.contract_execution),
    /**
     * 其他收入
     */
    OTHER_INCOME(3, R.string.other_income),
    /**
     * 其他支出
     */
    OTHER_EXPENSES(4, R.string.other_expenses),
    /**
     * MPC交易
     */
    MPC_TRANSACTION(5, R.string.mpc_transaction),
    /**
     * 发起质押(创建验证人)
     */
    CREATE_VALIDATOR(1000, R.string.create_validator),
    /**
     * 修改质押信息(编辑验证人)
     */
    EDIT_VALIDATOR(1001, R.string.edit_validator),
    /**
     * 增加自有质押
     */
    INCREASE_STAKING(1002, R.string.increase_staking),
    /**
     * 退出验证人(包括撤销验证人创建,撤销质押)
     */
    EXIT_VALIDATOR(1003, R.string.exit_validator),
    /**
     * 发起委托(委托)
     */
    DELEGATE(1004, R.string.delegate),
    /**
     * 减持/撤销委托(赎回委托)
     */
    UNDELEGATE(1005, R.string.undelegate),
    /**
     * 创建文提案
     */
    CREATE_TEXT_PROPOSAL(2000, R.string.create_proposal),
    /**
     * 创建升级提案
     */
    CREATE_UPGRADE_PROPOSAL(2001, R.string.create_proposal),
    /**
     * 创建参数提案
     */
    CREATE_PARAMETER_PROPOSAL(2002, R.string.create_proposal),
    /**
     * 给提案投票
     */
    VOTING_PROPOSAL(2003, R.string.voting_proposal),
    /**
     * 版本声明
     */
    DECLARE_VERSION(2004, R.string.declare_version),
    /**
     * 创建取消提案
     */
    CANCEL_PROPOSAL(2005, R.string.create_proposal),
    /**
     * 举报多签(举报验证人)
     */
    REPORT_VALIDATOR(3000, R.string.report_validator),
    /**
     * 创建锁仓计划(创建锁仓)
     */
    CREATE_RESTRICTING(4000, R.string.create_restricting);

    private int value;
    private @StringRes
    int descRes;

    TransactionType(int value, int descRes) {
        this.value = value;
        this.descRes = descRes;
    }

    private static Map<Integer, TransactionType> map = new HashMap<>();

    static {
        for (TransactionType status : values()) {
            map.put(status.value, status);
        }
    }

    public static TransactionType getTxTypeByValue(int value) {
        return map.get(value);
    }

    public int getTxTypeDescRes() {
        return descRes;
    }

    public int getTxTypeValue() {
        return value;
    }

}
