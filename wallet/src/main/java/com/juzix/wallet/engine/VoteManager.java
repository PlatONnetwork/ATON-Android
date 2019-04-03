package com.juzix.wallet.engine;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.HttpClient;
import com.juzhen.framework.util.MapUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.entity.RegionInfoEntity;
import com.juzix.wallet.db.entity.SingleVoteInfoEntity;
import com.juzix.wallet.db.entity.TicketInfoEntity;
import com.juzix.wallet.db.sqlite.RegionInfoDao;
import com.juzix.wallet.db.sqlite.SingleVoteInfoDao;
import com.juzix.wallet.engine.service.VoteService;
import com.juzix.wallet.entity.BatchVoteSummaryEntity;
import com.juzix.wallet.entity.BatchVoteTransactionEntity;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.entity.SingleVoteEntity;
import com.juzix.wallet.entity.TicketEntity;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.TicketContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.DefaultWasmGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


/**
 * @author matrixelement
 */
public class VoteManager {

    private static final String TAG = VoteManager.class.getSimpleName();
    public static final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    public static final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;

    private VoteManager() {

    }

    private static class InstanceHolder {
        private static volatile VoteManager INSTANCE = new VoteManager();
    }

    public static VoteManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<Response<ApiResponse<List<BatchVoteSummaryEntity>>>> getBatchVoteSummary(String[] addressList) {
        return HttpClient.getInstance().createService(VoteService.class).getBatchVoteSummary(ApiRequestBody.newBuilder()
                .put("addressList", addressList)
                .put("cid", "203")
                .build());
    }

