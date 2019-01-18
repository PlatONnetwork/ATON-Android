package com.juzix.wallet.component.ui.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.juzhen.framework.util.RandomUtil;
import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.db.entity.TestD;
import com.juzix.wallet.db.entity.TestEntity;

import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

public class TestActivity extends BaseActivity implements View.OnClickListener{

    private final static String TAG = TestActivity.class.getSimpleName();

    public static void actionStart(Context context){
        context.startActivity(new Intent(context, TestActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn_add).setOnClickListener(this);
        showList();
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.color_1b2137;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                add();
                showList();
                break;
        }
    }

    private void add(){
        ArrayList<TestEntity> list = getList();
        ArrayList<TestEntity> newList = new ArrayList<>();
        ArrayList<TestD>      ds      = new ArrayList<TestD>();
        ds.add(new TestD.Builder().name("abcd").uuid(String.valueOf(RandomUtil.randInt(200, 500))).build());
        ds.add(new TestD.Builder().name("abcd").uuid("1").build());
        newList.add(new TestEntity.Builder().uuid(list.get(0).getUuid()).name(String.valueOf(RandomUtil.randInt(200))).d(ds).build());
        newList.add(new TestEntity.Builder().uuid(list.get(1).getUuid()).name(String.valueOf(RandomUtil.randInt(200))).d(ds).build());
        newList.add(new TestEntity.Builder().uuid(String.valueOf(RandomUtil.randInt(200))).name(String.valueOf(RandomUtil.randInt(200))).d(ds).build());
        insertOrUpdateBatch(newList);
    }

    private void showList(){
        ArrayList<TestEntity> list = getList();
        StringBuilder builder = new StringBuilder();
        for (TestEntity entity : list){
            builder.append("\n" + entity.getUuid() + ":\t" + entity.getName());
            for (TestD d : entity.getOwnerArrayList()){
                builder.append("\t\t" + d.getUuid() + ":\t" + d.getName());
            }
        }
        TextView tvContent = findViewById(R.id.tv_content);
        tvContent.setText(builder.toString());
    }


    private boolean insertOrUpdateBatch(ArrayList<TestEntity> entities) {
        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(entities);
            realm.commitTransaction();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            realm.cancelTransaction();
            return false;
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private ArrayList<TestEntity> getList() {

        ArrayList<TestEntity> list  = new ArrayList<>();
        Realm            realm = null;
        try {
            realm = Realm.getDefaultInstance();
            RealmResults<TestEntity> results = realm.where(TestEntity.class).findAll();
            list.addAll(realm.copyFromRealm(results));
        } catch (Exception exp) {
            exp.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
        return list;
    }
}
