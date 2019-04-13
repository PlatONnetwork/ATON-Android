package com.juzix.wallet.app;

import com.juzix.wallet.R;

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


    private int errCode;

    public CustomThrowable(int errCode) {
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
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
            default:
                break;

        }

        return detailMsgRes;
    }
}