    public Single<List<BatchVoteTransactionEntity>> getBatchVoteTransaction(String[] walletAddrs) {
        return HttpClient.getInstance().createService(VoteService.class).getBatchVoteTransaction(ApiRequestBody.newBuilder()
                .put("walletAddrs", walletAddrs)
                .put("cid", "203")
                .put("pageNo", 1)
                .put("pageSize", 10)
                .build())
                .toFlowable()
                .flatMap(new Function<Response<ApiResponse<List<BatchVoteTransactionEntity>>>, Publisher<BatchVoteTransactionEntity>>() {
                    @Override
                    public Publisher<BatchVoteTransactionEntity> apply(Response<ApiResponse<List<BatchVoteTransactionEntity>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse.body() != null) {
                            return Flowable.fromIterable(apiResponseResponse.body().getData());
                        } else {
                            return Flowable.fromIterable(Collections.emptyList());
                        }
                    }
                }).map(new Function<BatchVoteTransactionEntity, BatchVoteTransactionEntity>() {
                    @Override
                    public BatchVoteTransactionEntity apply(BatchVoteTransactionEntity batchVoteTransactionEntity) throws Exception {
                        Optional<RegionEntity> optional = getBatchVoteTransactionRegion(batchVoteTransactionEntity.getCandidateId());
                        if (!optional.isEmpty()) {
                            batchVoteTransactionEntity.setRegionEntity(optional.get());
                        }
                        return batchVoteTransactionEntity;
                    }
                }).map(new Function<BatchVoteTransactionEntity, BatchVoteTransactionEntity>() {
                    @Override
                    public BatchVoteTransactionEntity apply(BatchVoteTransactionEntity batchVoteTransactionEntity) throws Exception {
                        SingleVoteInfoEntity singleVoteInfoEntity = SingleVoteInfoDao.getInstance().getTransactionByHash(batchVoteTransactionEntity.getTransactionHash());
                        if (singleVoteInfoEntity != null) {
                            batchVoteTransactionEntity.setNodeName(singleVoteInfoEntity.getCandidateName());
                            batchVoteTransactionEntity.setVoteStaked(singleVoteInfoEntity.getVoteStaked());
                        }

                        return batchVoteTransactionEntity;
                    }
                })
                .toList();
    }


    public Single<Long> getPoolRemainder() {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                try {
                    Web3j web3j = Web3jManager.getInstance().getWeb3j();
                    TicketContract ticketContract = TicketContract.load(
                            web3j,
                            new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                            new DefaultWasmGasProvider());
                    return NumberParserUtils.parseLong(ticketContract.GetPoolRemainder().send());
                } catch (Exception exp) {
                    return 0L;
                }
            }
        });
    }

    public Single<String> getTicketPrice() {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
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
        });
    }

    public Single<Long> getCandidateEpoch(String nodeId) {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                try {
                    Web3j web3j = Web3jManager.getInstance().getWeb3j();
                    TicketContract ticketContract = TicketContract.load(
                            web3j,
                            new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                            new DefaultWasmGasProvider());
                    return NumberParserUtils.parseLong(ticketContract.GetCandidateEpoch(nodeId).send());
                } catch (Exception exp) {
                    return 0L;
                }
            }
        });
    }

    public Map<String, List<String>> getBatchCandidateTicketIds(String nodeIds) {

        Map<String, List<String>> map = new HashMap<>();

        try {
            Web3j web3j = Web3jManager.getInstance().getWeb3j();
            TicketContract ticketContract = TicketContract.load(
                    web3j,
                    new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                    new DefaultWasmGasProvider());
            String ticketIds = ticketContract.GetBatchCandidateTicketIds(nodeIds).send();
            map = JSONUtil.parseObject(ticketIds, Map.class);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return map;
    }

    public Single<Integer> getCandidateTicketIdsCounter(String nodeId) {

        return Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                TicketContract ticketContract = TicketContract.load(
                        web3j,
                        new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                        new DefaultWasmGasProvider());
                String ticketIds = ticketContract.GetCandidateTicketIds(nodeId).send();
                return JSONUtil.parseArray(ticketIds, String.class).size();
            }
        }).onErrorReturnItem(0);
    }

    public Single<SingleVoteInfoEntity> submitVote(Credentials credentials, IndividualWalletEntity walletEntity, CandidateEntity candidateEntity, String ticketNum, String ticketPrice) {

        String walletName = walletEntity.getName();
        String walletAddress = walletEntity.getPrefixAddress();
        String candidateId = candidateEntity.getCandidateId();
        String candidateName = candidateEntity.getCandidateName();
        BigInteger value = BigDecimalUtil.mul(ticketPrice, ticketNum).toBigInteger();
        double energonPrice = BigDecimalUtil.div(BigDecimalUtil.mul(GAS_PRICE.doubleValue(), GAS_LIMIT.doubleValue()), 1E18);

        return voteTicket(credentials, ticketPrice, ticketNum, candidateId)
                .filter(new Predicate<TransactionReceipt>() {
                    @Override
                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                        return transactionReceipt != null;
                    }
                })
                .map(new Function<TransactionReceipt, String>() {
                    @Override
                    public String apply(TransactionReceipt transactionReceipt) throws Exception {
                        return transactionReceipt.getTransactionHash();
                    }
                })
                .toSingle()
                .flatMap(new Function<String, SingleSource<SingleVoteInfoEntity>>() {
                    @Override
                    public SingleSource<SingleVoteInfoEntity> apply(String transactionHash) throws Exception {
                        return Single.zip(getTransactionUuid(ticketNum, transactionHash), getTicketIdList(ticketNum, transactionHash), new BiFunction<String, List<String>, List<TicketInfoEntity>>() {
                            @Override
                            public List<TicketInfoEntity> apply(String transactionUuid, List<String> ticketIdList) throws Exception {
                                return getTicketInfoList(ticketIdList, transactionUuid, candidateId, ticketPrice).blockingGet();
                            }
                        }).map(new Function<List<TicketInfoEntity>, SingleVoteInfoEntity>() {
                            @Override
                            public SingleVoteInfoEntity apply(List<TicketInfoEntity> ticketInfoEntityList) throws Exception {
                                return new SingleVoteInfoEntity.Builder()
                                        .uuid(UUID.randomUUID().toString())
                                        .hash(transactionHash)
                                        .transactionId(ticketInfoEntityList.get(0).getTransactionId())
                                        .candidateId(candidateId)
                                        .candidateName(candidateName)
                                        .host(candidateEntity.getHost())
                                        .contractAddress(TicketContract.CONTRACT_ADDRESS)
                                        .walletName(walletName)
                                        .walletAddress(walletAddress)
                                        .createTime(System.currentTimeMillis())
                                        .value(BigDecimalUtil.div(value.doubleValue(), 1E18))
                                        .ticketNumber(NumberParserUtils.parseLong(ticketNum))
                                        .ticketPrice(ticketPrice)
                                        .energonPrice(energonPrice)
                                        .status(SingleVoteEntity.STATUS_PENDING)
                                        .tickets(ticketInfoEntityList)
                                        .build();
                            }
                        });
                    }
                })
                .doOnSuccess(new Consumer<SingleVoteInfoEntity>() {
                    @Override
                    public void accept(SingleVoteInfoEntity voteInfoEntity) throws Exception {
                        SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);
                        updateVoteTicket(voteInfoEntity);
                    }
                });
    }

    /**
     * 更新投票数
     */
    public void updateVoteTickets() {
        Flowable.fromCallable(new Callable<List<SingleVoteInfoEntity>>() {
            @Override
            public List<SingleVoteInfoEntity> call() throws Exception {
                return SingleVoteInfoDao.getInstance().getTransactionListByStatus(SingleVoteEntity.STATUS_PENDING);
            }
        }).flatMap(new Function<List<SingleVoteInfoEntity>, Publisher<SingleVoteInfoEntity>>() {
            @Override
            public Publisher<SingleVoteInfoEntity> apply(List<SingleVoteInfoEntity> singleVoteInfoEntities) throws Exception {
                return Flowable.fromIterable(singleVoteInfoEntities);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Consumer<SingleVoteInfoEntity>() {
                    @Override
                    public void accept(SingleVoteInfoEntity voteInfoEntity) throws Exception {
                        updateVoteTicket(voteInfoEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "updateVoteTickets " + throwable.getMessage());
                    }
                });
    }

    /**
     * 分批获取投票详情
     *
     * @param ticketIds
     * @return
     */
    public Single<Map<String, TicketEntity>> getTicketBatchDetail(String ticketIds) {

        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                TicketContract ticketContract = TicketContract.load(
                        web3j,
                        new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                        new DefaultWasmGasProvider());
                return ticketContract.GetBatchTicketDetail(ticketIds).send();
            }
        }).map(new Function<String, List<TicketDto>>() {
            @Override
            public List<TicketDto> apply(String resp) throws Exception {
                return JSONUtil.parseArray(resp, TicketDto.class);
            }
        })
                .toFlowable()
                .flatMap(new Function<List<TicketDto>, Publisher<TicketDto>>() {
                    @Override
                    public Publisher<TicketDto> apply(List<TicketDto> ticketDtos) throws Exception {
                        return Flowable.fromIterable(ticketDtos);
                    }
                })
                .map(new Function<TicketDto, TicketEntity>() {

                    @Override
                    public TicketEntity apply(TicketDto ticketDto) throws Exception {
                        return new TicketEntity.Builder()
                                .state(ticketDto.getState())
                                .rBlockNumber(ticketDto.getRBlockNumber().longValue())
                                .owner(ticketDto.getOwner())
                                .deposit(ticketDto.getDeposit().toString())
                                .candidateId(ticketDto.getCandidateId())
                                .blockNumber(ticketDto.getBlockNumber().longValue())
                                .ticketId(ticketDto.getTicketId())
                                .build();
                    }
                })
                .collect(new Callable<Map<String, TicketEntity>>() {
                    @Override
                    public Map<String, TicketEntity> call() throws Exception {
                        return new HashMap<>();
                    }
                }, new BiConsumer<Map<String, TicketEntity>, TicketEntity>() {
                    @Override
                    public void accept(Map<String, TicketEntity> stringTicketEntityMap, TicketEntity ticketEntity) throws Exception {
                        stringTicketEntityMap.put(ticketEntity.getTicketId(), ticketEntity);
                    }
                })
                .onErrorReturnItem(new HashMap<>());
    }

    /**
     * 获取投票详情
     *
     * @param ticketId
     * @return
     */
    public Single<List<TicketEntity>> getTicketDetail(String ticketId) {

        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                TicketContract ticketContract = TicketContract.load(
                        web3j,
                        new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                        new DefaultWasmGasProvider());
                return ticketContract.GetBatchTicketDetail(ticketId).send();
            }
        }).map(new Function<String, List<TicketDto>>() {
            @Override
            public List<TicketDto> apply(String resp) throws Exception {
                return JSONUtil.parseArray(resp, TicketDto.class);
            }
        }).toFlowable().flatMap(new Function<List<TicketDto>, Publisher<TicketDto>>() {
            @Override
            public Publisher<TicketDto> apply(List<TicketDto> ticketDtos) throws Exception {
                return Flowable.fromIterable(ticketDtos);
            }
        }).map(new Function<TicketDto, TicketEntity>() {
            @Override
            public TicketEntity apply(TicketDto ticketDto) throws Exception {
                return new TicketEntity.Builder()
                        .state(ticketDto.getState())
                        .rBlockNumber(ticketDto.getRBlockNumber().longValue())
                        .owner(ticketDto.getOwner())
                        .deposit(ticketDto.getDeposit().toString())
                        .candidateId(ticketDto.getCandidateId())
                        .blockNumber(ticketDto.getBlockNumber().longValue())
                        .ticketId(ticketDto.getTicketId())
                        .build();
            }
        }).toList();

    }

    private Single<TransactionReceipt> voteTicket(Credentials credentials, String ticketPrice, String ticketNum, String candidateId) {
        return Single
                .fromCallable(new Callable<TransactionReceipt>() {
                    @Override
                    public TransactionReceipt call() throws Exception {
                        Web3j web3j = Web3jManager.getInstance().getWeb3j();
                        TicketContract ticketContract = TicketContract.load(web3j, credentials, new DefaultWasmGasProvider());
                        return ticketContract.VoteTicket(new BigInteger(ticketNum), new BigInteger(ticketPrice), candidateId).send();
                    }
                });
    }

    private String getTransactionHash(Credentials credentials, BigInteger value, String voteTicketData) {
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        TransactionManager transactionManager = new RawTransactionManager(web3j, credentials);
        String transactionHash = null;
        try {
            EthSendTransaction transaction = transactionManager.sendTransaction(GAS_PRICE, GAS_LIMIT, TicketContract.CONTRACT_ADDRESS, voteTicketData, value);
            if (transaction != null) {
                transactionHash = transaction.getTransactionHash();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return transactionHash;
    }

    private Optional<RegionEntity> getBatchVoteTransactionRegion(String candidateId) {
        return Flowable.fromCallable(new Callable<List<SingleVoteInfoEntity>>() {
            @Override
            public List<SingleVoteInfoEntity> call() throws Exception {
                return SingleVoteInfoDao.getInstance().getTransactionListByCandidateId(candidateId);
            }
        }).filter(new Predicate<List<SingleVoteInfoEntity>>() {
            @Override
            public boolean test(List<SingleVoteInfoEntity> singleVoteInfoEntityList) throws Exception {
                return !singleVoteInfoEntityList.isEmpty();
            }
        }).switchIfEmpty(new Flowable<List<SingleVoteInfoEntity>>() {
            @Override
            protected void subscribeActual(Subscriber<? super List<SingleVoteInfoEntity>> observer) {
                observer.onError(new Throwable());
            }
        }).flatMap(new Function<List<SingleVoteInfoEntity>, Publisher<SingleVoteInfoEntity>>() {
            @Override
            public Publisher<SingleVoteInfoEntity> apply(List<SingleVoteInfoEntity> singleVoteInfoEntities) throws Exception {
                return Flowable.fromIterable(singleVoteInfoEntities);
            }
        }).map(new Function<SingleVoteInfoEntity, Optional<RegionEntity>>() {
            @Override
            public Optional<RegionEntity> apply(SingleVoteInfoEntity voteInfoEntity) throws Exception {
                RegionInfoEntity regionInfoEntity = RegionInfoDao.getInstance().getRegionInfoWithIp(voteInfoEntity.getHost());
                if (regionInfoEntity == null) {
                    return new Optional<RegionEntity>(null);
                } else {
                    return new Optional<RegionEntity>(regionInfoEntity.toRegionEntity());
                }
            }
        })
                .onErrorReturnItem(new Optional<RegionEntity>(null))
                .blockingFirst();
    }

    private Single<String> getTransactionUuid(String ticketNum, String transactionHash) {
        return Flowable
                .range(0, NumberParserUtils.parseInt(ticketNum))
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer index) throws Exception {
                        String ticketId = genTicketId(index, transactionHash);
                        if (index == 0) {
                            return ticketId;
                        } else {
                            return TextUtils.concat(":", ticketId).toString();
                        }
                    }
                })
                .collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() {
                    @Override
                    public void accept(StringBuilder stringBuilder, String s) throws Exception {
                        stringBuilder.append(s);
                    }
                })
                .map(new Function<StringBuilder, String>() {
                    @Override
                    public String apply(StringBuilder stringBuilder) throws Exception {
                        return stringBuilder.toString();
                    }
                });
    }

    private Single<List<String>> getTicketIdList(String ticketNum, String transactionHash) {
        return Flowable
                .range(0, NumberParserUtils.parseInt(ticketNum))
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer index) throws Exception {
                        return genTicketId(index, transactionHash);
                    }
                })
                .collect(new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<String>, String>() {
                    @Override
                    public void accept(List<String> ticketIdList, String ticketId) throws Exception {
                        ticketIdList.add(ticketId);
                    }
                });
    }

    private Single<List<TicketInfoEntity>> getTicketInfoList(List<String> ticketIdList, String transactionUuid, String candidateId, String ticketPrice) {
        return Flowable
                .fromIterable(ticketIdList)
                .map(new Function<String, TicketInfoEntity>() {
                    @Override
                    public TicketInfoEntity apply(String ticketId) throws Exception {
                        return new TicketInfoEntity.Builder()
                                .uuid(ticketId)
                                .candidateId(candidateId)
                                .deposit(ticketPrice)
                                .ticketId(ticketId)
                                .transactionUuid(transactionUuid)
                                .build();
                    }
                })
                .collect(new Callable<List<TicketInfoEntity>>() {
                    @Override
                    public List<TicketInfoEntity> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<TicketInfoEntity>, TicketInfoEntity>() {
                    @Override
                    public void accept(List<TicketInfoEntity> ticketInfoEntityList, TicketInfoEntity ticketInfoEntity) throws Exception {
                        ticketInfoEntityList.add(ticketInfoEntity);
                    }
                });
    }

    /**
     * 发送投票交易
     *
     * @param hash
     * @return
     */
    private Single<TransactionReceipt> sendTransaction(String hash) {
        return Single.fromCallable(new Callable<TransactionReceipt>() {
            @Override
            public TransactionReceipt call() {
                PollingTransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(
                        Web3jManager.getInstance().getWeb3j(), TransactionManager.DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                TransactionReceipt receipt = null;
                try {
                    receipt = transactionReceiptProcessor.waitForTransactionReceipt(hash);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                }
                return receipt;
            }
        });
    }

    private Single<String> getVoteTicketEventEvents(TransactionReceipt receipt) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                TicketContract ticketContract = TicketContract.load(web3j,
                        new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                        new DefaultWasmGasProvider());
                List<TicketContract.VoteTicketEventEventResponse> responses = ticketContract.getVoteTicketEventEvents(receipt);
                if (responses != null && !responses.isEmpty()) {
                    return responses.get(0).param1;
                }
                return null;
            }
        });
    }

    private void updateVoteTicket(SingleVoteInfoEntity singleVoteInfoEntity) {

        String transactionHash = singleVoteInfoEntity.getHash();
        String candidateId = singleVoteInfoEntity.getCandidateId();

        sendTransaction(transactionHash)
                .flatMap(new Function<TransactionReceipt, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(TransactionReceipt transactionReceipt) throws Exception {
                        return getVoteTicketEventEvents(transactionReceipt);
                    }
                })
                .map(new Function<String, JSONObject>() {
                    @Override
                    public JSONObject apply(String resp) throws Exception {
                        return JSONObject.parseObject(resp);
                    }
                })
                .filter(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(JSONObject jsonObject) throws Exception {
                        boolean ret = jsonObject.getBoolean("Ret");
                        if (!ret) {
                            SingleVoteInfoEntity tempVoteInfoEntity = singleVoteInfoEntity.clone();
                            tempVoteInfoEntity.setStatus(SingleVoteEntity.STATUS_FAILED);
                            SingleVoteInfoDao.getInstance().insertTransaction(tempVoteInfoEntity);
                        }
                        return ret;
                    }
                })
                .toSingle()
                .flatMap(new Function<JSONObject, SingleSource<SingleVoteInfoEntity>>() {
                    @Override
                    public SingleSource<SingleVoteInfoEntity> apply(JSONObject jsonObject) throws Exception {

                        String data = MapUtils.getString(jsonObject, "Data");
                        String[] array = data.split(":", 2);
                        String validTicketNum = array[0];
                        String ticketPrice = array[1];

                        return Single.zip(getTransactionUuid(validTicketNum, transactionHash), getTicketIdList(validTicketNum, transactionHash), new BiFunction<String, List<String>, List<TicketInfoEntity>>() {
                            @Override
                            public List<TicketInfoEntity> apply(String transactionUuid, List<String> ticketIdList) throws Exception {
                                return getTicketInfoList(ticketIdList, transactionUuid, candidateId, ticketPrice).blockingGet();
                            }
                        }).map(new Function<List<TicketInfoEntity>, SingleVoteInfoEntity>() {
                            @Override
                            public SingleVoteInfoEntity apply(List<TicketInfoEntity> ticketInfoEntityList) throws Exception {

                                SingleVoteInfoEntity tempVoteInfoEntity = singleVoteInfoEntity.clone();
                                tempVoteInfoEntity.setTransactionId(ticketInfoEntityList.get(0).getTransactionId());
                                tempVoteInfoEntity.setTicketInfoEntityArrayList(ticketInfoEntityList);
                                tempVoteInfoEntity.setStatus(SingleVoteEntity.STATUS_SUCCESS);

                                return tempVoteInfoEntity;
                            }
                        });
                    }
                })
                .doOnSuccess(new Consumer<SingleVoteInfoEntity>() {
                    @Override
                    public void accept(SingleVoteInfoEntity voteInfoEntity) throws Exception {
                        SingleVoteInfoDao.getInstance().insertTransaction(voteInfoEntity);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<SingleVoteInfoEntity>() {
                    @Override
                    public void accept(SingleVoteInfoEntity singleVoteInfoEntity) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "updateVoteTicket " + throwable.getMessage());
                    }
                });
    }

    private String genTicketId(int index, String hash) {
        byte[] data1 = Numeric.hexStringToByteArray(hash);
        byte[] data2 = String.valueOf(index).getBytes();
        byte[] data3 = new byte[data1.length + data2.length];
        System.arraycopy(data1, 0, data3, 0, data1.length);
        System.arraycopy(data2, 0, data3, data1.length, data2.length);
        return "0x" + AppUtil.sha3256(data3);
    }

}
