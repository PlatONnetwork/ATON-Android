package com.juzix.wallet.engine;

import android.support.annotation.CheckResult;
import android.text.TextUtils;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.juzhen.framework.network.HttpClient;
import com.juzix.wallet.App;
import com.juzix.wallet.db.entity.CandidateInfoEntity;
import com.juzix.wallet.db.entity.RegionInfoEntity;
import com.juzix.wallet.db.sqlite.CandidateInfoDao;
import com.juzix.wallet.db.sqlite.RegionInfoDao;
import com.juzix.wallet.engine.service.RegionService;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.protocol.entity.GetRegionInfoRequestEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.LanguageUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Publisher;
import org.web3j.platon.contracts.CandidateContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultWasmGasProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;


/**
 * @author matrixelement
 */
public class CandidateManager {

    private final static String TAG = CandidateManager.class.getSimpleName();

    private CandidateManager() {

    }

    private static class InstanceHolder {
        private static volatile CandidateManager INSTANCE = new CandidateManager();
    }

    public static CandidateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<List<CandidateEntity>> getCandidateList(String ticketPrice) {

        return Flowable
                .fromCallable(new Callable<List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> call() throws Exception {
                        return getCandidateListFromNet();
                    }
                })
                .flatMap(new Function<List<CandidateEntity>, Publisher<CandidateEntity>>() {
                    @Override
                    public Publisher<CandidateEntity> apply(List<CandidateEntity> candidateDtoList) throws Exception {
                        return Flowable.fromIterable(candidateDtoList);
                    }
                })
                .map(new Function<CandidateEntity, CandidateEntity>() {
                    @Override
                    public CandidateEntity apply(CandidateEntity candidateEntity) throws Exception {
                        RegionInfoEntity regionInfoEntity = RegionInfoDao.getRegionInfoEntityWithIp(candidateEntity.getHost());
                        if (regionInfoEntity != null){
                            candidateEntity.setRegionEntity(regionInfoEntity.toRegionEntity());
                        }
                        candidateEntity.setTicketPrice(ticketPrice);
                        return candidateEntity;
                    }
                })
                .collect(new Callable<List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<CandidateEntity>, CandidateEntity>() {
                    @Override
                    public void accept(List<CandidateEntity> candidateEntities, CandidateEntity candidateEntity) throws Exception {
                        candidateEntities.add(candidateEntity);
                    }
                })
                .doOnSuccess(new Consumer<List<CandidateEntity>>() {
                    @Override
                    public void accept(List<CandidateEntity> candidateEntityList) throws Exception {
                        insertCandidateInfoList(candidateEntityList);
                        updateBatchRegionInfoWithIpList(getIpList(candidateEntityList));
                    }
                });
    }

    public Single<List<CandidateEntity>> getVerifiersList(String ticketPrice) {

        return Flowable
                .fromCallable(new Callable<List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> call() throws Exception {
                        return getVerifiersListFromNet();
                    }
                })
                .flatMap(new Function<List<CandidateEntity>, Publisher<CandidateEntity>>() {
                    @Override
                    public Publisher<CandidateEntity> apply(List<CandidateEntity> candidateDtoList) throws Exception {
                        return Flowable.fromIterable(candidateDtoList);
                    }
                })
                .map(new Function<CandidateEntity, CandidateEntity>() {
                    @Override
                    public CandidateEntity apply(CandidateEntity candidateEntity) throws Exception {
                        RegionInfoEntity regionInfoEntity = RegionInfoDao.getRegionInfoEntityWithIp(candidateEntity.getHost());
                        if (regionInfoEntity != null){
                            candidateEntity.setRegionEntity(regionInfoEntity.toRegionEntity());
                        }
                        candidateEntity.setStatus(CandidateEntity.CandidateStatus.STATUS_VERIFY);
                        candidateEntity.setTicketPrice(ticketPrice);
                        return candidateEntity;
                    }
                })
                .collect(new Callable<List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<CandidateEntity>, CandidateEntity>() {
                    @Override
                    public void accept(List<CandidateEntity> candidateEntities, CandidateEntity candidateEntity) throws Exception {
                        candidateEntities.add(candidateEntity);
                    }
                })
                .doOnSuccess(new Consumer<List<CandidateEntity>>() {
                    @Override
                    public void accept(List<CandidateEntity> candidateEntityList) throws Exception {
                        insertCandidateInfoList(candidateEntityList);
                        updateBatchRegionInfoWithIpList(getIpList(candidateEntityList));
                    }
                });
    }

    public void updateBatchRegionInfoWithIpList(List<String> ipList) {

        getRegionList(buildRegionRequestParams(ipList).blockingGet())
                .toFlowable()
                .flatMap(new Function<Response<List<RegionEntity>>, Publisher<RegionEntity>>() {
                    @Override
                    public Publisher<RegionEntity> apply(Response<List<RegionEntity>> listResponse) throws Exception {
                        return Flowable.fromIterable(listResponse.body());
                    }
                })
                .map(new Function<RegionEntity, RegionEntity>() {
                    @Override
                    public RegionEntity apply(RegionEntity regionEntity) throws Exception {
                        String countryZh = getCountryByName("CN", regionEntity.getCountryCode());
                        String countryEn = getCountryByName("EN", regionEntity.getCountryCode());
                        regionEntity.setCountryEn(countryEn);
                        regionEntity.setCountryZh(countryZh);
                        regionEntity.setCountryPinyin(Pinyin.toPinyin(countryZh, ""));
                        regionEntity.setUpdateTime(System.currentTimeMillis());
                        regionEntity.setNodeAddress(NodeManager.getInstance().getCurNodeAddress());
                        return regionEntity;
                    }
                })
                .doOnNext(new Consumer<RegionEntity>() {
                    @Override
                    public void accept(RegionEntity regionEntity) throws Exception {
                        EventPublisher.getInstance().sendUpdateCandidateRegionInfoEvent(regionEntity);
                    }
                })
                .toList()
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<List<RegionEntity>>() {
                    @Override
                    public void accept(List<RegionEntity> regionEntities) throws Exception {
                        RegionInfoDao.insertBatchRegionInfo(buildRegionInfoEntityList(regionEntities));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }

    public Single<CandidateEntity> getCandidateDetail(String nodeId) {

        return Single
                .fromCallable(new Callable<List<CandidateEntity>>() {
                    @Override
                    public List<CandidateEntity> call() throws Exception {
                        return getCandidateEntity(nodeId);
                    }
                })
                .toFlowable()
                .flatMap(new Function<List<CandidateEntity>, Publisher<CandidateEntity>>() {
                    @Override
                    public Publisher<CandidateEntity> apply(List<CandidateEntity> candidateEntities) throws Exception {
                        return Flowable.fromIterable(candidateEntities);
                    }
                })
                .firstElement()
                .toSingle()
                .map(new Function<CandidateEntity, CandidateEntity>() {
                    @Override
                    public CandidateEntity apply(CandidateEntity candidateEntity) throws Exception {
                        candidateEntity.setVotedNum(VoteManager.getInstance().getCandidateTicketCount(candidateEntity.getCandidateId()).blockingGet());
                        return candidateEntity;
                    }
                })
                .map(new Function<CandidateEntity, CandidateEntity>() {
                    @Override
                    public CandidateEntity apply(CandidateEntity candidateEntity) throws Exception {
                        RegionInfoEntity regionInfo = RegionInfoDao.getRegionInfoWithIp(candidateEntity.getHost());
                        if (regionInfo != null) {
                            candidateEntity.setRegionEntity(regionInfo.toRegionEntity());
                        }
                        return candidateEntity;
                    }
                });
    }

    private void insertCandidateInfoList(List<CandidateEntity> candidateEntityList) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return CandidateInfoDao.insertCandidateInfoList(getCandidateInfoEntityList(candidateEntityList));
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }

    private List<CandidateInfoEntity> getCandidateInfoEntityList(List<CandidateEntity> candidateEntityList) {
        return Flowable
                .fromIterable(candidateEntityList)
                .map(new Function<CandidateEntity, CandidateInfoEntity>() {
                    @Override
                    public CandidateInfoEntity apply(CandidateEntity candidateEntity) throws Exception {
                        return candidateEntity.buildCandidateInfo();
                    }
                })
                .toList()
                .blockingGet();
    }

    private List<String> getIpList(List<CandidateEntity> candidateEntityList) {
        return Flowable.fromIterable(candidateEntityList)
                .filter(new Predicate<CandidateEntity>() {
                    @Override
                    public boolean test(CandidateEntity candidateEntity) throws Exception {
                        return candidateEntity.isInvalidHost();
                    }
                })
                .map(new Function<CandidateEntity, String>() {
                    @Override
                    public String apply(CandidateEntity candidateEntity) throws Exception {
                        return candidateEntity.getHost();
                    }
                })
                .toList()
                .blockingGet();
    }

    private List<CandidateEntity> getCandidateListFromNet() {
        Log.e(TAG, "start getCandidateListFromNet");
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        CandidateContract candidateContract = CandidateContract.load(web3j,
                new ReadonlyTransactionManager(web3j, CandidateContract.CONTRACT_ADDRESS),
                new DefaultWasmGasProvider());
        String candidateListResp = null;
        try {
            candidateListResp = candidateContract.CandidateList().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "end getCandidateListFromNet");
        return JSONUtil.parseTwoDimensionArray(candidateListResp, CandidateEntity.class);
    }

    private List<CandidateEntity> getVerifiersListFromNet() {
        Log.e(TAG, "start getVerifiersListFromNet");
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        CandidateContract candidateContract = CandidateContract.load(web3j,
                new ReadonlyTransactionManager(web3j, CandidateContract.CONTRACT_ADDRESS),
                new DefaultWasmGasProvider());
        String candidateListResp = null;
        try {
            candidateListResp = candidateContract.VerifiersList().send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e(TAG, "end getVerifiersListFromNet");
        return JSONUtil.parseArray(candidateListResp, CandidateEntity.class);
    }

    private List<CandidateEntity> getCandidateEntity(String nodeId) {
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        CandidateContract candidateContract = CandidateContract.load(
                web3j, new ReadonlyTransactionManager(web3j, CandidateContract.CONTRACT_ADDRESS), new DefaultWasmGasProvider());

        String candidateDetailsResp = null;
        try {
            candidateDetailsResp = candidateContract.CandidateDetails(nodeId).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONUtil.parseArray(candidateDetailsResp, CandidateEntity.class);
    }

    private List<RegionInfoEntity> buildRegionInfoEntityList(List<RegionEntity> regionEntities) {
        return Flowable.fromIterable(regionEntities)
                .map(new Function<RegionEntity, RegionInfoEntity>() {
                    @Override
                    public RegionInfoEntity apply(RegionEntity regionEntity) throws Exception {
                        return regionEntity.buildRegionInfoEntity();
                    }
                }).toList().blockingGet();
    }

    private Single<Response<List<RegionEntity>>> getRegionList(String json) {
        return HttpClient.getInstance()
                .createService(RegionService.class)
                .getRegionInfoList(RequestBody.create(MediaType.parse("application/json:charset=utf-8"), json));
    }

    private Single<String> buildRegionRequestParams(List<String> ipList) {
        return Flowable.fromIterable(ipList)
                .map(new Function<String, GetRegionInfoRequestEntity>() {
                    @Override
                    public GetRegionInfoRequestEntity apply(String ip) throws Exception {
                        return new GetRegionInfoRequestEntity(ip, TextUtils.join(",", new String[]{"query", "countryCode"}), "");
                    }
                })
                .toList()
                .map(new Function<List<GetRegionInfoRequestEntity>, String>() {
                    @Override
                    public String apply(List<GetRegionInfoRequestEntity> getRegionInfoRequestEntities) throws Exception {
                        return JSONUtil.toJSONString(getRegionInfoRequestEntities);
                    }
                });
    }

    private String getCountryByName(String name, String countryCode) {
        JSONObject object = getRegionObject(countryCode);
        if (object != null) {
            try {
                return object.getString(name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private JSONObject getRegionObject(String countryCode) {
        String regionJson = FileUtil.getAssets(App.getContext(), "region.json");
        JSONObject object = null;
        try {
            JSONObject jsonObject = new JSONObject(regionJson);
            object = jsonObject.getJSONObject(countryCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }
}
