package com.juzix.wallet.db.sqlite;

import com.juzix.wallet.db.entity.CandidateInfoEntity;

import io.realm.Realm;

public class CandidateInfoDao {


    public static boolean insertCandidateInfo(CandidateInfoEntity candidateInfoEntity) {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(candidateInfoEntity);
            realm.commitTransaction();
            return true;
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return false;
    }

    public static CandidateInfoEntity getCandidateInfoById(String candidateId) {
        Realm realm = null;
        CandidateInfoEntity candidateInfoEntity = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            CandidateInfoEntity infoEntity = realm.where(CandidateInfoEntity.class)
                    .equalTo("candidateId", candidateId)
                    .findFirst();
            if (infoEntity != null) {
                candidateInfoEntity = realm.copyFromRealm(infoEntity);
            }
            realm.commitTransaction();
        } catch (Exception exp) {
            if (realm != null) {
                realm.cancelTransaction();
            }
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return candidateInfoEntity;
    }


}
