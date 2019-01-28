package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.TicketEntity;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.TicketContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.DefaultWasmGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * @author matrixelement
 */
public class TicketManager {

    public static final  int        CODE_OK                     = 0;
    public static final  int        CODE_ERROR_PASSWORD         = -100;
    public static final  int        CODE_ERROR_VOTE_TICKET      = -101;
    public static final  int        CODE_ERROR_GET_TICKET_PRICE = -102;
    public static final  int        CODE_ERROR_UNKNOW           = -999;
    public static final  BigInteger GAS_PRICE                   = DefaultGasProvider.GAS_PRICE;
    public static final  BigInteger GAS_LIMIT                   = DefaultGasProvider.GAS_LIMIT;

    private TicketManager() {

    }

    public static TicketManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public long getPoolRemainder() {
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String poolRemainder = ticketContract.GetPoolRemainder().send();
            return NumberParserUtils.parseLong(poolRemainder);
        } catch (Exception exp) {
            return 0L;
        }
    }

    public String getTicketPrice() {
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            return ticketContract.GetTicketPrice().send();
        } catch (Exception exp) {
            return "0";
        }
    }

    public long getCandidateEpoch(String nodeId) {
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String epoch = ticketContract.GetCandidateEpoch(nodeId).send();
            return Long.parseLong(epoch);
        } catch (Exception exp) {
            return 0L;
        }
    }

    public Map<String, List<String>> getBatchCandidateTicketIds(String nodeIds) {
        if(TextUtils.isEmpty(nodeIds)){
            return new HashMap<>();
        }
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String ticketIds = ticketContract.GetBatchCandidateTicketIds(nodeIds).send();
            return JSONUtil.parseObject(ticketIds, Map.class);
        } catch (Exception exp) {
            return new HashMap<>();
        }
    }

    public long getCandidateTicketIdsCounter(String nodeId) {
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String ticketIds = ticketContract.GetCandidateTicketIds(nodeId).send();
            return JSONUtil.parseArray(ticketIds, String.class).size();
        } catch (Exception exp) {
            return 0L;
        }
    }

