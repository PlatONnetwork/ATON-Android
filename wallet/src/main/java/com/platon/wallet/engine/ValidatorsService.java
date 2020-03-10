package com.platon.wallet.engine;

import com.platon.wallet.db.entity.VerifyNodeEntity;
import com.platon.wallet.db.sqlite.VerifyNodeDao;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ValidatorsService {

    /**
     * 插入实体
     *
     * @param entity
     * @return
     */
    public static Single<Boolean> insertVerifyNode(VerifyNodeEntity entity) {
        return Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return VerifyNodeDao.insertVerifyNode(entity);
            }
        }).subscribeOn(Schedulers.io());

    }


//    /**
//     * 插入列表
//     *
//     * @param entityList
//     * @return
//     */
//    public static Single<Boolean> insertVerifyNodeList(List<VerifyNode> entityList) {
//        return Flowable.fromIterable(entityList)
//                .map(new Function<VerifyNode, VerifyNodeEntity>() {
//                    @Override
//                    public VerifyNodeEntity apply(VerifyNode verifyNode) throws Exception {
//                        return verifyNode.toVerifyNodeEntity();
//                    }
//                }).toList()
//                .map(new Function<List<VerifyNodeEntity>, Boolean>() {
//                    @Override
//                    public Boolean apply(List<VerifyNodeEntity> entityList) throws Exception {
//                        return VerifyNodeDao.insertVerifyNodeList(entityList);
//                    }
//                });
//
//    }


}
