package com.platon.aton.app;

import com.platon.aton.R;

/**
 * @author matrixelement
 */
public class CustomThrowable extends Throwable {

    public static final int CODE_CREATE_WALLET_OK = 0;
    public static final int CODE_ERROR_PASSWORD = -100;
    public static final int CODE_ERROR_ADD_WALLET = -101;
    public static final int CODE_ERROR_DEPLOY = -102;
    public static final int CODE_ERROR_INIT_WALLET = -103;
    public static final int CODE_ERROR_SUBMIT_TRANSACTION = -104;
    public static final int CODE_ERROR_CONFIRM_TRANSACTION = -105;
    public static final int CODE_ERROR_REVOKE_TRANSACTION = -106;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_ILLEGAL_WALLET = -201;
    public static final int CODE_ERROR_UNLINKED_WALLET = -202;
    public static final int CODE_ERROR_TRANSFER_FAILED = -203;
    public static final int CODE_ERROR_NOT_SUFFICIENT_BALANCE = -204;
    public static final int CODE_ERROR_VOTE_TICKET_INSUFFICIENT_BALANCE = -205;
    public static final int CODE_ERROR_NOT_EXIST_VALID_WALLET = -206;
    public static final int CODE_ERROR_ADD_SHARE_WALLET = -207;
    public static final int CODE_NODE_EXIT_CONSENSUS = -208;
    public static final int CODE_ERROR_MNEMONIC = -209;
    public static final int CODE_ERROR_CREATE_WALLET_FAILED = -210;
    public static final int CODE_TRANSFER_FAILED = -211;
    public static final int CODE_TX_KNOWN_TX = 3001;//known transaction
    public static final int CODE_TX_NONCE_TOO_LOW = 3002;//nonce太低
    public static final int CODE_TX_GAS_LOW = 3003;//gas 过低，需要重新估算
    public static final int CODE_TX_BALANCE_NOT_ENOUGH = 3004;//余额不足(普通转账)
    public static final int CODE_TX_BALANCE_NOT_ENOUGH_DELEGATOR = 300401;//余额不足(委托时)
    public static final int CODE_TX_BALANCE_NOT_ENOUGH_WITHDRAW = 300402;//余额不足(赎回时)
    public static final int CODE_TX_BALANCE_NOT_ENOUGH_CLAIM = 300403;//余额不足(领取时)
    public static final int CODE_TX_OVER_BLOCK_GAS_LIMIT = 3005;//超过节点区块gas限制
    public static final int CODE_TX_NODE_EXITED = 3006;//节点已退出或退出中，不能委托
    public static final int CODE_TX_NODE_ASSOCIATED_WALLET = 3007;//节点关联的钱包地址,不能委托
    public static final int CODE_TX_NODE_AS_INSIDE_CANDIDATE = 3008;//节点为初始化时内置的候选人，不能委托
    public static final int CODE_TX_AUTHORIZED_NUMBER_LOW = 3009;//低于节点委托或赎回的最小阀值



    private int errCode;
    private int extraTxCode;

    public CustomThrowable(int errCode) {
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

    public int getExtraTxCode() {
        return extraTxCode;
    }

    public void setExtraTxCode(int extraTxCode) {
        this.extraTxCode = extraTxCode;
    }

    public int getDetailMsgRes() {

        int detailMsgRes = -1;

        switch (errCode) {
            case CODE_CREATE_WALLET_OK:
                break;
            case CODE_ERROR_PASSWORD:
                break;
            case CODE_ERROR_ADD_WALLET:
                detailMsgRes = R.string.createWalletFailed;
                break;
            case CODE_ERROR_DEPLOY:
                break;
            case CODE_ERROR_INIT_WALLET:
                break;
            case CODE_ERROR_SUBMIT_TRANSACTION:
                break;
            case CODE_ERROR_CONFIRM_TRANSACTION:
                break;
            case CODE_ERROR_REVOKE_TRANSACTION:
                break;
            case CODE_ERROR_WALLET_EXISTS:
                detailMsgRes = R.string.walletExists;
                break;
            case CODE_ERROR_ILLEGAL_WALLET:
                detailMsgRes = R.string.illegalWalletAddress;
                break;
            case CODE_ERROR_UNLINKED_WALLET:
                detailMsgRes = R.string.joint_wallet_can_not_added;
                break;
            case CODE_ERROR_TRANSFER_FAILED:
                detailMsgRes = R.string.transfer_failed;
                break;
            case CODE_ERROR_NOT_SUFFICIENT_BALANCE:
                detailMsgRes = R.string.insufficient_balance;
                break;
            case CODE_ERROR_VOTE_TICKET_INSUFFICIENT_BALANCE:
                detailMsgRes = R.string.voteTicketInsufficientBalanceTips;
                break;
            case CODE_ERROR_NOT_EXIST_VALID_WALLET:
                detailMsgRes = R.string.voteTicketCreateWalletTips;
                break;
            case CODE_ERROR_ADD_SHARE_WALLET:
                detailMsgRes = R.string.addWalletFailed;
                break;
            case CODE_NODE_EXIT_CONSENSUS:
                detailMsgRes = R.string.node_exit_consensus;
                break;
            case CODE_ERROR_CREATE_WALLET_FAILED:
                detailMsgRes = R.string.createWalletFailed;
                break;
            case CODE_TRANSFER_FAILED:
                detailMsgRes = R.string.transfer_failed;
                break;
            case CODE_TX_KNOWN_TX:
                detailMsgRes = R.string.msg_transaction_repeatedly_exception;
                break;
            case CODE_TX_NONCE_TOO_LOW:
                //detailMsgRes = App.getContext().getString(R.string.msg_transaction_exception,CODE_TX_NONCE_TOO_LOW);
                detailMsgRes = R.string.msg_transaction_exception;
                break;
            case CODE_TX_GAS_LOW:
                detailMsgRes = R.string.msg_expired_qr_code;
                break;
            case CODE_TX_BALANCE_NOT_ENOUGH:

                //余额不足分场景：普通转账、委托转账、赎回、领取
                if(getExtraTxCode() == CODE_TX_BALANCE_NOT_ENOUGH_DELEGATOR){
                    detailMsgRes = R.string.delegate_less_than_fee;
                }else if(getExtraTxCode() == CODE_TX_BALANCE_NOT_ENOUGH_WITHDRAW){
                    detailMsgRes = R.string.withdraw_less_than_fee;
                }else if(getExtraTxCode() == CODE_TX_BALANCE_NOT_ENOUGH_CLAIM){
                    detailMsgRes = R.string.msg_balance_not_enougth;
                }else{
                    detailMsgRes = R.string.msg_balance_not_enougth;
                }
                break;
            case CODE_TX_OVER_BLOCK_GAS_LIMIT:
                detailMsgRes = R.string.msg_transaction_over_block_gas_limit;
                break;
            case CODE_TX_NODE_EXITED:
                detailMsgRes = R.string.the_validator_has_exited_and_cannot_be_delegated;
                break;
            case CODE_TX_NODE_ASSOCIATED_WALLET:
                detailMsgRes = R.string.msg_transaction_node_associated_wallet;
                break;
            case CODE_TX_NODE_AS_INSIDE_CANDIDATE:
                detailMsgRes = R.string.msg_transaction_node_as_inside_candidate;
                break;
            case CODE_TX_AUTHORIZED_NUMBER_LOW:
                detailMsgRes = R.string.delegate_amount_tips;
                break;
            default:
                break;
        }
        return detailMsgRes;
    }
}