//    public int submitVoteTicket(String password, long count, long price, IndividualWalletEntity walletEntity, CandidateEntity candidateEntity) {
//        Credentials credentials = credentials(password, walletEntity.getKey());
//        if (credentials == null) {
//            return CODE_ERROR_PASSWORD;
//        }
//        try {
//            Web3j              web3j              = Web3jManager.getInstance().getWeb3j();
//            TicketContract     ticketContract     = TicketContract.load(web3j, credentials, new DefaultWasmGasProvider());
//            String             voteTicketData     = ticketContract.VoteTicketData(BigInteger.valueOf(count), BigInteger.valueOf(price), candidateEntity.getCandidateId());
//            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
//            BigInteger         value              = new BigDecimal(count).multiply(new BigDecimal(price)).toBigInteger();
//            EthSendTransaction transaction        = transactionManager.sendTransaction(GAS_PRICE, GAS_LIMIT, TicketContract.CONTRACT_ADDRESS, voteTicketData, value);
//            String             transactionHash    = transaction.getTransactionHash();
//            PollingTransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(
//                    web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
//            TransactionReceipt receipt = transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
//            if (receipt == null) {
//                return CODE_ERROR_VOTE_TICKET;
//            }
//            List<TicketContract.VoteTicketEventEventResponse> responses = ticketContract.getVoteTicketEventEvents(receipt);
//            if (responses == null || responses.isEmpty()) {
//                return CODE_ERROR_VOTE_TICKET;
//            }
//            String     resp       = responses.get(0).param1;
//            JSONObject jsonObject = JSONObject.parseObject(resp);
//            boolean    ret        = jsonObject.getBoolean("Ret");
//            if (!ret) {
//                return CODE_ERROR_VOTE_TICKET;
//            }
//            long ticketNumber = 0;
//            try{
//               ticketNumber =  Long.parseLong(jsonObject.getString("Data"));
//            }catch (Exception exp){
//                exp.printStackTrace();
//            }
//            if (ticketNumber <= 0) {
//                return CODE_ERROR_VOTE_TICKET;
//            }
//            ArrayList<String> ticketIdList = new ArrayList<>();
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < ticketNumber; i++) {
//                byte[] data1 = Numeric.hexStringToByteArray(transactionHash);
//                byte[] data2 = String.valueOf(i).getBytes();
//                byte[] data3 = new byte[data1.length + data2.length];
//                System.arraycopy(data1, 0, data3, 0, data1.length);
//                System.arraycopy(data2, 0, data3, data1.length, data2.length);
//                String ticketId = "0x" + AppUtil.sha3256(data3);
//                if (i != 0){
//                    sb.append(":");
//                }
//                sb.append(ticketId);
//                ticketIdList.add(ticketId);
//            }
//            if (ticketIdList.isEmpty()) {
//                return CODE_ERROR_VOTE_TICKET;
//            }
//            String               candidateId   = candidateEntity.getCandidateId();
//            String               candidateName = "";
//            CandidateExtraEntity extraEntity   = candidateEntity.getCandidateExtraEntity();
//            if (extraEntity != null) {
//                candidateName = extraEntity.getNodeName();
//            }
//            ArrayList<TicketInfoEntity> tickets = new ArrayList<>();
//            for (int i = 0; i < ticketIdList.size(); i++){
//                String ticketId = ticketIdList.get(i);
//                TicketInfoEntity entity = new TicketInfoEntity.Builder()
//                        .uuid(ticketId)
//                        .candidateId(candidateId)
//                        .deposit(String.valueOf(price))
//                        .ticketId(ticketId)
//                        .transactionUuid(sb.toString())
//                        .build();
//                tickets.add(entity);
//            }
//            SingleVoteInfoEntity voteInfoEntity = new SingleVoteInfoEntity.Builder()
//                    .uuid(UUID.randomUUID().toString())
//                    .hash(receipt.getTransactionHash())
//                    .transactionId(sb.toString())
//                    .candidateId(candidateId)
//                    .candidateName(candidateName)
//                    .region(candidateEntity.getRegion())
//                    .contractAddress(TicketContract.CONTRACT_ADDRESS)
//                    .walletName(walletEntity.getName())
//                    .walletAddress(walletEntity.getAddress())
//                    .createTime(System.currentTimeMillis())
//                    .value(count * price)
//                    .ticketNumber(count)
//                    .ticketPrice(price)
//                    .energonPrice(BigDecimalUtil.div(BigDecimalUtil.mul(GAS_PRICE.doubleValue(), GAS_LIMIT.doubleValue()), 1E18))
//                    .tickets(tickets)
//                    .build();
//            SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);
//            return CODE_OK;
//        } catch (Exception exp) {
//            return CODE_ERROR_VOTE_TICKET;
//        }
//    }

    public int submitVoteTicket(String password, long count, String price, IndividualWalletEntity walletEntity, CandidateEntity candidateEntity) {
        Credentials credentials = credentials(password, walletEntity.getKey());
        if (credentials == null) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            Web3j              web3j              = Web3jManager.getInstance().getWeb3j();
            String             voteTicketData     = TicketContract.VoteTicketData(BigInteger.valueOf(count), new BigInteger(price), candidateEntity.getCandidateId());
            TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
            BigInteger         value              = new BigDecimal(count).multiply(new BigDecimal(price)).toBigInteger();
            EthSendTransaction transaction        = transactionManager.sendTransaction(GAS_PRICE, GAS_LIMIT, TicketContract.CONTRACT_ADDRESS, voteTicketData, value);
            String             transactionHash    = transaction.getTransactionHash();
            if (TextUtils.isEmpty(transactionHash)){
                return CODE_ERROR_VOTE_TICKET;
            }
            ArrayList<String> ticketIdList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < count; i++) {
                String ticketId = genTicketId(i, transactionHash);
                if (i != 0){
                    sb.append(":");
                }
                sb.append(ticketId);
                ticketIdList.add(ticketId);
            }
            String               candidateId   = candidateEntity.getCandidateId();
            String               candidateName = "";
            CandidateExtraEntity extraEntity   = candidateEntity.getCandidateExtraEntity();
            if (extraEntity != null) {
                candidateName = extraEntity.getNodeName();
            }
            ArrayList<TicketInfoEntity> tickets = new ArrayList<>();
            for (int i = 0; i < ticketIdList.size(); i++){
                String ticketId = ticketIdList.get(i);
                TicketInfoEntity entity = new TicketInfoEntity.Builder()
                        .uuid(ticketId)
                        .candidateId(candidateId)
                        .deposit(String.valueOf(price))
                        .ticketId(ticketId)
                        .transactionUuid(sb.toString())
                        .build();
                tickets.add(entity);
            }
            SingleVoteInfoEntity voteInfoEntity = new SingleVoteInfoEntity.Builder()
                    .uuid(UUID.randomUUID().toString())
                    .hash(transactionHash)
                    .transactionId(sb.toString())
                    .candidateId(candidateId)
                    .candidateName(candidateName)
                    .avatar(candidateEntity.getAvatar())
                    .region(candidateEntity.getRegion())
                    .contractAddress(TicketContract.CONTRACT_ADDRESS)
                    .walletName(walletEntity.getName())
                    .walletAddress(walletEntity.getAddress())
                    .createTime(System.currentTimeMillis())
                    .value(BigDecimalUtil.div(value.doubleValue(), 1E18))
                    .ticketNumber(count)
                    .ticketPrice(price)
                    .energonPrice(BigDecimalUtil.div(BigDecimalUtil.mul(GAS_PRICE.doubleValue(), GAS_LIMIT.doubleValue()), 1E18))
                    .status(SingleVoteEntity.STATUS_PENDING)
                    .tickets(tickets)
                    .build();
            SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);

            updateVoteTicket(voteInfoEntity);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_VOTE_TICKET;
        }
    }

    public void updateVoteTickets(){
        List<SingleVoteInfoEntity> singleVoteInfoEntityList = SingleVoteInfoDao.getInstance().getTransactionListByStatus(SingleVoteEntity.STATUS_PENDING);
        for (SingleVoteInfoEntity voteInfoEntity : singleVoteInfoEntityList){
            updateVoteTicket(voteInfoEntity);
        }
    }

    public Map<String, TicketEntity> getTicketBatchDetail(String ticketIds) {
        Map<String, TicketEntity> ticketEntityMap = new HashMap<>();
        if(TextUtils.isEmpty(ticketIds)){
            return ticketEntityMap;
        }
        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String          resp          = ticketContract.GetBatchTicketDetail(ticketIds).send();
            List<TicketDto> ticketDtoList = JSONUtil.parseArray(resp, TicketDto.class);
            for (TicketDto ticketDto : ticketDtoList) {
                String               ticketId = ticketDto.getTicketId();
                TicketEntity.Builder builder  = new TicketEntity.Builder();
                builder.state(ticketDto.getState());
                builder.rBlockNumber(ticketDto.getRBlockNumber().longValue());
                builder.owner(ticketDto.getOwner());
                builder.deposit(ticketDto.getDeposit().toString());
                builder.candidateId(ticketDto.getCandidateId());
                builder.blockNumber(ticketDto.getBlockNumber().longValue());
                builder.ticketId(ticketId);
                ticketEntityMap.put(ticketId, builder.build());
            }

        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return ticketEntityMap;
    }

    private Credentials credentials(String password, String keyJson) {
        Credentials credentials = null;
        try {
            credentials = JZWalletUtil.loadCredentials(password, keyJson);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return credentials;
    }

    private void updateVoteTicket(SingleVoteInfoEntity singleVoteInfoEntity){
        new Thread(){
            @Override
            public void run() {
                try {
                    Web3j              web3j              = Web3jManager.getInstance().getWeb3j();
                    TicketContract     ticketContract     = TicketContract.load(web3j,
                            new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                            new DefaultWasmGasProvider());
                    PollingTransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(
                            web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                    TransactionReceipt receipt = transactionReceiptProcessor.waitForTransactionReceipt(singleVoteInfoEntity.getHash());
                    if (receipt == null) {
                        return ;
                    }
                    List<TicketContract.VoteTicketEventEventResponse> responses = ticketContract.getVoteTicketEventEvents(receipt);
                    if (responses == null || responses.isEmpty()) {
                        return ;
                    }
                    String     resp       = responses.get(0).param1;
                    JSONObject jsonObject = JSONObject.parseObject(resp);
                    boolean    ret        = jsonObject.getBoolean("Ret");
                    if (!ret) {
                        SingleVoteInfoEntity voteInfoEntity = new SingleVoteInfoEntity.Builder()
                                .uuid(singleVoteInfoEntity.getUuid())
                                .hash(singleVoteInfoEntity.getHash())
                                .transactionId(singleVoteInfoEntity.getTransactionId())
                                .candidateId(singleVoteInfoEntity.getCandidateId())
                                .candidateName(singleVoteInfoEntity.getCandidateName())
                                .avatar(singleVoteInfoEntity.getAvatar())
                                .region(singleVoteInfoEntity.getRegion())
                                .contractAddress(singleVoteInfoEntity.getContractAddress())
                                .walletName(singleVoteInfoEntity.getWalletName())
                                .walletAddress(singleVoteInfoEntity.getWalletAddress())
                                .createTime(singleVoteInfoEntity.getCreateTime())
                                .value(singleVoteInfoEntity.getValue())
                                .ticketNumber(singleVoteInfoEntity.getTicketNumber())
                                .ticketPrice(singleVoteInfoEntity.getTicketPrice())
                                .energonPrice(singleVoteInfoEntity.getEnergonPrice())
                                .status(SingleVoteEntity.STATUS_FAILED)
                                .tickets(singleVoteInfoEntity.getTicketInfoEntityArrayList())
                                .build();
                        SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);
                        return ;
                    }
                    long ticketNumber = 0;
                    try{
                        ticketNumber =  Long.parseLong(jsonObject.getString("Data"));
                    }catch (Exception exp){
                        exp.printStackTrace();
                    }
                    if (ticketNumber <= 0) {
                        return ;
                    }
                    ArrayList<String> ticketIdList = new ArrayList<>();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < ticketNumber; i++) {
                        String ticketId = genTicketId(i, singleVoteInfoEntity.getHash());
                        if (i != 0){
                            sb.append(":");
                        }
                        sb.append(ticketId);
                        ticketIdList.add(ticketId);
                    }
                    ArrayList<TicketInfoEntity> tickets = new ArrayList<>();
                    for (int i = 0; i < ticketIdList.size(); i++){
                        String ticketId = ticketIdList.get(i);
                        TicketInfoEntity entity = new TicketInfoEntity.Builder()
                                .uuid(ticketId)
                                .candidateId(singleVoteInfoEntity.getCandidateId())
                                .deposit(String.valueOf(singleVoteInfoEntity.getTicketPrice()))
                                .ticketId(ticketId)
                                .transactionUuid(sb.toString())
                                .build();
                        tickets.add(entity);
                    }
                    SingleVoteInfoEntity voteInfoEntity = new SingleVoteInfoEntity.Builder()
                            .uuid(singleVoteInfoEntity.getUuid())
                            .hash(singleVoteInfoEntity.getHash())
                            .transactionId(sb.toString())
                            .candidateId(singleVoteInfoEntity.getCandidateId())
                            .candidateName(singleVoteInfoEntity.getCandidateName())
                            .avatar(singleVoteInfoEntity.getAvatar())
                            .region(singleVoteInfoEntity.getRegion())
                            .contractAddress(singleVoteInfoEntity.getContractAddress())
                            .walletName(singleVoteInfoEntity.getWalletName())
                            .walletAddress(singleVoteInfoEntity.getWalletAddress())
                            .createTime(singleVoteInfoEntity.getCreateTime())
                            .value(singleVoteInfoEntity.getValue())
                            .ticketNumber(singleVoteInfoEntity.getTicketNumber())
                            .ticketPrice(singleVoteInfoEntity.getTicketPrice())
                            .energonPrice(singleVoteInfoEntity.getEnergonPrice())
                            .status(SingleVoteEntity.STATUS_SUCCESS)
                            .tickets(tickets)
                            .build();
                    SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);
                } catch (Exception exp) {
                }
            }
        }.start();
    }

    public String genTicketId (int index, String hash){
        byte[] data1 = Numeric.hexStringToByteArray(hash);
        byte[] data2 = String.valueOf(index).getBytes();
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return  "0x" + AppUtil.sha3256(data3);
    }

    private static class InstanceHolder {
        private static volatile TicketManager INSTANCE = new TicketManager();
    }
}
