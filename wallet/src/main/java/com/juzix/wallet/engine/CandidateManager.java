package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.juzhen.framework.network.Headers;
import com.juzhen.framework.network.NoHttp;
import com.juzhen.framework.network.RequestMethod;
import com.juzhen.framework.network.rest.Request;
import com.juzhen.framework.network.rest.Response;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.db.entity.RegionInfoEntity;
import com.juzix.wallet.db.sqlite.RegionInfoDao;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.entity.CandidateExtraEntity;
import com.juzix.wallet.protocol.entity.GetRegionInfoRequestEntity;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.LanguageUtil;

import org.json.JSONArray;
import org.json.JSONObject;
import org.web3j.platon.contracts.CandidateContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultWasmGasProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * @author matrixelement
 */
public class CandidateManager {

    private static final String REGION_NAME              = "region.json";
    private static final long   UPDATE_REGION_TIME_MILLS = 60 * 1000;

    private CandidateManager() {

    }

    public static CandidateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public ArrayList<CandidateEntity> getCandidateList(){
        Web3j                      web3j             = Web3jManager.getInstance().getWeb3j();
        CandidateContract candidateContract = CandidateContract.load( web3j,
                new ReadonlyTransactionManager(web3j, CandidateContract.CONTRACT_ADDRESS),
                new DefaultWasmGasProvider());
        ArrayList<CandidateEntity> candidateEntities = new ArrayList<>();
        try {
            boolean isEnglish = true;
            Locale locale = LanguageUtil.getLocale(App.getContext());
            if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
                isEnglish = false;
            }
            String             candidateListResp = candidateContract.CandidateList().send();
            Set<String>        ipSet            = new HashSet<>();
            List<String>       candidateIdList   = new ArrayList<>();
            List<CandidateDto> candidateDtoList  = JSONUtil.parseArray(candidateListResp, CandidateDto.class);
            for (CandidateDto candidateDto : candidateDtoList){
                CandidateEntity entity = toCandidateEntity(candidateDto);
                CandidateExtraEntity extraEntity = JSONUtil.parseObject(entity.getExtra(), CandidateExtraEntity.class);
                if (extraEntity != null){
                    entity.setAvatar(extraEntity.getNodePortrait());
                    entity.setCandidateExtraEntity(extraEntity);
                }
                entity.setRegion(App.getContext().getString(isEnglish ? R.string.unknownRegionEn : R.string.unknownRegion));
                candidateEntities.add(entity);
                ipSet.add(entity.getHost());
                candidateIdList.add(entity.getCandidateId());
            }
            Map<String, List<String>> ticketIds = TicketManager.getInstance().getBatchCandidateTicketIds(TextUtils.join(":", candidateIdList));
            List<RegionInfoEntity> regionInfoEntityList = RegionInfoDao.getInstance().getRegionInfoListWithIpList(new ArrayList<>(ipSet));
            for (CandidateEntity candidateEntity : candidateEntities){
                String candidateId = candidateEntity.getCandidateId();
                if (ticketIds.containsKey(candidateId) && ticketIds.get(candidateId) != null){
                    candidateEntity.setVotedNum(ticketIds.get(candidateId).size());
                }
                String host = candidateEntity.getHost();
                if (!TextUtils.isEmpty(host) && !regionInfoEntityList.isEmpty()){
                    for (RegionInfoEntity entity : regionInfoEntityList ){
                        if (host.equals(entity.getIp())){
                            String region = isEnglish ? entity.getCountryEn() : entity.getCountryZh();
                            try {
                                if (!TextUtils.isEmpty(region)){
                                    candidateEntity.setRegion(region);
                                    if (ipSet.contains(host)) {
                                        ipSet.remove(host);
                                    }
                                }else {
                                    if (System.currentTimeMillis() - entity.getUpdateTime() > UPDATE_REGION_TIME_MILLS){
                                        if (ipSet.contains(host)) {
                                            ipSet.remove(host);
                                        }
                                    }
                                }
                            }catch (Exception exp){
                                exp.printStackTrace();
                            }
                            break;
                        }
                    }
                }
            }
            if (!ipSet.isEmpty()){
                new Thread(){
                    @Override
                    public void run() {
                        updateBatchRegionInfoWithIpList(ipSet);
                    }
                }.start();
            }
        }catch (Exception exp){
            exp.printStackTrace();
        }
        return candidateEntities;
    }

    private CandidateEntity toCandidateEntity(CandidateDto dto) {
        return new CandidateEntity.Builder()
                .deposit(dto.getDeposit().toString())
                .blockNumber(dto.getBlockNumber().longValue())
                .owner(dto.getOwner())
                .txIndex(dto.getTxIndex().intValue())
                .candidateId(dto.getCandidateId())
                .from(dto.getFrom())
                .fee(dto.getFee().intValue())
                .host(dto.getHost())
                .port(dto.getPort())
                .extra(dto.getExtra())
                .build();
    }

    public CandidateEntity getCandidateDetail(String nodeId){
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        CandidateContract candidateContract = CandidateContract.load(
                web3j, new ReadonlyTransactionManager(web3j, CandidateContract.CONTRACT_ADDRESS), new DefaultWasmGasProvider());
        try {
            boolean isEnglish = true;
            Locale locale = LanguageUtil.getLocale(App.getContext());
            if (Locale.CHINESE.getLanguage().equals(locale.getLanguage())) {
                isEnglish = false;
            }
            String                  candidateDetailsResp    = candidateContract.CandidateDetails(nodeId).send();
            CandidateEntity entity = toCandidateEntity(JSONUtil.parseObject(candidateDetailsResp, CandidateDto.class));
            CandidateExtraEntity extraEntity = JSONUtil.parseObject(entity.getExtra(), CandidateExtraEntity.class);
            if (extraEntity != null){
                entity.setAvatar(extraEntity.getNodePortrait());
                entity.setCandidateExtraEntity(extraEntity);
            }
            entity.setVotedNum(TicketManager.getInstance().getCandidateTicketIdsCounter(entity.getCandidateId()));
            RegionInfoEntity regionInfoEntity = RegionInfoDao.getInstance().getRegionInfoWithIp(entity.getHost());
            if (regionInfoEntity != null){
                entity.setRegion(isEnglish ? regionInfoEntity.getCountryEn() : regionInfoEntity.getCountryZh());
            }else {
                entity.setRegion(App.getContext().getString(isEnglish ? R.string.unknownRegionEn : R.string.unknownRegion));
            }
            return entity;
        }catch (Exception exp){
            exp.printStackTrace();
            return null;
        }
    }

    public int getNodeIcon(String nodePortrait) {
        int resId = -1;
        try {
            resId = RUtils.drawable(App.getContext().getResources().getStringArray(R.array.node_avatar)[Integer.parseInt(nodePortrait) - 1]);
        }catch (Exception exp){
        }
        return resId < 0 ? R.drawable.icon_default_coin : resId;
    }

    private void updateBatchRegionInfoWithIpList(Set<String> ips) {
        final String query = "query";
        final String field = "countryCode";
        ArrayList<GetRegionInfoRequestEntity> entityArrayList = new ArrayList<>();
        for (String ip : ips){
            entityArrayList.add(new GetRegionInfoRequestEntity(ip, field + "," + query, ""));
        }
        Request<JSONArray>  request = NoHttp.createJsonArrayRequest(Constants.URL.IP_URL, RequestMethod.POST);
        request.setConnectTimeout(10 * 1000);
        request.setReadTimeout(10 * 1000);
        request.setDefineRequestBody(JSON.toJSONString(entityArrayList), Headers.HEAD_VALUE_CONTENT_TYPE_JSON);
        Response<JSONArray> response = NoHttp.startRequestSync(request);
        if (!response.isSucceed()){
            return ;
        }
        JSONArray jsonArray = response.get();
        if (jsonArray == null){
            return ;
        }
        long updateTime = System.currentTimeMillis();
        ArrayList<RegionInfoEntity> arrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject  = jsonArray.getJSONObject(i);
                if (!jsonObject.has(query)){
                    continue;
                }
                String countryCode = "";
                if (jsonObject.has(field)){
                    countryCode = jsonObject.getString(field);
                }
                String queryValue = jsonObject.getString(query);
                arrayList.add(new RegionInfoEntity.Builder()
                        .uuid(queryValue)
                        .ip(queryValue)
                        .countryCode(countryCode)
                        .updateTime(updateTime)
                        .build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (arrayList.isEmpty()){
            return;
        }
        String regionJson = FileUtil.getStringFromAssets(App.getContext(), REGION_NAME);
        if (TextUtils.isEmpty(regionJson)){
            return;
        }
        try {
            JSONObject jsonObject  = new JSONObject(regionJson);
            for (RegionInfoEntity regionInfoEntity : arrayList){
                try {
                    String countryCode = regionInfoEntity.getCountryCode();
                    if (TextUtils.isEmpty(countryCode) || !jsonObject.has(countryCode)){
                        continue;
                    }
                    JSONObject obj = jsonObject.getJSONObject(countryCode);
                    if (obj != null){
                        regionInfoEntity.setCountryZh(obj.getString("CN"));
                        regionInfoEntity.setCountryEn(obj.getString("EN"));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        RegionInfoDao.getInstance().insertBatchRegionInfo(arrayList);
    }

    private static class InstanceHolder {
        private static volatile CandidateManager INSTANCE = new CandidateManager();
    }
}
